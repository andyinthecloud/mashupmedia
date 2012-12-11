package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	public static void appendPlaylist(Playlist playlist, List<Song> songs) {

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null) {
			playlistMediaItems = new ArrayList<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (songs == null || songs.isEmpty()) {
			return;
		}

		int totalPlaylistItems = playlistMediaItems.size();
		
		List<PlaylistMediaItem> appendPlaylistMediaItems = new ArrayList<PlaylistMediaItem>();

		for (int i = 0; i < songs.size(); i++) {
			PlaylistMediaItem playlistSong = new PlaylistMediaItem();
			playlistSong.setMediaItem(songs.get(i));
			playlistSong.setRanking(totalPlaylistItems + i);
			playlistSong.setPlaylist(playlist);
			appendPlaylistMediaItems.add(playlistSong);
		}
		
		
		playlistMediaItems.addAll(appendPlaylistMediaItems);		

//		if (playlistMediaItems.isEmpty()) {
//			return playlistMediaItems;
//		}

		playlistMediaItems.get(0).setPlaying(true);
		playlist.setPlaylistMediaItems(playlistMediaItems);
		
//		return appendPlaylistMediaItems;

	}

	public static void replacePlaylist(Playlist playlist, Song song) {
		if (song == null) {
			return;
		}

		List<Song> songs = new ArrayList<Song>();
		songs.add(song);
		replacePlaylist(playlist, songs);
	}

	public static void appendPlaylist(Playlist playlist, Song song) {
//		List<PlaylistMediaItem> appendPlaylistMediaItems = new ArrayList<PlaylistMediaItem>();
		if (song == null) {
			return;
		}

		List<Song> songs = new ArrayList<Song>();
		songs.add(song);
		appendPlaylist(playlist, songs);
	}
	
//	public static Playlist processRelativePlayingMediaItem(Playlist playlist, int relativeOffset) {
//		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
//		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
//			return null;
//		}	
//		
//		int index = getPlayingIndex(playlistMediaItems);		
//		playlistMediaItems.get(index).setPlaying(false);
//		int maxIndex = playlistMediaItems.size() - 1;
//		int selectedIndex = index + relativeOffset;
//		if (selectedIndex < 0 || selectedIndex > maxIndex) {
//			return playlist;
//		} 
//
//		
//		playlistMediaItems.get(selectedIndex).setPlaying(true);
//		return playlist;
//	}
	
	
//	private static int getPlayingIndex(List<PlaylistMediaItem> playlistMediaItems) {		
//		for (int i = 0; i < playlistMediaItems.size(); i++) {
//			PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(i);
//			if (playlistMediaItem.isPlaying()) {
//				playlistMediaItem.setPlaying(false);
//				return i;
//			}
//		}
//		
//		return 0;
//	}
	
//	public static boolean hasRelativePlayingMediaItem(Playlist playlist, int relativeOffset) {
//		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
//		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
//			return false;
//		}
//		
//		int index = getPlayingIndex(playlistMediaItems);		
//		int maxIndex = playlistMediaItems.size() - 1;
//		int selectedIndex = index + relativeOffset;
//		if (selectedIndex < 0 || selectedIndex > maxIndex) {
//			return false;
//		} 
//
//		return true;
//	}

//	public static int getPlayingMediaItemIndex(Playlist playlist) {
//		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
//		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
//			return 0;
//		}
//		
//		for (int i = 0; i < playlistMediaItems.size(); i++) {
//			PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(i);
//			if (playlistMediaItem.isPlaying()) {
//				return i;
////				MediaItem mediaItem = playlistMediaItem.getMediaItem();
////				return mediaItem;
//			}
//			
//		}
//		
////		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
////			if (playlistMediaItem.isPlaying()) {
////				MediaItem mediaItem = playlistMediaItem.getMediaItem();
////				return mediaItem;
////			}
////		}
//		
////		PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(0);
////		playlistMediaItem.setPlaying(true);
////		MediaItem mediaItem = playlistMediaItem.getMediaItem();
////		return mediaItem;
//		return 0;
//	}

	public static MediaItem processRelativePlayingMediaItemFromPlaylist(Playlist playlist, int relativeOffset) {
		
		MediaItem emptyMediaItem = new MediaItem();
		
		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return emptyMediaItem;
		}
		
		int playingIndex = 0;
		for (int i = 0; i < playlistMediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(i);
			if (playlistMediaItem.isPlaying()) {
				playingIndex = i;
			}			
			playlistMediaItem.setPlaying(false);
		}
		
		int newPlayingIndex = playingIndex + relativeOffset;
		if (newPlayingIndex < 0 || newPlayingIndex > (playlistMediaItems.size() - 1)) {
			playlistMediaItems.get(playingIndex).setPlaying(true);
			return emptyMediaItem;
		}
		
		PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(newPlayingIndex);
		playlistMediaItem.setPlaying(true);
		return playlistMediaItem.getMediaItem();
	}

//	public static Playlist processNextMediaItem(Playlist playlist) {
//		playlist = processPlayingMediaItem(playlist, 1);
//		return playlist;
//	}
//
//	public static Playlist processPreviousMediaItem(Playlist playlist) {
//		playlist = processPlayingMediaItem(playlist, -1);
//		return playlist;
//	}
//
//	public static boolean hasNextMediaItem(Playlist playlist) {
//		return hasPlayingMediaItem(playlist, 1);
//	}
//
//	public static boolean hasPreviousMediaItem(Playlist playlist) {
//		return hasPlayingMediaItem(playlist, -1);
//	}

}
