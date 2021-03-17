package org.mashupmedia.controller.rest.playlist;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.exception.PageNotFoundException;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.web.restful.RestfulMediaItem;
import org.mashupmedia.web.restful.RestfulSong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	@ResponseBody
	public RestfulSong playAlbum(@RequestParam("albumId") Long albumId) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		PlaylistHelper.replacePlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist, 0,
				true);
		Song song = (Song) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulSong restfulSong = new RestfulSong(song, suppliedStreamingMediaContentTypes);
		return restfulSong;
	}

	@RequestMapping(value = "append-album", method = RequestMethod.GET)
	@ResponseBody
	public RestfulSong appendAlbum(@RequestParam("albumId") Long albumId, Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		PlaylistHelper.appendPlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist, 0,
				true);

		// if playlist was empty get the first song in the new list
		if (playlistMediaItem == null) {
			playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist, 0, true);
		}

		Song song = (Song) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulSong restfulSong = new RestfulSong(song, suppliedStreamingMediaContentTypes);
		return restfulSong;
	}

	@RequestMapping(value = "/play-artist", method = RequestMethod.GET)
	@ResponseBody
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

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist, 0,
				true);

		Song song = (Song) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulSong restfulSong = new RestfulSong(song, suppliedStreamingMediaContentTypes);
		return restfulSong;
	}

	@RequestMapping(value = "append-artist", method = RequestMethod.GET)
	@ResponseBody
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

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist, 0,
				true);

		Song song = (Song) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulSong restfulSong = new RestfulSong(song, suppliedStreamingMediaContentTypes);
		return restfulSong;
	}

	@RequestMapping(value = "/play-song", method = RequestMethod.GET)
	@ResponseBody
	public RestfulSong playSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			return null;
		}

		Song song = (Song) mediaItem;

		PlaylistHelper.replacePlaylist(playlist, song);
		savePlaylist(playlist);

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulSong restfulSong = new RestfulSong(song, suppliedStreamingMediaContentTypes);
		return restfulSong;
	}

	@RequestMapping(value = "append-song", method = RequestMethod.GET)
	@ResponseBody
	public RestfulSong appendSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			throw new PageNotFoundException("Unable to find song: " + songId);
		}

		Song song = (Song) mediaItem;
		PlaylistHelper.appendPlaylist(playlist, song);
		savePlaylist(playlist);

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulSong restfulSong = new RestfulSong(song, suppliedStreamingMediaContentTypes);
		return restfulSong;

	}

	@Override
	protected PlaylistType getPlaylistType() {
		return PlaylistType.MUSIC;
	}

	@Override
	protected RestfulMediaItem convertToRestfulMediaItem(PlaylistMediaItem playlistMediaItem) {

		Song song = new Song();
		MediaContentType[] suppliedStreamingMediaContentTypes = new MediaContentType[] { MediaContentType.UNSUPPORTED };

		if (playlistMediaItem != null) {
			song = (Song) playlistMediaItem.getMediaItem();
			suppliedStreamingMediaContentTypes = mediaManager.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		}

		RestfulSong restfulSong = new RestfulSong(song, suppliedStreamingMediaContentTypes);
		return restfulSong;
	}

}
