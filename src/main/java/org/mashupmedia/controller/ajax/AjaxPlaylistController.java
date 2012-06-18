package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.model.User;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
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
public class AjaxPlaylistController extends BaseAjaxController {

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private MusicManager musicManager;
	
	@Autowired
	private AdminManager adminManager;

	@RequestMapping(value = "/current-user-playlist", method = RequestMethod.POST)
	public String getCurrentUserMusicPlaylist(Model model) {
		User user = SecurityHelper.getLoggedInUser();
		PlaylistMediaItem playlistSong = user.getCurrentPlaylistSong();
		Playlist playlist = null;
		if (playlistSong != null) {
			playlist = playlistSong.getPlaylist();
		} else {
			playlist = new Playlist();
		}
		
		model.addAttribute("playlist", playlist);
		return "ajax/playlist/music-playlist";		
	}

	@RequestMapping(value = "/play-album", method = RequestMethod.POST)
	public String playAlbum(@RequestParam("albumId") Long albumId, Model model) {
		Playlist playlist = playlistManager.getDefaultMusicPlaylistForCurrentUser();
		User user = SecurityHelper.getLoggedInUser();
		if (playlist == null) {
			playlist = new Playlist();
			playlist.setDefault(true);
			playlist.setOwner(user);
		}

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();		

		PlaylistHelper.replacePlaylist(playlist, songs);
		playlistManager.savePlaylist(playlist);
		
		PlaylistMediaItem playlistSong = new PlaylistMediaItem();
		if (songs != null && !songs.isEmpty()) {
			playlistSong = playlist.getPlaylistMediaItems().get(0);
		}
		
		user.setCurrentPlaylistSong(playlistSong);
		adminManager.saveUser(user);		

		model.addAttribute("playlist", playlist);
		return "ajax/playlist/music-playlist";

	}

}
