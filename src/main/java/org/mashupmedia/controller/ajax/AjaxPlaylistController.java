package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.model.User;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Playlist;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ajax/playlist")
public class AjaxPlaylistController extends BaseAjaxController{
	
	@Autowired
	private PlaylistManager playlistManager;
	
	@Autowired
	private MusicManager musicManager;
	
	@RequestMapping(value = "/play-album", method = RequestMethod.POST)
	public String addToPlaylist(@RequestParam("albumId") Long albumId,  Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser();
		User user = SecurityHelper.getLoggedInUser();
		if (playlist == null) {
			playlist = new Playlist();
			playlist.setDefault(true);
			playlist.setOwner(user);
		}
		
		Album album = musicManager.getAlbum(albumId);
		List<Song> songs =  album.getSongs();
		
		
		List<Album> albums = musicManager.getRandomAlbums(30);
		model.addAttribute("albums", albums);
		return "ajax/music/random-albums";

	}

}
