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
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.WebHelper;
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

	@RequestMapping(value = "/song", method = RequestMethod.POST)
	public String encodeSong(@RequestParam(value = "id") long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			return "encode.message.media-item-not-found";
		}
		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>();
		encodeMediaItem(messageKeys, musicEncodingMediaContentType, mediaItem);

		String messageKey = getMostImportantMessageKey(messageKeys);
		String message = MessageHelper.getMessage(messageKey, new String[] { WebHelper.getContextPath() });
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
		encodeMusicAlbum(album.getId(), messageKeys, musicEncodingMediaContentType);

		String messageKey = getMostImportantMessageKey(messageKeys);

		String message = MessageHelper.getMessage(messageKey, new String[] { WebHelper.getContextPath() });
		return message;
	}

	@RequestMapping(value = "/music-artist", method = RequestMethod.POST)
	public String encodeMusicArtist(@RequestParam(value = "id") long artistId) {

		Artist artist = musicManager.getArtist(artistId);
		if (artist == null) {
			return MessageHelper.getMessage("encode.message.media-item-not-found");
		}

		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>();
		List<Album> albums = artist.getAlbums();
		for (Album album : albums) {
			encodeMusicAlbum(album.getId(), messageKeys, musicEncodingMediaContentType);

		}

		String messageKey = getMostImportantMessageKey(messageKeys);
		String message = MessageHelper.getMessage(messageKey, new String[] { WebHelper.getContextPath() });
		return message;
	}

	protected void encodeMusicAlbum(Long albumId, Map<EncodeMessageType, String> messageKeys,
			MediaContentType mediaContentType) {
		if (messageKeys == null) {
			messageKeys = new HashMap<EncodeMessageType, String>();
		}

		// Reinitialise album to get containing songs
		Album album = musicManager.getAlbum(albumId);
		if (album == null) {
			messageKeys.put(EncodeMessageType.ERROR, "encode.message.media-item-not-found");
			return;
		}
		
		List<Song> songs = album.getSongs();
		for (Song song : songs) {
			encodeMediaItem(messageKeys, mediaContentType, song);
		}
	}

	protected void encodeMediaItem(Map<EncodeMessageType, String> messageKeys, MediaContentType mediaContentType,
			MediaItem mediaItem) {
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
