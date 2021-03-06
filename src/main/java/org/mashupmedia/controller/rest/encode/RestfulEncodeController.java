package org.mashupmedia.controller.rest.encode;

import java.io.File;
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
import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper;
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

	private MediaContentType[] musicEncodingMediaContentTypes = new MediaContentType[] { MediaContentType.MP3 };
	
	private MediaContentType[] videoEncodingMediaContentTypes = new MediaContentType[] { MediaContentType.MP4 };

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private MusicManager musicManager;

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@RequestMapping(value = "/song", method = RequestMethod.POST)
	public String encodeSong(@RequestParam(value = "id") long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			return "encode.message.media-item-not-found";
		}
		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>();
		encodeMediaItem(messageKeys, musicEncodingMediaContentTypes, mediaItem);

		String messageKey = getMostImportantMessageKey(messageKeys);
		String message = MessageHelper.getMessage(messageKey, new String[] { WebHelper.getContextPath() });
		return message;
	}

	@RequestMapping(value = "/playlist", method = RequestMethod.POST)
	public String encodePlaylist(@RequestParam(value = "mediaItemId") long mediaItemId) {

		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
		
		if (playlist == null) {
			return "encode.message.media-item-not-found";
		}

		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>();
		boolean isUpToPositionNowPlaying = false;
		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			MediaItem mediaItem = playlistMediaItem.getMediaItem();
			if (!isUpToPositionNowPlaying && mediaItem.getId() == mediaItemId) {
				isUpToPositionNowPlaying = true;
			}
			
			if (!isUpToPositionNowPlaying) {
				continue;
			}

			MediaContentType[] mediaContentTypes = null;
			if (mediaItem.getClass().isAssignableFrom(Song.class)) {
				mediaContentTypes = musicEncodingMediaContentTypes;
			}

			encodeMediaItem(messageKeys, mediaContentTypes, mediaItem);
		}

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
		encodeMusicAlbum(album.getId(), messageKeys, musicEncodingMediaContentTypes);

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
			encodeMusicAlbum(album.getId(), messageKeys, musicEncodingMediaContentTypes);

		}

		String messageKey = getMostImportantMessageKey(messageKeys);
		String message = MessageHelper.getMessage(messageKey, new String[] { WebHelper.getContextPath() });
		return message;
	}

	protected void encodeMusicAlbum(Long albumId, Map<EncodeMessageType, String> messageKeys,
			MediaContentType[] mediaContentTypes) {
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
			encodeMediaItem(messageKeys, mediaContentTypes, song);
		}
	}

	protected void encodeMediaItem(Map<EncodeMessageType, String> messageKeys, MediaContentType[] mediaContentTypes,
			MediaItem mediaItem) {
		if (mediaContentTypes == null || mediaContentTypes.length == 0) {
			messageKeys.put(EncodeMessageType.ERROR, "encode.message.unsupported-format");
			return;
		}

		for (MediaContentType mediaContentType : mediaContentTypes) {
			try {
				// Only encode media item if missing
				if (MediaItemHelper.hasMediaEncoding(mediaItem, mediaContentType)) {
					File file =  FileHelper.getEncodedMediaFile(mediaItem, mediaContentType);
					if (file.exists()) {
						continue;
					}
				}

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
			}
		}

		messageKeys.put(EncodeMessageType.INFO, "encode.message.queued");
	}
	
	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public String encodeVideo(@RequestParam(value = "id") long videoId) {
		
		MediaItem mediaItem = mediaManager.getMediaItem(videoId);
		if (!mediaItem.getClass().isAssignableFrom(Video.class)) {
			return MessageHelper.getMessage("encode.message.error");
		}
		
		Map<EncodeMessageType, String> messageKeys = new HashMap<EncodeMessageType, String>();
		encodeMediaItem(messageKeys, videoEncodingMediaContentTypes, mediaItem);
		String messageKey = getMostImportantMessageKey(messageKeys);

		String message = MessageHelper.getMessage(messageKey, new String[] { WebHelper.getContextPath() });
		return message;
	}	

}
