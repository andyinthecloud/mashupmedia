package org.mashupmedia.controller.ajax;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.PageNotFoundException;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.task.StreamingTaskManager;
import org.mashupmedia.util.PlaylistHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
	private StreamingTaskManager streamingTaskManager;

	@Autowired
	private MediaManager mediaManager;

	@RequestMapping(value = "/current-user-playlist", method = RequestMethod.POST)
	public String getCurrentUserMusicPlaylist(Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
		model.addAttribute("playlist", playlist);
		return "ajax/playlist/music-playlist";
	}

	@RequestMapping(value = "/play-artist", method = RequestMethod.POST)
	public String playArtist(@RequestParam("artistId") Long artistId, Model model) {
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
		playlistManager.savePlaylist(playlist);
		model.addAttribute("playlist", playlist);
		return "ajax/playlist/music-playlist";
	}

	@RequestMapping(value = "/append-artist", method = RequestMethod.POST)
	public String appendArtist(@RequestParam("artistId") Long artistId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<Album> albums = musicManager.getAlbumsByArtist(artistId);
		if (albums == null || albums.isEmpty()) {
			throw new PageNotFoundException("No songs found for artist id = " + artistId);
		}

		List<Song> songs = new ArrayList<Song>();
		for (Album album : albums) {
			songs.addAll(album.getSongs());
		}

		List<PlaylistMediaItem> appendPlaylistMediaItems = PlaylistHelper.appendPlaylist(playlist, songs);
		playlistManager.savePlaylist(playlist);
		model.addAttribute("playlistMediaItems", appendPlaylistMediaItems);
		return "ajax/playlist/append-playlist";
	}

	@RequestMapping(value = "/play-album", method = RequestMethod.POST)
	public String playAlbum(@RequestParam("albumId") Long albumId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		for (Song song : songs) {
			streamingTaskManager.startMediaItemDownload(song.getId());
		}

		PlaylistHelper.replacePlaylist(playlist, songs);
		playlistManager.savePlaylist(playlist);
		model.addAttribute("playlist", playlist);
		return "ajax/playlist/music-playlist";
	}

	@RequestMapping(value = "/append-album", method = RequestMethod.POST)
	public String appendAlbum(@RequestParam("albumId") Long albumId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		for (Song song : songs) {
			streamingTaskManager.startMediaItemDownload(song.getId());
		}

		List<PlaylistMediaItem> appendPlaylistMediaItems = PlaylistHelper.appendPlaylist(playlist, songs);
		playlistManager.savePlaylist(playlist);
		model.addAttribute("playlistMediaItems", appendPlaylistMediaItems);
		return "ajax/playlist/append-playlist";
	}

	@RequestMapping(value = "/play-song", method = RequestMethod.POST)
	public String playSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			return null;
		}

		Song song = (Song) mediaItem;
		streamingTaskManager.startMediaItemDownload(song.getId());

		PlaylistHelper.replacePlaylist(playlist, song);
		playlistManager.savePlaylist(playlist);
		model.addAttribute("playlist", playlist);
		return "ajax/playlist/music-playlist";
	}

	@RequestMapping(value = "/append-song", method = RequestMethod.POST)
	public String appendSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			throw new PageNotFoundException("Unable to find song: " + songId);
		}

		Song song = (Song) mediaItem;
		streamingTaskManager.startMediaItemDownload(song.getId());

		List<PlaylistMediaItem> appendPlaylistMediaItems = PlaylistHelper.appendPlaylist(playlist, song);
		playlistManager.savePlaylist(playlist);
		model.addAttribute("playlistMediaItems", appendPlaylistMediaItems);
		return "ajax/playlist/append-playlist";
	}

	@RequestMapping(value = "/id/{playlistId}", method = RequestMethod.GET)
	public String playPlaylist(@PathVariable("playlistId") Long playlistId, Model model) {
		Playlist playlist = playlistManager.getPlaylist(playlistId);
		playlistManager.savePlaylist(playlist);
		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		List<MediaItem> mediaItems = PlaylistHelper.getMediaItems(playlistMediaItems);
		model.addAttribute("mediaItems", mediaItems);
		return "ajax/playlist/player-script";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String handleSaveCurrentPlaylist(@RequestParam("playlistId") Long playlistId,
			@RequestParam(value = "mediaItemIds[]", required = false) Long[] mediaItemsIds, Model model) {

		Playlist playlist = playlistManager.getPlaylist(playlistId);

		if (mediaItemsIds == null) {
			mediaItemsIds = new Long[0];
		}

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		for (Long mediaItemId : mediaItemsIds) {
			MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
			mediaItems.add(mediaItem);
		}

		PlaylistHelper.replacePlaylist(playlist, mediaItems);
		playlistManager.savePlaylist(playlist);

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_IS_SUCCESSFUL, true);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_CODE, MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_SUCCESS);
		return "ajax/json/response";
	}
}
