package org.mashupmedia.controller.rest.encode;

import java.util.List;

import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.exception.MediaItemEncodeException.EncodeExceptionType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful/encode")
public class RestfulEncodeController {

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private MusicManager musicManager;

	
	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@RequestMapping(value = "/media-item", method = RequestMethod.POST)
	public String encodeMediaItem(@RequestParam(value = "id") long mediaItemId,
			@RequestParam(value = "mediaContentTypeValue") String mediaContentTypeValue) {
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			return "encode.message.media-item-not-found";
		}
		String messageKey = encodeMediaItem(mediaContentType, mediaItem);
		String message = MessageHelper.getMessage(messageKey);
		return message;
	}
	

	@RequestMapping(value = "/music-album", method = RequestMethod.POST)
	public String encodeMusicAlbum(@RequestParam(value = "id") long albumId,
			@RequestParam(value = "mediaContentTypeValue") String mediaContentTypeValue) {
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		Album album = musicManager.getAlbum(albumId);
		if (album == null) {
			return MessageHelper.getMessage("encode.message.media-item-not-found");
		}
		
		String messageKey = null;
		List<Song> songs = album.getSongs();
		for (Song song : songs) {
			messageKey = encodeMediaItem(mediaContentType, song);
		}
		
		String message = MessageHelper.getMessage(messageKey);
		return message;
	}
	
	
	protected String encodeMediaItem(MediaContentType mediaContentType, MediaItem mediaItem) {
		String messageKey = null;
		if (mediaContentType == null || mediaContentType == MediaContentType.UNSUPPORTED) {
			return "encode.message.unsupported-format ";
		}		
		
		try {
			encodeMediaItemTaskManager.processMediaItemForEncoding(mediaItem, mediaContentType);
		} catch (MediaItemEncodeException e) {
			EncodeExceptionType encodeExceptionType = e.getEncodeExceptionType();
			if (encodeExceptionType == EncodeExceptionType.ENCODER_NOT_CONFIGURED) {
				messageKey = "encode.message.not-installed";
			} else if (encodeExceptionType == EncodeExceptionType.UNSUPPORTED_ENCODING_FORMAT) {
				messageKey = "encode.message.unsupported-format";

			} else {
				messageKey = "encode.message.error";

			}
		}

		messageKey = "encode.message.queued";
		
		return messageKey;
	}

}
