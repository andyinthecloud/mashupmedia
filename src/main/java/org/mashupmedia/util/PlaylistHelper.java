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

	public static List<PlaylistMediaItem> appendPlaylist(Playlist playlist, List<Song> songs) {

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null) {
			playlistMediaItems = new ArrayList<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (songs == null || songs.isEmpty()) {
			return playlistMediaItems;
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

		if (playlistMediaItems.isEmpty()) {
			return playlistMediaItems;
		}

		playlistMediaItems.get(0).setPlaying(true);
		playlist.setPlaylistMediaItems(playlistMediaItems);
		return appendPlaylistMediaItems;

	}

	public static void replacePlaylist(Playlist playlist, Song song) {
		if (song == null) {
			return;
		}

		List<Song> songs = new ArrayList<Song>();
		songs.add(song);
		replacePlaylist(playlist, songs);
	}

	public static List<PlaylistMediaItem> appendPlaylist(Playlist playlist, Song song) {
		List<PlaylistMediaItem> appendPlaylistMediaItems = new ArrayList<PlaylistMediaItem>();
		if (song == null) {
			return appendPlaylistMediaItems;
		}

		List<Song> songs = new ArrayList<Song>();
		songs.add(song);
		appendPlaylistMediaItems = appendPlaylist(playlist, songs);
		return appendPlaylistMediaItems;
	}

}
