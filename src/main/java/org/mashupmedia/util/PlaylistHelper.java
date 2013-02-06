package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.playlist.Playlist;
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

		List<PlaylistMediaItem> appendPlaylistMediaItems = new ArrayList<PlaylistMediaItem>();

		for (int i = 0; i < mediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = new PlaylistMediaItem();
			playlistMediaItem.setMediaItem(mediaItems.get(i));
			playlistMediaItem.setRanking(totalPlaylistItems + i);
			playlistMediaItem.setPlaylist(playlist);
			appendPlaylistMediaItems.add(playlistMediaItem);
		}

		playlistMediaItems.addAll(appendPlaylistMediaItems);

		playlistMediaItems.get(0).setPlaying(true);
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

	public static PlaylistMediaItem getRelativePlayingMediaItemFromPlaylist(Playlist playlist, int relativeOffset) {

		PlaylistMediaItem emptyPlaylistMediaItem = new PlaylistMediaItem();
		emptyPlaylistMediaItem.setPlaylist(playlist);
		emptyPlaylistMediaItem.setMediaItem(new MediaItem());

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return emptyPlaylistMediaItem;
		}

		User user = SecurityHelper.getLoggedInUser();
		PlaylistMediaItem currentPlaylistMediaItem = user.getPlaylistMediaItem();
		
		int playingIndex = 0;
		for (int i = 0; i < playlistMediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(i);
			if (playlistMediaItem.equals(currentPlaylistMediaItem) ) {
				playingIndex = i;
			}
			playlistMediaItem.setPlaying(false);
		}

		int newPlayingIndex = playingIndex + relativeOffset;
		if (newPlayingIndex < 0 || newPlayingIndex > (playlistMediaItems.size() - 1)) {
			playlistMediaItems.get(playingIndex).setPlaying(true);
			return emptyPlaylistMediaItem;
		}

		PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(newPlayingIndex);
		playlistMediaItem.setPlaying(true);
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

		User user = SecurityHelper.getLoggedInUser();
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
		
		User user = SecurityHelper.getLoggedInUser();
		if (user == null) {
			return;
		}
		
		PlaylistMediaItem userPlaylistMediaItem = user.getPlaylistMediaItem();		
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			if (playlistMediaItem.equals(userPlaylistMediaItem)) {
				playlistMediaItem.setPlaying(true);
				return;
			}
		}
		
		
	}


}
