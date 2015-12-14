package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.WebContentType;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/playlist")
public class PlaylistController extends BaseController{
	
	private PlaylistManager playlistManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		// do nothing
	}

	@Override
	public String getPageTitleMessageKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@RequestMapping(value = "/id/{playlistId}", method = RequestMethod.GET)
	public String getPlaylist(
			@PathVariable Long playlistId,						
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

		return "ajax/playlist/music-playlist";
	}

}
