package org.mashupmedia.controller;

import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.PlaylistHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class PlaylistController extends BaseController{
	
	private PlaylistManager playlistManager;

	
	
	@RequestMapping(method = RequestMethod.GET)
	public String getPlaylist(@RequestParam(value = FRAGMENT_PARAM, required = false) Boolean isFragment,
			@RequestParam(value = "playlist", required = false) Long playlistId,						
			Model model) {
		Playlist playlist = playlistManager.getPlaylist(playlistId);
		PlaylistHelper.initialiseCurrentlyPlaying(playlist);


		model.addAttribute("playlist", playlist);

		boolean canSavePlaylist = PlaylistHelper.canSavePlaylist(playlist);
		if (playlistId == 0) {
			canSavePlaylist = true;
		}

		model.addAttribute("canSavePlaylist", canSavePlaylist);

//		WebContentType webFormatType = WebHelper.getWebContentType(
//				webFormatTypeValue, WebContentType.HTML);
//		if (webFormatType == WebContentType.JSON) {
//			return "ajax/json/playlist";
//		}

		
		String playlistPath = getPlaylistPath();
		String path = getPath(isFragment, playlistPath);
		return path;
//		return "ajax/playlist/music-playlist";
	}



	protected abstract String getPlaylistPath();

}
