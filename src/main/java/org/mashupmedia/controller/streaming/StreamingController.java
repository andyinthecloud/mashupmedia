package org.mashupmedia.controller.streaming;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.view.MediaItemImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/streaming")
public class StreamingController {

	public static final MediaContentType[] ESSENTIAL_MUSIC_STREAMING_CONTENT_TYPES = new MediaContentType[] {
			MediaContentType.MP3 };

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private PlaylistManager playlistManager;

	@RequestMapping(value = "/media/{mediaItemId}/{mediaContentType}", method = { RequestMethod.GET,
			RequestMethod.HEAD })
	public void getMediaStream(@PathVariable("mediaItemId") Long mediaItemId,
			@PathVariable(value = "mediaContentType") String mediaContentTypeValue, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		MediaType mediaType = mediaItem.getMediaType();
		if (mediaType == MediaType.PHOTO) {
			Photo photo = (Photo) mediaItem;
			getImageStream(photo, mediaContentTypeValue, request, response);
			return;
		}

		MediaEncoding mediaEncoding = getMediaEncoding(mediaItem, mediaContentTypeValue);
		prepareModelAndView(mediaItem, mediaEncoding, request, response);
	}

	@RequestMapping(value = "/playlist/{playlistTypeValue}/{mediaContentType}/{timestamp}", method = {
			RequestMethod.GET, RequestMethod.HEAD })
	@ResponseStatus(HttpStatus.OK)
	public void getCurrentPlaylistStream(@PathVariable(value = "playlistTypeValue") String playlistTypeValue,
			@PathVariable(value = "mediaContentType") String mediaContentTypeValue,
			@PathVariable(value = "timestamp") String timestamp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PlaylistType playlistType = PlaylistHelper.getPlaylistType(playlistTypeValue);
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(playlistType);
		prepareModelAndView(playlist, mediaContentTypeValue, request, response);

	}

	protected void getImageStream(Photo photo, String imageTypeValue, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ImageType imageType = ImageHelper.getImageType(imageTypeValue);

		String filePath = photo.getPath();
		String defaultImagePath = MediaItemImageView.IMAGE_PATH_DEFAULT_PHOTO;
		if (imageType == ImageType.THUMBNAIL) {
			filePath = photo.getThumbnailPath();
			defaultImagePath = MediaItemImageView.IMAGE_PATH_DEFAULT_PHOTO_THUMBNAIL;
		} else if (imageType == ImageType.WEB_OPTIMISED) {
			filePath = photo.getWebOptimisedImagePath();
		}

		filePath = StringUtils.trimToEmpty(filePath);

		File mediaFile = new File(filePath);
		long lastModified = mediaFile.lastModified();
		if (mediaFile.isFile()) {
			StreamingMediaHandler.fromMediaItem(playlistManager, photo, lastModified).with(request).with(response).serveResource();
			return;
		}

		// No image found so showing default
		WebHelper.writeResourceToResponse(defaultImagePath, request, response);
	}

	protected MediaEncoding getMediaEncoding(MediaItem mediaItem, String mediaContentTypeValue) {

		mediaContentTypeValue = StringUtils.trimToEmpty(mediaContentTypeValue);
		if (StringUtils.isBlank(mediaContentTypeValue)) {
			MediaEncoding mediaEncoding = mediaItem.getBestMediaEncoding();
			return mediaEncoding;
		}

		Collection<MediaEncoding> mediaEncodings = mediaItem.getMediaEncodings();
		if (mediaEncodings == null || mediaEncodings.isEmpty()) {
			return null;
		}

		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		for (MediaEncoding mediaEncoding : mediaEncodings) {
			if (mediaEncoding.getMediaContentType() == mediaContentType) {
				return mediaEncoding;
			}
		}

		return mediaItem.getBestMediaEncoding();
	}

	protected void prepareModelAndView(Playlist playlist, String mediaContentTypeValue, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		int offset = 0;
		boolean isEndOfPlaylist = false;

		MediaEncoding mediaEncoding = null;

		while (isEndOfPlaylist == false) {
			PlaylistMediaItem playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist,
					offset, false);
			if (playlistMediaItem == null || playlistMediaItem.getId() == 0) {
				isEndOfPlaylist = true;
				continue;
			}

			MediaItem mediaItem = playlistMediaItem.getMediaItem();
			if (mediaEncoding == null) {
				mediaEncoding = getMediaEncoding(mediaItem, mediaContentTypeValue);
			}

			File mediaFile = getMediaFile(mediaItem, mediaEncoding);
			if (mediaFile != null) {
				mediaItems.add(mediaItem);	
			}			

			offset++;
		}

		long lastModified = playlist.getUpdatedOn().getTime();
		StreamingMediaHandler.fromMediaItems(playlistManager, mediaItems, lastModified).with(request).with(response).serveResource();

	}

	private File getMediaFile(MediaItem mediaItem, MediaEncoding mediaEncoding) {
		File mediaFile = FileHelper.getMediaFile(mediaItem, mediaEncoding);
		Path path = Paths.get(mediaFile.getAbsolutePath());
		if (!Files.exists(path)) {
			return null;
		}
		
		return mediaFile;
	}

	protected void prepareModelAndView(MediaItem mediaItem, MediaEncoding mediaEncoding, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (mediaEncoding == null) {
			return;
		}

		Library library = mediaItem.getLibrary();

		if (library.isRemote()) {
			Location location = library.getLocation();
			String path = location.getPath();
			path = LibraryHelper.getRemoteStreamingPath(path);
			long remoteMediaItemId = NumberUtils.toLong(mediaItem.getPath());

			if (StringUtils.isNotBlank(path) && remoteMediaItemId > 0) {
				response.encodeRedirectURL(path + "/" + remoteMediaItemId);
				return;
			}
		}

		File mediaFile = getMediaFile(mediaItem, mediaEncoding);
		long lastModified = mediaFile.lastModified();

		StreamingMediaHandler.fromMediaItem(playlistManager, mediaItem, lastModified).with(request).with(response).serveResource();
	}

}
