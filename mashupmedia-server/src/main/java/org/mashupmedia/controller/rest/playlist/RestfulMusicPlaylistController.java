package org.mashupmedia.controller.rest.playlist;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.exception.PageNotFoundException;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.web.restful.RestfulMediaItem;
import org.mashupmedia.web.restful.RestfulTrack;
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
	public RestfulTrack playAlbum(@RequestParam("albumId") Long albumId) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Track> tracks = album.getTracks();
		PlaylistHelper.replacePlaylist(playlist, tracks);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.navigatePlaylist(playlist, 0,
				true);
		Track track = (Track) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulTrack restfulTrack = new RestfulTrack(track, suppliedStreamingMediaContentTypes);
		return restfulTrack;
	}

	@RequestMapping(value = "append-album", method = RequestMethod.GET)
	@ResponseBody
	public RestfulTrack appendAlbum(@RequestParam("albumId") Long albumId, Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		Album album = musicManager.getAlbum(albumId);
		List<Track> tracks = album.getTracks();
		PlaylistHelper.appendPlaylist(playlist, tracks);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.navigatePlaylist(playlist, 0,
				true);

		// if playlist was empty get the first track in the new list
		if (playlistMediaItem == null) {
			playlistMediaItem = PlaylistHelper.navigatePlaylist(playlist, 0, true);
		}

		Track track = (Track) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulTrack restfulTrack = new RestfulTrack(track, suppliedStreamingMediaContentTypes);
		return restfulTrack;
	}

	@RequestMapping(value = "/play-artist", method = RequestMethod.GET)
	@ResponseBody
	public RestfulTrack playArtist(@RequestParam("artistId") Long artistId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<Album> albums = musicManager.getAlbumsByArtist(artistId);
		if (albums == null || albums.isEmpty()) {
			throw new PageNotFoundException("No tracks found for artist id = " + artistId);
		}

		List<Track> tracks = new ArrayList<Track>();
		for (Album album : albums) {
			tracks.addAll(album.getTracks());
		}

		PlaylistHelper.replacePlaylist(playlist, tracks);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.navigatePlaylist(playlist, 0,
				true);

		Track track = (Track) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulTrack restfulTrack = new RestfulTrack(track, suppliedStreamingMediaContentTypes);
		return restfulTrack;
	}

	@RequestMapping(value = "append-artist", method = RequestMethod.GET)
	@ResponseBody
	public RestfulTrack appendArtist(@RequestParam("artistId") Long artistId, Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<Album> albums = musicManager.getAlbumsByArtist(artistId);
		if (albums == null || albums.isEmpty()) {
			throw new PageNotFoundException("No tracks found for artist id = " + artistId);
		}

		List<Track> tracks = new ArrayList<Track>();
		for (Album album : albums) {
			tracks.addAll(album.getTracks());
		}

		PlaylistHelper.appendPlaylist(playlist, tracks);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper.navigatePlaylist(playlist, 0,
				true);

		Track track = (Track) playlistMediaItem.getMediaItem();

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulTrack restfulTrack = new RestfulTrack(track, suppliedStreamingMediaContentTypes);
		return restfulTrack;
	}

	@RequestMapping(value = "/play-track", method = RequestMethod.GET)
	@ResponseBody
	public RestfulTrack playTrack(@RequestParam("trackId") Long trackId, Model model) {
		Playlist playlist = playlistManager.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(trackId);
		if (!(mediaItem instanceof Track)) {
			return null;
		}

		Track track = (Track) mediaItem;

		PlaylistHelper.replacePlaylist(playlist, track);
		savePlaylist(playlist);

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulTrack restfulTrack = new RestfulTrack(track, suppliedStreamingMediaContentTypes);
		return restfulTrack;
	}

	@RequestMapping(value = "append-track", method = RequestMethod.GET)
	@ResponseBody
	public RestfulTrack appendTrack(@RequestParam("trackId") Long trackId, Model model) {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(trackId);
		if (!(mediaItem instanceof Track)) {
			throw new PageNotFoundException("Unable to find track: " + trackId);
		}

		Track track = (Track) mediaItem;
		PlaylistHelper.appendPlaylist(playlist, track);
		savePlaylist(playlist);

		MediaContentType[] suppliedStreamingMediaContentTypes = mediaManager
				.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		RestfulTrack restfulTrack = new RestfulTrack(track, suppliedStreamingMediaContentTypes);
		return restfulTrack;

	}

	@Override
	protected PlaylistType getPlaylistType() {
		return PlaylistType.MUSIC;
	}

	@Override
	protected RestfulMediaItem convertToRestfulMediaItem(PlaylistMediaItem playlistMediaItem) {

		Track track = new Track();
		MediaContentType[] suppliedStreamingMediaContentTypes = new MediaContentType[] { MediaContentType.UNSUPPORTED };

		if (playlistMediaItem != null) {
			track = (Track) playlistMediaItem.getMediaItem();
			suppliedStreamingMediaContentTypes = mediaManager.getSuppliedStreamingMediaContentTypes(LibraryType.MUSIC);
		}

		RestfulTrack restfulTrack = new RestfulTrack(track, suppliedStreamingMediaContentTypes);
		return restfulTrack;
	}

}
