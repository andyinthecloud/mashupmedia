package org.mashupmedia.controller.ajax;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.PageNotFoundException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.WebContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ajax/playlist")
@Deprecated
public class MusicPlaylistController extends AjaxBaseController {

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private MusicManager musicManager;

	@Autowired
	private MediaManager mediaManager;



	@RequestMapping(value = "/play/id/{playlistId}", method = RequestMethod.GET)
	public String handlePlayPlaylist(@PathVariable Long playlistId, Model model) {
		Playlist playlist = playlistManager.getPlaylist(playlistId);
		model.addAttribute("playlist", playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper
				.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
		MediaItem mediaItem = playlistMediaItem.getMediaItem();
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
				mediaItem);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
				playlist);
		return "ajax/json/media-item";
	}

	@RequestMapping(value = "/play-artist", method = RequestMethod.POST)
	public String playArtist(@RequestParam("artistId") Long artistId,
			Model model) {
		Playlist playlist = playlistManager
				.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<Album> albums = musicManager.getAlbumsByArtist(artistId);
		if (albums == null || albums.isEmpty()) {
			throw new PageNotFoundException("No songs found for artist id = "
					+ artistId);
		}

		List<Song> songs = new ArrayList<Song>();
		for (Album album : albums) {
			songs.addAll(album.getSongs());
		}

		PlaylistHelper.replacePlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper
				.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
		MediaItem mediaItem = playlistMediaItem.getMediaItem();
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
				mediaItem);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
				playlist);
		return "ajax/json/media-item";
	}

	protected void savePlaylist(Playlist playlist) {
		playlistManager.savePlaylist(playlist);
		List<PlaylistMediaItem> accessiblePlaylistMediaItems = playlist
				.getPlaylistMediaItems();
		playlist.setAccessiblePlaylistMediaItems(accessiblePlaylistMediaItems);
	}

	@RequestMapping(value = "/append-artist", method = RequestMethod.POST)
	public String appendArtist(@RequestParam("artistId") Long artistId,
			Model model) {
		Playlist playlist = playlistManager
				.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<Album> albums = musicManager.getAlbumsByArtist(artistId);
		if (albums == null || albums.isEmpty()) {
			throw new PageNotFoundException("No songs found for artist id = "
					+ artistId);
		}

		List<Song> songs = new ArrayList<Song>();
		for (Album album : albums) {
			songs.addAll(album.getSongs());
		}

		PlaylistHelper.appendPlaylist(playlist, songs);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper
				.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
		MediaItem mediaItem = playlistMediaItem.getMediaItem();
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
				mediaItem);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
				playlist);
		return "ajax/json/media-item";
	}

//	@RequestMapping(value = "/play-album", method = RequestMethod.POST)
//	public String playAlbum(@RequestParam("albumId") Long albumId, Model model) {
//		Playlist playlist = playlistManager
//				.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);
//
//		Album album = musicManager.getAlbum(albumId);
//		List<Song> songs = album.getSongs();
//		PlaylistHelper.replacePlaylist(playlist, songs);
//		savePlaylist(playlist);
//
//		PlaylistMediaItem playlistMediaItem = PlaylistHelper
//				.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
//		MediaItem mediaItem = playlistMediaItem.getMediaItem();
//		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
//				mediaItem);
//		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
//				playlist);
//		return "ajax/json/media-item";
//	}

//	@RequestMapping(value = "/append-album", method = RequestMethod.POST)
//	public String appendAlbum(@RequestParam("albumId") Long albumId, Model model) {
//		Playlist playlist = playlistManager
//				.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
//
//		Album album = musicManager.getAlbum(albumId);
//		List<Song> songs = album.getSongs();
//		PlaylistHelper.appendPlaylist(playlist, songs);
//		savePlaylist(playlist);
//
//		PlaylistMediaItem playlistMediaItem = PlaylistHelper
//				.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
//		MediaItem mediaItem = playlistMediaItem.getMediaItem();
//		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
//				mediaItem);
//		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
//				playlist);
//		return "ajax/json/media-item";
//	}

	@RequestMapping(value = "/play-song", method = RequestMethod.POST)
	public String playSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager
				.getDefaultPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			return null;
		}

		Song song = (Song) mediaItem;

		PlaylistHelper.replacePlaylist(playlist, song);
		savePlaylist(playlist);

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
				mediaItem);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
				playlist);
		return "ajax/json/media-item";
	}

	@RequestMapping(value = "/append-song", method = RequestMethod.POST)
	public String appendSong(@RequestParam("songId") Long songId, Model model) {
		Playlist playlist = playlistManager
				.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (!(mediaItem instanceof Song)) {
			throw new PageNotFoundException("Unable to find song: " + songId);
		}

		Song song = (Song) mediaItem;
		PlaylistHelper.appendPlaylist(playlist, song);
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper
				.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
		mediaItem = playlistMediaItem.getMediaItem();
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
				mediaItem);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
				playlist);
		return "ajax/json/media-item";
	}

	@RequestMapping(value = "/append-media-items", method = RequestMethod.POST)
	public String appendMediaItems(
			@RequestParam("mediaItemIds[]") Long[] mediaItemIds, Model model) {
		addMediaItemsToPlaylist(mediaItemIds, model, false);
		return "ajax/json/media-item";
	}

	@RequestMapping(value = "/replace-media-items", method = RequestMethod.POST)
	public String replaceMediaItems(
			@RequestParam("mediaItemIds[]") Long[] mediaItemIds, Model model) {
		addMediaItemsToPlaylist(mediaItemIds, model, true);
		return "ajax/json/media-item";
	}

	protected void addMediaItemsToPlaylist(Long[] mediaItemIds, Model model,
			boolean isReplace) {
		Playlist playlist = playlistManager
				.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		for (Long mediaItemId : mediaItemIds) {
			MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
			mediaItems.add(mediaItem);
		}

		if (isReplace) {
			PlaylistHelper.replacePlaylist(playlist, mediaItems);
		} else {
			PlaylistHelper.appendPlaylist(playlist, mediaItems);
		}
		savePlaylist(playlist);

		PlaylistMediaItem playlistMediaItem = PlaylistHelper
				.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
		MediaItem mediaItem = playlistMediaItem.getMediaItem();
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM,
				mediaItem);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST,
				playlist);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String handleSaveCurrentPlaylist(
			@RequestParam("playlistId") Long playlistId,
			@RequestParam(value = "playlistName", required = false) String playlistName,
			@RequestParam(value = "mediaItemIds[]", required = false) Long[] mediaItemsIds,
			Model model) {

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
		PlaylistHelper.initialiseCurrentlyPlaying(playlist);

		playlistName = StringUtils.trimToEmpty(playlistName);
		if (StringUtils.isNotEmpty(playlistName)) {
			playlist.setName(playlistName);
		}

		playlistManager.savePlaylist(playlist);

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_IS_SUCCESSFUL,
				true);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_CODE,
				MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_SUCCESS);
		return "ajax/json/response";
	}

	@RequestMapping(value = "/save-as", method = RequestMethod.POST)
	public String handleSaveAsPlaylist(
			@RequestParam("playlistId") Long playlistId,
			@RequestParam("playlistName") String playlistName,
			@RequestParam(value = "mediaItemIds[]", required = false) Long[] mediaItemsIds,
			Model model) {

		Playlist playlist = playlistManager.getPlaylist(playlistId);
		playlist.setId(0);
		playlistName = StringUtils.trimToEmpty(playlistName);
		playlist.setName(playlistName);

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
		PlaylistMediaItem playlistMediaItem = PlaylistHelper
				.getFirstPlayListMediaItem(playlist);
		User user = AdminHelper.getLoggedInUser();
		playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_IS_SUCCESSFUL,
				true);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_CODE,
				playlist.getId());
		return "ajax/json/response";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String handleNewPlaylist(
			@RequestParam("playlistName") String playlistName, Model model) {

		Playlist playlist = new Playlist();
		playlist.setName(playlistName);
		playlist.setPlaylistType(PlaylistType.MUSIC);

		playlistManager.savePlaylist(playlist);

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_IS_SUCCESSFUL,
				true);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_CODE,
				playlist.getId());

		return "ajax/json/response";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String handleDeletePlaylist(
			@RequestParam("playlistId") Long playlistId, Model model) {
		Playlist playlist = playlistManager.getPlaylist(playlistId);

		boolean isSuccessful = false;
		if (!playlist.isUserDefault()) {
			playlistManager.deletePlaylist(playlistId);
			isSuccessful = true;
		}

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_IS_SUCCESSFUL,
				isSuccessful);
		model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_CODE,
				MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_SUCCESS);
		return "ajax/json/response";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String handleListPlaylists(
			@RequestParam("playlistType") String playlistTypeValue, Model model) {

		PlaylistType playlistType = PlaylistHelper
				.getPlaylistType(playlistTypeValue);
		List<Playlist> playlists = playlistManager.getPlaylists(playlistType);
		model.addAttribute("playlists", playlists);
		return "ajax/playlist/list-playlists";
	}

	@RequestMapping(value = "/id/{playlistId}", method = RequestMethod.GET)
	public String handleGetPlaylist(
			@PathVariable Long playlistId,
			@RequestParam(value = "webFormatType", required = false) String webFormatTypeValue,
			@RequestParam(value = "updateLastAccessedToNow", required = false) Boolean isUpdateLastAccessedToNow,
			Model model) {
		Playlist playlist = playlistManager.getPlaylist(playlistId);
		PlaylistHelper.initialiseCurrentlyPlaying(playlist);

		if (isUpdateLastAccessedToNow != null && isUpdateLastAccessedToNow) {
			playlistManager.savePlaylist(playlist);
		}

		model.addAttribute("playlist", playlist);

		boolean canSavePlaylist = PlaylistHelper.canSavePlaylist(playlist);
		if (playlistId == 0) {
			canSavePlaylist = true;
		}

		model.addAttribute("canSavePlaylist", canSavePlaylist);

		WebContentType webFormatType = WebHelper.getWebContentType(
				webFormatTypeValue, WebContentType.HTML);
		if (webFormatType == WebContentType.JSON) {
			return "ajax/json/playlist";
		}

		return "ajax/playlist/music-playlist";
	}

}
