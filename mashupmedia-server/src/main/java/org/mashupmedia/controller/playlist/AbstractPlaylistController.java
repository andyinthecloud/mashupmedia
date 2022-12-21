package org.mashupmedia.controller.playlist;

import org.mashupmedia.controller.BaseController;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.PlaylistHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class AbstractPlaylistController extends BaseController {

	@Autowired
	private PlaylistManager playlistManager;

	@RequestMapping(method = RequestMethod.GET)
	public String getPlaylist(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@RequestParam(value = "playlist", required = false) Long playlistId, Model model) {

		Playlist playlist = null;
		if (playlistId == null) {
			playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(getPlaylistType());
		} else {
			playlist = playlistManager.getPlaylist(playlistId);
		}

		playlistId = playlist.getId();

		// PlaylistHelper.initialiseCurrentlyPlaying(playlist);

		model.addAttribute("playlist", playlist);

		boolean canSavePlaylist = PlaylistHelper.canSavePlaylist(playlist);
		if (playlistId == 0) {
			canSavePlaylist = true;
		}

		model.addAttribute("canSavePlaylist", canSavePlaylist);

		String playlistPath = getPlaylistPath();
		String path = getPath(isFragment, playlistPath);
		return path;
	}

	protected abstract PlaylistType getPlaylistType();

	protected abstract String getPlaylistPath();

}
