package org.mashupmedia.controller.rest.playlist;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.exception.PageNotFoundException;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.web.restful.RestfulMediaItem;
import org.mashupmedia.web.restful.RestfulSong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful/playlist/music")
public class RestfulMusicPlaylistController extends AbstractRestfulPlaylistController {

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private MusicManager musicManager;
	
	@Autowired
	private MediaManager mediaManager;

	@RequestMapping(value = "/play-album", method = RequestMethod.GET)
	public RestfulSong playAlbum(@RequestParam("albumId") Long albumId) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		PlaylistHelper.replacePlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
		Song song = (Song) playlistMediaItem.getMediaItem();

		RestfulSong restfulSong = new RestfulSong(song);
		return restfulSong;
	}

	@RequestMapping(value = "/append-album", method = RequestMethod.GET)
	public RestfulSong appendAlbum(@RequestParam("albumId") Long albumId, Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		PlaylistHelper.appendPlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist, 0);

		Song song = (Song) playlistMediaItem.getMediaItem();
		RestfulSong restfulSong = new RestfulSong(song);
		return restfulSong;
	}

	@RequestMapping(value = "/play-artist", method = RequestMethod.GET)
	public RestfulSong playArtist(@RequestParam("artistId") Long artistId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<Album> albums = musicManager.getAlbumsByArtist(artistId);
		if (albums == null || albums.isEmpty()) {
			throw new PageNotFoundException("No songs found for artist id = " + artistId);
		}

		List<Song> songs = new ArrayList<Song>();
		for (Album album : albums) {
			songs.addAll(album.getSongs());
		}

		PlaylistHelper.replacePlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist, 0);

		Song song = (Song) playlistMediaItem.getMediaItem();
		RestfulSong restfulSong = new RestfulSong(song);
		return restfulSong;
	}

	@RequestMapping(value = "/append-artist", method = RequestMethod.GET)
	public RestfulSong appendArtist(@RequestParam("artistId") Long artistId, Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<Album> albums = musicManager.getAlbumsByArtist(artistId);
		if (albums == null || albums.isEmpty()) {
			throw new PageNotFoundException("No songs found for artist id = " + artistId);
		}

		List<Song> songs = new ArrayList<Song>();
		for (Album album : albums) {
			songs.addAll(album.getSongs());
		}

		PlaylistHelper.appendPlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist, 0);

		Song song = (Song) playlistMediaItem.getMediaItem();
		RestfulSong restfulSong = new RestfulSong(song);
		return restfulSong;
	}
	
	@RequestMapping(value = "/play-song", method = RequestMethod.GET)
	public RestfulSong playSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager
				.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		
		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			return null;
		}

		Song song = (Song) mediaItem;

		PlaylistHelper.replacePlaylist(playlist, song);
		savePlaylist(playlist);

		RestfulSong restfulSong = new RestfulSong(song);
		return restfulSong;
	}

	@RequestMapping(value = "/append-song", method = RequestMethod.GET)
	public RestfulSong appendSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager
				.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			throw new PageNotFoundException("Unable to find song: " + songId);
		}

		Song song = (Song) mediaItem;
		PlaylistHelper.appendPlaylist(playlist, song);
		savePlaylist(playlist);

		RestfulSong restfulSong = new RestfulSong(song);
		return restfulSong;

	}
	

	// @RequestMapping(value = "/play/current", method = RequestMethod.GET)
	// public RestfulSong playCurrentUserMusicPlaylist(Model model) {
	// Playlist playlist =
	// playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
	//
	// PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(0,
	// playlist);
	// RestfulSong restfulSong = convertToRestfulMediaItem(playlistMediaItem);
	// return restfulSong;
	// }

	@Override
	protected PlaylistType getPlaylistType() {
		return PlaylistType.MUSIC;
	}

	@Override
	protected RestfulMediaItem convertToRestfulMediaItem(PlaylistMediaItem playlistMediaItem) {
		if (playlistMediaItem == null) {
			return null;
		}

		Song song = (Song) playlistMediaItem.getMediaItem();
		RestfulSong restfulSong = new RestfulSong(song);
		return restfulSong;
	}

	// @RequestMapping(value = "/play/previous", method = RequestMethod.GET)
	// public RestfulSong playPreviousSong(Model model) {
	// Playlist playlist =
	// playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
	// PlaylistMediaItem playlistMediaItem = getMediaItemFromPlaylist(-1,
	// playlist);
	// if (playlistMediaItem == null) {
	// return null;
	// }
	//
	// User user = AdminHelper.getLoggedInUser();
	// playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);
	// RestfulSong restfulSong = convertToResfulMediaItem(playlistMediaItem);
	// return restfulSong;
	// }

	// protected void savePlaylist(Playlist playlist) {
	// playlistManager.savePlaylist(playlist);
	// List<PlaylistMediaItem> accessiblePlaylistMediaItems =
	// playlist.getPlaylistMediaItems();
	// playlist.setAccessiblePlaylistMediaItems(accessiblePlaylistMediaItems);
	// }

	// @RequestMapping(value = "/id/{playlistId}", method = RequestMethod.GET)
	// public String handleGetPlaylist(
	// @PathVariable Long playlistId,
	// @RequestParam(value = "webFormatType", required = false) String
	// webFormatTypeValue,
	// @RequestParam(value = "updateLastAccessedToNow", required = false)
	// Boolean isUpdateLastAccessedToNow,
	// Model model) {
	// Playlist playlist = playlistManager.getPlaylist(playlistId);
	// PlaylistHelper.initialiseCurrentlyPlaying(playlist);
	//
	// if (isUpdateLastAccessedToNow != null && isUpdateLastAccessedToNow) {
	// playlistManager.savePlaylist(playlist);
	// }
	//
	// model.addAttribute("playlist", playlist);
	//
	// boolean canSavePlaylist = PlaylistHelper.canSavePlaylist(playlist);
	// if (playlistId == 0) {
	// canSavePlaylist = true;
	// }
	//
	// model.addAttribute("canSavePlaylist", canSavePlaylist);
	//
	// WebContentType webFormatType = WebHelper.getWebContentType(
	// webFormatTypeValue, WebContentType.HTML);
	// if (webFormatType == WebContentType.JSON) {
	// return "ajax/json/playlist";
	// }
	//
	// return "ajax/playlist/music-playlist";
	// }

}
