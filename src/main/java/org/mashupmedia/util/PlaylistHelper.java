package org.mashupmedia.util;

import java.util.ArrayList;
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

	public static MediaItem getRelativePlayingMediaItemFromPlaylist(Playlist playlist, int relativeOffset) {
		
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

}
