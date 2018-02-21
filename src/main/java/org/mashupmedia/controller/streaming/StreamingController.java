package org.mashupmedia.controller.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.task.PlaylistTaskManager;
import org.mashupmedia.util.AdminHelper;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/streaming")
public class StreamingController {
	
	private Logger logger = Logger.getLogger(getClass());

	public static final MediaContentType[] ESSENTIAL_MUSIC_STREAMING_CONTENT_TYPES = new MediaContentType[] {
			MediaContentType.MP3 };

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private AdminManager adminManager;
	
	@Autowired
	private PlaylistTaskManager playlistTaskManager;

	@RequestMapping(value = "/media/{mediaItemId}/{mediaContentType}", method = { RequestMethod.GET })
	public void getMediaStream(@PathVariable("mediaItemId") Long mediaItemId,
			@PathVariable(value = "mediaContentType") String mediaContentTypeValue, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

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

		MediaType mediaType = mediaItem.getMediaType();
		if (mediaType == MediaType.PHOTO) {
			Photo photo = (Photo) mediaItem;
			writeImageStream(photo, mediaContentTypeValue, request, response);
			return;
		}

		MediaEncoding mediaEncoding = getMediaEncoding(mediaItem, mediaContentTypeValue);
		File mediaFile = getMediaFile(mediaItem, mediaEncoding);

		Long contentLength = mediaFile.length();
		String format = mediaItem.getFormat();
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(format);

		setResponse(response, mediaContentType, mediaItem.getDisplayTitle(), contentLength);
		WebHelper.writeFileToResponse(mediaFile, response);
	}

	protected void writeImageStream(Photo photo, String imageTypeValue, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ImageType imageType = ImageHelper.getImageType(imageTypeValue);

		String filePath = photo.getPath();
		String defaultImagePath = MediaItemImageView.IMAGE_PATH_DEFAULT_PHOTO;
		if (imageType == ImageType.WEB_OPTIMISED) {
			filePath = photo.getWebOptimisedImagePath();
		}

		filePath = StringUtils.trimToEmpty(filePath);

		File mediaFile = new File(filePath);

		String format = photo.getFormat();
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(format);
		response.setContentType(mediaContentType.getMimeContentType());

		if (mediaFile.exists()) {
			WebHelper.writeFileToResponse(mediaFile, response);
			return;
		}

		response.setContentType(MediaContentType.PNG.getMimeContentType());
		WebHelper.writeResourceToResponse(defaultImagePath, request, response);
	}

	private void setResponse(HttpServletResponse response, MediaContentType mediaContentType, String title,
			Long contentLength) throws IOException {
		if (contentLength != null && contentLength > 0) {
			response.setContentLength(contentLength.intValue());
		}
		response.setContentType(mediaContentType.getMimeContentType());
		response.setHeader("Keep-Alive", "timeout=60 max=100");
		response.setHeader("Content-Disposition", title);
		response.flushBuffer();

	}

	@RequestMapping(value = "/playlist/{playlistTypeValue}/{mediaContentType}/{timestamp}", method = {
			RequestMethod.GET })
	public void getCurrentPlaylistStream(@PathVariable(value = "playlistTypeValue") String playlistTypeValue,
			@PathVariable(value = "mediaContentType") String mediaContentTypeValue,
			@PathVariable(value = "timestamp") String timestamp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PlaylistType playlistType = PlaylistHelper.getPlaylistType(playlistTypeValue);
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(playlistType);
		User user = AdminHelper.getLoggedInUser();

		List<PlaylistMediaItem> playlistMediaItems = getPlaylistMediaItems(playlist);

		MediaItem firstMediaItem = PlaylistHelper.getFirstPlayListMediaItem(playlist).getMediaItem();
		String format = firstMediaItem.getFormat();
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(format);

//		long contentLength = getContentLength(playlistMediaItems, mediaContentTypeValue);
		setResponse(response, mediaContentType, playlist.getName(), null);

		List<FileInputStream> fileInputStreams = new ArrayList<FileInputStream>();
		try {
			PlaylistMediaItem previousPlaylistMediaItem = null;
			
			for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
				
				if (previousPlaylistMediaItem == null) {
					previousPlaylistMediaItem = playlistMediaItem;
				}
				user.setPlaylistMediaItem(previousPlaylistMediaItem);
				adminManager.updateUser(user);
				
				MediaItem mediaItem = playlistMediaItem.getMediaItem();
				MediaEncoding mediaEncoding = getMediaEncoding(mediaItem, mediaContentTypeValue);
				File mediaFile = getMediaFile(mediaItem, mediaEncoding);
				FileInputStream fileInputStream = new FileInputStream(mediaFile);
				
				// Copy file with audio tags removed
				File playlistFile = playlistTaskManager.getTemporaryPlaylistFile(playlist.getId(), mediaFile);
				FileInputStream playlistFileInputStream = new FileInputStream(playlistFile);
				fileInputStreams.add(playlistFileInputStream);
				IOUtils.copy(fileInputStream, response.getOutputStream());
				response.flushBuffer();
				playlistFileInputStream.close();
				FileHelper.deleteFile(playlistFile);
				previousPlaylistMediaItem = playlistMediaItem;
			}
		} finally {
			if (fileInputStreams == null || fileInputStreams.isEmpty()) {
				return;
			}
			for (FileInputStream fileInputStream : fileInputStreams) {
				try {
				fileInputStream.close();
				} catch (IOException e) {
					logger.error("Unable to close fileinputstream", e);
				}				
			}
		}
	}

	private long getContentLength(List<PlaylistMediaItem> playlistMediaItems, String mediaContentTypeValue) {
		long contentLength = 0;

		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return contentLength;
		}

		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			MediaItem mediaItem = playlistMediaItem.getMediaItem();
			MediaEncoding mediaEncoding = getMediaEncoding(mediaItem, mediaContentTypeValue);
			File mediaFile = getMediaFile(mediaItem, mediaEncoding);
			contentLength += mediaFile.length();
		}

		return contentLength;
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

	protected List<PlaylistMediaItem> getPlaylistMediaItems(Playlist playlist) throws Exception {

		List<PlaylistMediaItem> playlistMediaItems = new ArrayList<PlaylistMediaItem>();

		int offset = 0;
		boolean isEndOfPlaylist = false;

		while (isEndOfPlaylist == false) {
			PlaylistMediaItem playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist,
					offset, false);
			if (playlistMediaItem == null || playlistMediaItem.getId() == 0) {
				isEndOfPlaylist = true;
				continue;
			}

			playlistMediaItems.add(playlistMediaItem);
			offset++;
		}

		return playlistMediaItems;
	}

	private File getMediaFile(MediaItem mediaItem, MediaEncoding mediaEncoding) {
		File mediaFile = FileHelper.getMediaFile(mediaItem, mediaEncoding);
		Path path = Paths.get(mediaFile.getAbsolutePath());
		if (!Files.exists(path)) {
			return null;
		}

		return mediaFile;
	}

}
