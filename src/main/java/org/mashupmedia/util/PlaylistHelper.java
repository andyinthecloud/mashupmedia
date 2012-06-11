package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.playlist.MusicPlaylist;
import org.mashupmedia.model.playlist.PlaylistSong;

public class PlaylistHelper {

	public static void replaceMusicPlaylistSongs(MusicPlaylist playlist, List<Song> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		List<PlaylistSong> playlistSongs = playlist.getPlaylistSongs();
		if (playlistSongs != null) {
			playlistSongs.clear();
		} else {
			playlistSongs = new ArrayList<PlaylistSong>();
		}

		for (Song song : songs) {
			PlaylistSong playlistSong = new PlaylistSong();
			playlistSong.setSong(song);
			playlistSong.setPlaylist(playlist);
			playlistSongs.add(playlistSong);
		}

		playlist.setPlaylistSongs(playlistSongs);
	}

}
