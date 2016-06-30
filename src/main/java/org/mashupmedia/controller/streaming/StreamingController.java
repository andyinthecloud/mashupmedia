package org.mashupmedia.controller.streaming;

import java.io.File;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/streaming")
public class StreamingController {

	public static final MediaContentType[] ESSENTIAL_MUSIC_STREAMING_CONTENT_TYPES = new MediaContentType[] {
			MediaContentType.MP3 };

	@Autowired
	private MediaManager mediaManager;

	@RequestMapping(value = "/media/{mediaItemId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public void getMediaStream(@PathVariable("mediaItemId") Long mediaItemId,
			@RequestParam(value = "mediaContentType", required = false) String mediaContentTypeValue,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		MediaEncoding mediaEncoding = getMediaContentType(mediaItem, mediaContentTypeValue);
		prepareModelAndView(mediaItem, mediaEncoding, request, response);
	}

	protected MediaEncoding getMediaContentType(MediaItem mediaItem, String mediaContentTypeValue) {

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

		return null;
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

		File mediaFile = FileHelper.getMediaFile(mediaItem, mediaEncoding);
		if (!mediaFile.exists()) {
			mediaFile = new File(mediaItem.getPath());
		}

		StreamingMediaHandler.fromFile(mediaFile).with(request).with(response).serveResource();

	}

}
