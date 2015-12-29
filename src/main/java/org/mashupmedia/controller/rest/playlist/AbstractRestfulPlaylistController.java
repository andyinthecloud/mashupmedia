package org.mashupmedia.controller.rest.playlist;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
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
		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist,
				relativePosition);
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
	
	@RequestMapping(value = "/save-playlist", method = RequestMethod.POST)
	public RestfulMediaItem savePlaylist(
			@RequestParam("playlistId") Long playlistId,
			@RequestParam(value = "playlistName", required = false) String playlistName,
			@RequestParam(value = "mediaItemIds[]", required = false) Long[] mediaItemsIds,
			
			
			Model model) {

		Playlist playlist = playlistManager.getPlaylist(playlistId);		

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
		
		playlistName = StringUtils.trimToEmpty(playlistName);
		if (StringUtils.isNotEmpty(playlistName)) {
			playlist.setName(playlistName);
		}		
		
		playlistManager.savePlaylist(playlist);
		PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(-1, playlist);
		RestfulMediaItem restfulMediaItem = convertToRestfulMediaItem(playlistMediaItem);
		return restfulMediaItem;
	}
}
