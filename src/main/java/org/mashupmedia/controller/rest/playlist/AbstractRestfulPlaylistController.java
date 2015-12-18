package org.mashupmedia.controller.rest.playlist;

import org.mashupmedia.model.User;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.web.restful.RestfulMediaItem;
import org.mashupmedia.web.restful.RestfulSong;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class AbstractRestfulPlaylistController {

	
	@RequestMapping(value = "/play/next", method = RequestMethod.GET)
	public RestfulMediaItem playNextSong(Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
		PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(1, playlist);
		if (playlistMediaItem == null) {
			return null;
		}

		User user = AdminHelper.getLoggedInUser();
		playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);
		RestfulSong restfulSong = convertToResfulMediaItem(playlistMediaItem);
		return restfulSong;
	}
	
	protected PlaylistMediaItem getMediaItemFromPlaylist(int relativePosition, Playlist playlist) {
		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist,
				relativePosition);
		if (playlistMediaItem == null || playlistMediaItem.getId() < 1) {
			return null;
		}

		return playlistMediaItem;
	}
	
	protected abstract RestfulMediaItem convertToRestfulMediaItem(PlaylistMediaItem playlistMediaItem);
}
