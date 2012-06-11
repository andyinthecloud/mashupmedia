package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.model.User;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.playlist.MusicPlaylist;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.PlaylistHelper;
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
	
	@Autowired
	private AdminManager adminManager;
	
	@RequestMapping(value = "/play-album", method = RequestMethod.POST)
	public String playAlbum(@RequestParam("albumId") Long albumId,  Model model) {
		MusicPlaylist musicPlaylist = playlistManager.getDefaultMusicPlaylistForCurrentUser();
		User user = SecurityHelper.getLoggedInUser();
		if (musicPlaylist == null) {
			musicPlaylist = new MusicPlaylist();
			musicPlaylist.setDefault(true);
			musicPlaylist.setOwner(user);
		}
		
		Album album = musicManager.getAlbum(albumId);
		List<Song> songs =  album.getSongs();
		
		PlaylistHelper.replaceMusicPlaylistSongs(musicPlaylist, songs);
		playlistManager.savePlaylist(musicPlaylist);
		user.setCurrentMusicPlaylist(musicPlaylist);
		adminManager.saveUser(user);
		
		model.addAttribute("musicPlaylist", musicPlaylist);		
		return "ajax/playlist/music-playlist";

	}

}
