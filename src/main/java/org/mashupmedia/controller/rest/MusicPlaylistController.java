package org.mashupmedia.controller.rest;

import java.util.List;

import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.web.restful.RestfulSong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful/music-playlist")
public class MusicPlaylistController {

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private MusicManager musicManager;

	@RequestMapping(value = "/play-album", method = RequestMethod.GET)
	public RestfulSong playAlbum(@RequestParam("albumId") Long albumId) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		PlaylistHelper.replacePlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
		Song song = (Song) playlistMediaItem.getMediaItem();

		String contextPath = WebHelper.getContextPath();
		RestfulSong restfulSong = new RestfulSong(contextPath, song);
		return restfulSong;
	}
	
	@RequestMapping(value = "/play/current", method = RequestMethod.GET)
	public RestfulSong playCurrentUserMusicPlaylist(Model model) {
		Playlist playlist = playlistManager
				.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		RestfulSong restfulSong = getSongFromPlaylist(0, playlist);		
		return restfulSong;		
	}
	
	@RequestMapping(value = "/play/next", method = RequestMethod.GET)
	public RestfulSong playNextSong(Model model) {
		Playlist playlist = playlistManager
				.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
		RestfulSong restfulSong = getSongFromPlaylist(1, playlist);		
		return restfulSong;		
	}
	
	protected RestfulSong getSongFromPlaylist(int relativePosition, Playlist playlist) {

		PlaylistMediaItem playlistMediaItem = PlaylistHelper
				.getRelativePlayingMediaItemFromPlaylist(playlist, relativePosition);
		if (playlistMediaItem == null || playlistMediaItem.getId() < 1) {
			return null;
		}
		
		Song song = (Song) playlistMediaItem.getMediaItem();
		String contextPath = WebHelper.getContextPath();
		RestfulSong restfulSong = new RestfulSong(contextPath, song);
		return restfulSong;		
	}
	

	protected void savePlaylist(Playlist playlist) {
		playlistManager.savePlaylist(playlist);
		List<PlaylistMediaItem> accessiblePlaylistMediaItems = playlist.getPlaylistMediaItems();
		playlist.setAccessiblePlaylistMediaItems(accessiblePlaylistMediaItems);
	}
}
