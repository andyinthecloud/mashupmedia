package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

public class PlaylistHelper {

	public static void replacePlaylist(Playlist playlist, List<? extends MediaItem> songs) {
		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems != null) {
			playlistMediaItems.clear();
		} else {
			playlistMediaItems = new ArrayList<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (songs == null || songs.isEmpty()) {
			return;
		}

		for (int i = 0; i < songs.size(); i++) {
			PlaylistMediaItem playlistSong = new PlaylistMediaItem();
			playlistSong.setMediaItem(songs.get(i));
			playlistSong.setRanking(i);
			playlistSong.setPlaylist(playlist);
			playlistMediaItems.add(playlistSong);
		}

		if (playlistMediaItems.isEmpty()) {
			return;
		}

		playlistMediaItems.get(0).setPlaying(true);
		playlist.setPlaylistMediaItems(playlistMediaItems);
	}

	public static List<MediaItem> getMediaItems(List<PlaylistMediaItem> playlistMediaItems) {
		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return mediaItems;
		}

		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			mediaItems.add(playlistMediaItem.getMediaItem());
		}

		return mediaItems;
	}

	public static void appendPlaylist(Playlist playlist, List<? extends MediaItem> mediaItems) {

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null) {
			playlistMediaItems = new ArrayList<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (mediaItems == null || mediaItems.isEmpty()) {
			return;
		}

		int totalPlaylistItems = playlistMediaItems.size();

		for (int i = 0; i < mediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = new PlaylistMediaItem();
			playlistMediaItem.setMediaItem(mediaItems.get(i));
			playlistMediaItem.setRanking(totalPlaylistItems + i);
			playlistMediaItem.setPlaylist(playlist);
			playlistMediaItems.add(playlistMediaItem);
		}

		playlist.setPlaylistMediaItems(playlistMediaItems);
	}

	public static void replacePlaylist(Playlist playlist, Song song) {
		if (song == null) {
			return;
		}

		List<Song> songs = new ArrayList<Song>();
		songs.add(song);
		replacePlaylist(playlist, songs);
	}

	public static void appendPlaylist(Playlist playlist, MediaItem mediaItem) {
		if (mediaItem == null) {
			return;
		}

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		mediaItems.add(mediaItem);
		appendPlaylist(playlist, mediaItems);
	}

	public static PlaylistMediaItem processRelativePlayingMediaItemFromPlaylist(Playlist playlist, int relativeOffset, boolean isSetPlayingStatus) {

		PlaylistMediaItem emptyPlaylistMediaItem = new PlaylistMediaItem();
		emptyPlaylistMediaItem.setPlaylist(playlist);
		emptyPlaylistMediaItem.setMediaItem(new MediaItem());

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return emptyPlaylistMediaItem;
		}

		User user = AdminHelper.getLoggedInUser();
		PlaylistMediaItem currentPlaylistMediaItem = user.getPlaylistMediaItem();

		int playingIndex = 0;
		for (int i = 0; i < playlistMediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(i);
			if (playlistMediaItem.equals(currentPlaylistMediaItem)) {
				playingIndex = i;
			}
			
			if (isSetPlayingStatus) {
				playlistMediaItem.setPlaying(false);
			}
		}

		int newPlayingIndex = playingIndex + relativeOffset;
		if (newPlayingIndex < 0 || newPlayingIndex > (playlistMediaItems.size() - 1)) {
			playlistMediaItems.get(playingIndex).setPlaying(true);
			return emptyPlaylistMediaItem;
		}

		PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(newPlayingIndex);
		if (isSetPlayingStatus) {
			playlistMediaItem.setPlaying(true);
		}
		return playlistMediaItem;
	}

	public static PlaylistMediaItem getFirstPlayListMediaItem(Playlist playlist) {
		if (playlist == null) {
			return null;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return null;
		}

		return playlistMediaItems.get(0);
	}

	public static boolean canSavePlaylist(Playlist playlist) {
		if (playlist == null) {
			return false;
		}

		User createdBy = playlist.getCreatedBy();
		if (createdBy == null) {
			return true;
		}
		long createdById = createdBy.getId();

		User user = AdminHelper.getLoggedInUser();
		long userId = user.getId();

		if (createdById == userId) {
			return true;
		}

		return false;
	}

	public static void initialiseCurrentlyPlaying(Playlist playlist) {
		if (playlist == null) {
			return;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return;
		}

		User user = AdminHelper.getLoggedInUser();
		if (user == null) {
			return;
		}

		PlaylistMediaItem userPlaylistMediaItem = user.getPlaylistMediaItem();
		if (userPlaylistMediaItem == null) {
			playlistMediaItems.get(0).setPlaying(true);
			return;
		}

		long userPlaylistMediaItemId = userPlaylistMediaItem.getMediaItem().getId();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			long playlistMediaItemId = playlistMediaItem.getMediaItem().getId();
			if (playlistMediaItemId == userPlaylistMediaItemId) {
				playlistMediaItem.setPlaying(true);
				return;
			}
		}
		
		playlistMediaItems.get(0).setPlaying(true);

	}

	public static PlaylistType getPlaylistType(String playlistTypeValue) {
		playlistTypeValue = StringUtils.trimToEmpty(playlistTypeValue);
		if (StringUtils.isEmpty(playlistTypeValue)) {
			return PlaylistType.MUSIC;
		}

		PlaylistType[] playlistTypes = PlaylistType.values();
		for (PlaylistType playlistType : playlistTypes) {
			if (playlistType.getValue().equalsIgnoreCase(playlistTypeValue)) {
				return playlistType;
			}
		}
		return PlaylistType.MUSIC;

	}

	public static PlaylistMediaItem getPlaylistMediaItem(Playlist playlist, Long mediaItemId) {
		
		if (mediaItemId == null || mediaItemId == 0) {
			return null;
		}
		
		if (playlist == null) {
			return null;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return null;
		}

		User user = AdminHelper.getLoggedInUser();
		if (user == null) {
			return null;
		}
		
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			MediaItem mediaItem = playlistMediaItem.getMediaItem(); 
			if (mediaItem.getId() == mediaItemId) {
				return playlistMediaItem;
			}
		}
		
		return null;
		
	}





}
