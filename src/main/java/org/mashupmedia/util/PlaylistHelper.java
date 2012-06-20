package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

public class PlaylistHelper {

	public static void replacePlaylist(Playlist playlist, List<? extends MediaItem> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems != null) {
			playlistMediaItems.clear();
		} else {
			playlistMediaItems = new ArrayList<PlaylistMediaItem>();
		}

		for (MediaItem song : songs) {
			PlaylistMediaItem playlistSong = new PlaylistMediaItem();
			playlistSong.setMediaItem(song);
			playlistSong.setPlaylist(playlist);
			playlistMediaItems.add(playlistSong);
		}
		
		playlistMediaItems.get(0).setPlaying(true);
		playlist.setPlaylistMediaItems(playlistMediaItems);
	}

}
