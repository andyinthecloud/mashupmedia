package org.mashupmedia.controller.rest.encode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.exception.MediaItemEncodeException.EncodeExceptionType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
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

	enum EncodeMessageType {
		ERROR, INFO, WARNING
	}
	
	private MediaContentType musicEncodingMediaContentType = MediaContentType.MP3;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private MusicManager musicManager;

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@RequestMapping(value = "/media-item", method = RequestMethod.POST)
	public String encodeMediaItem(@RequestParam(value = "id") long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			return "encode.message.media-item-not-found";
		}
		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>();
		encodeMediaItem(messageKeys, musicEncodingMediaContentType, mediaItem);
		
		String messageKey = getMostImportantMessageKey(messageKeys);		
		String message = MessageHelper.getMessage(messageKey);
		return message;
	}

	protected String getMostImportantMessageKey(Map<EncodeMessageType, String> messageKeys) {
		if (messageKeys == null || messageKeys.isEmpty()) {
			return null;
		}
		
		String messageKey = messageKeys.get(EncodeMessageType.ERROR);
		if (StringUtils.isNotEmpty(messageKey)) {
			return messageKey;
		}
		
		messageKey = messageKeys.get(EncodeMessageType.WARNING);
		if (StringUtils.isNotEmpty(messageKey)) {
			return messageKey;
		}
		
		messageKey = messageKeys.get(EncodeMessageType.INFO);
		if (StringUtils.isNotEmpty(messageKey)) {
			return messageKey;
		}
		
		return null;
	}

	@RequestMapping(value = "/music-album", method = RequestMethod.POST)
	public String encodeMusicAlbum(@RequestParam(value = "id") long albumId) {
		Album album = musicManager.getAlbum(albumId);
		
		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>();
		encodeMusicAlbum(album, messageKeys, musicEncodingMediaContentType);

		String messageKey = getMostImportantMessageKey(messageKeys);
		String message = MessageHelper.getMessage(messageKey);
		return message;
	}
	
	@RequestMapping(value = "/music-artist", method = RequestMethod.POST)
	public String encodeMusicArtist(@RequestParam(value = "id") long albumId) {
		
		Artist artist = musicManager.getArtist(albumId);
		if (artist == null) {
			return MessageHelper.getMessage("encode.message.media-item-not-found");
		}
		
		List<Album> albums = artist.getAlbums();
		for (Album album : albums) {
			if (album == null) {
				continue;
			}
			
		}
		
		Album album = musicManager.getAlbum(albumId);
		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>(); 
		encodeMusicAlbum(album, messageKeys, musicEncodingMediaContentType);

		String messageKey = getMostImportantMessageKey(messageKeys);
		String message = MessageHelper.getMessage(messageKey);
		return message;
	}
	
	protected void encodeMusicAlbum(Album album, Map<EncodeMessageType, String> messageKeys, MediaContentType mediaContentType) {
		if (messageKeys == null) {
			messageKeys = new HashMap<EncodeMessageType, String>();
		}
		
		if (album == null) {
			messageKeys.put(EncodeMessageType.ERROR, "encode.message.media-item-not-found");
			return;
		}
		
		List<Song> songs = album.getSongs();
		for (Song song : songs) {
			encodeMediaItem(messageKeys, mediaContentType, song);
		}
	}
	

	protected void encodeMediaItem(Map<EncodeMessageType, String> messageKeys, MediaContentType mediaContentType, MediaItem mediaItem) {
		if (mediaContentType == null || mediaContentType == MediaContentType.UNSUPPORTED) {
			messageKeys.put(EncodeMessageType.ERROR, "encode.message.unsupported-format");
			return;
		}

		try {
			encodeMediaItemTaskManager.processMediaItemForEncoding(mediaItem, mediaContentType);
		} catch (MediaItemEncodeException e) {
			EncodeExceptionType encodeExceptionType = e.getEncodeExceptionType();
			if (encodeExceptionType == EncodeExceptionType.ENCODER_NOT_CONFIGURED) {
				messageKeys.put(EncodeMessageType.WARNING, "encode.message.not-installed");
			} else if (encodeExceptionType == EncodeExceptionType.UNSUPPORTED_ENCODING_FORMAT) {
				messageKeys.put(EncodeMessageType.ERROR, "encode.message.unsupported-format");
			} else {
				messageKeys.put(EncodeMessageType.ERROR, "encode.message.error");
			}
			return;
		}

		messageKeys.put(EncodeMessageType.INFO, "encode.message.queued");
	}

}
