package org.mashupmedia.controller.rest.playlist;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.web.restful.RestfulMediaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class AbstractRestfulPlaylistController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private MediaManager mediaManager;

	@RequestMapping(value = "/play/current", method = RequestMethod.GET)
	public RestfulMediaItem playCurrentUserMusicPlaylist(Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(getPlaylistType());

		PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(0, playlist);
		RestfulMediaItem restfulMediaItem = convertToRestfulMediaItem(playlistMediaItem);
		return restfulMediaItem;
	}

	protected abstract PlaylistType getPlaylistType();

	@RequestMapping(value = "/play/next", method = RequestMethod.GET)
	public RestfulMediaItem playNextSong(Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(getPlaylistType());
		PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(1, playlist);
		if (playlistMediaItem == null) {
			return null;
		}

		User user = AdminHelper.getLoggedInUser();
		playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);
		RestfulMediaItem restfulMediaItem = convertToRestfulMediaItem(playlistMediaItem);
		return restfulMediaItem;
	}

	@RequestMapping(value = "/play/previous", method = RequestMethod.GET)
	public RestfulMediaItem playPreviousSong(Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
		PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(-1, playlist);
		if (playlistMediaItem == null) {
			return null;
		}

		User user = AdminHelper.getLoggedInUser();
		playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);
		RestfulMediaItem restfulMediaItem = convertToRestfulMediaItem(playlistMediaItem);
		return restfulMediaItem;
	}

	protected PlaylistMediaItem getMediaItemFromPlaylist(int relativePosition, Playlist playlist) {
		PlaylistMediaItem playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist,
				relativePosition, true);
		if (playlistMediaItem == null || playlistMediaItem.getId() < 1) {
			return null;
		}

		return playlistMediaItem;
	}

	protected void savePlaylist(Playlist playlist) {
		playlistManager.savePlaylist(playlist);
		List<PlaylistMediaItem> accessiblePlaylistMediaItems = playlist.getPlaylistMediaItems();
		playlist.setAccessiblePlaylistMediaItems(accessiblePlaylistMediaItems);
	}

	protected abstract RestfulMediaItem convertToRestfulMediaItem(PlaylistMediaItem playlistMediaItem);

	@RequestMapping(value = "/save-playlist-name", method = RequestMethod.POST)
	public String savePlaylistName(@RequestParam(value = "id") String id, @RequestParam(value = "value") String value) {
		id = StringUtils.trimToEmpty(id);
		if (StringUtils.isEmpty(id)) {
			logger.info("Unable to save playlist name without id. Id = " + id);
			return value;
		}
		long playlistId = NumberUtils.toLong(id.replaceAll("\\D", ""));
		Playlist playlist = playlistManager.getPlaylist(playlistId);
		value = StringUtils.trimToEmpty(value);
		if (StringUtils.isEmpty(value)) {
			logger.info("Unable to save empty playlist name.");
			return value;

		}
		playlist.setName(value);
		playlistManager.savePlaylist(playlist);
		String savedPlaylistName = playlist.getName();
		return savedPlaylistName;
	}

	@RequestMapping(value = "/delete-playlist", method = RequestMethod.POST)
	public void deletePlaylist(@RequestParam("playlistId") Long playlistId, Model model) {
		playlistManager.deletePlaylist(playlistId);
	}

	@RequestMapping(value = "/save-playlist", method = RequestMethod.POST)
	public RestfulMediaItem savePlaylist(@RequestParam("playlistId") Long playlistId,
			@RequestParam(value = "mediaItemIds[]", required = false) Long[] mediaItemsIds, Model model) {

		Playlist playlist = playlistManager.getPlaylist(playlistId);
		RestfulMediaItem restfulMediaItem = processSavePlaylist(playlist, mediaItemsIds);
		return restfulMediaItem;
	}

	@RequestMapping(value = "/play", method = RequestMethod.POST)
	public RestfulMediaItem playingMediaItem(@RequestParam("playlist") Long playlistId,
			@RequestParam(value = "mediaItemId") Long mediaItemId, Model model) {

		Playlist playlist = playlistManager.getPlaylist(playlistId);

		if (mediaItemId == 0) {
			throw new MashupMediaRuntimeException("Unable to play media item. mediaItemId = " + mediaItemId);
		}

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getPlaylistMediaItem(playlist, mediaItemId);
		if (playlistMediaItem == null) {
			throw new MashupMediaRuntimeException("Unable to play media item. mediaItemId = " + mediaItemId);
		}

		User user = AdminHelper.getLoggedInUser();
		playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);

		RestfulMediaItem restfulMediaItem = convertToRestfulMediaItem(playlistMediaItem);
		return restfulMediaItem;
	}

	@RequestMapping(value = "/new-playlist", method = RequestMethod.POST)
	public Long newPlaylist(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "mediaItemIds[]", required = false) Long[] mediaItemsIds,

			Model model) {

		Playlist playlist = new Playlist();

		name = StringUtils.trimToEmpty(name);
		if (StringUtils.isEmpty(name)) {
			name = MessageHelper.getMessage("playlist.new.name");
		}

		playlist.setName(name);
		playlist.setPlaylistType(getPlaylistType());
		processSavePlaylist(playlist, mediaItemsIds);
		return playlist.getId();

	}

	protected RestfulMediaItem processSavePlaylist(Playlist playlist, Long[] mediaItemsIds) {
		if (mediaItemsIds == null) {
			mediaItemsIds = new Long[0];
		}

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		for (Long mediaItemId : mediaItemsIds) {
			MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
			mediaItems.add(mediaItem);
		}

		PlaylistHelper.replacePlaylist(playlist, mediaItems);
		PlaylistHelper.initialiseCurrentlyPlaying(playlist);

		playlistManager.savePlaylist(playlist);
		PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(-1, playlist);

		RestfulMediaItem restfulMediaItem = convertToRestfulMediaItem(playlistMediaItem);
		return restfulMediaItem;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public RestfulMediaItem playingMediaItem(@RequestParam("seconds") long seconds,
			Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return null;
		}

		RestfulMediaItem restfulMediaItem = null;
		PlaylistMediaItem currentPlaylistMediaItem = getMediaItemFromPlaylist(0, playlist);
		Song song = (Song) currentPlaylistMediaItem.getMediaItem();
		long trackLength = song.getTrackLength();
		if (seconds < trackLength) {
			restfulMediaItem = convertToRestfulMediaItem(currentPlaylistMediaItem);
			return restfulMediaItem;
		}
		
		PlaylistMediaItem nextPlaylistMediaItem = getMediaItemFromPlaylist(1, playlist);
		User user = AdminHelper.getLoggedInUser();
		playlistManager.saveUserPlaylistMediaItem(user, nextPlaylistMediaItem);
		restfulMediaItem = convertToRestfulMediaItem(nextPlaylistMediaItem);
		return restfulMediaItem;
	}
}
