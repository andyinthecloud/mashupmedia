package org.mashupmedia.controller.playlist;

import java.util.List;

import org.mashupmedia.controller.BaseController;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.PlaylistManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class AbstractListPlaylistsController extends BaseController {

	@Autowired
	private PlaylistManager playlistManager;

	@RequestMapping(method = RequestMethod.GET)
	public String getPlaylists(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			Model model) {

		List<Playlist> playlists = playlistManager.getPlaylists(getPlaylistType());
		model.addAttribute("playlists", playlists);
		
		model.addAttribute("playlistType", getPlaylistType());
		
		String playlistPath = getPlaylistPath();
		String path = getPath(isFragment, playlistPath);
		return path;

	}

	protected String getPlaylistPath() {
		return "playlist.list";
	}

	@Override
	public String getPageTitleMessageKey() {
		return "playlist.list.title";
	}

	protected abstract PlaylistType getPlaylistType();

}
