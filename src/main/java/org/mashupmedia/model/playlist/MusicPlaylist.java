package org.mashupmedia.model.playlist;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
@Cacheable
public class MusicPlaylist extends Playlist {

	@OneToMany(mappedBy = "musicPlaylist")
	@OrderBy("ranking")
	private List<PlaylistSong> playlistSongs;

	public List<PlaylistSong> getPlaylistSongs() {
		return playlistSongs;
	}

	public void setPlaylistSongs(List<PlaylistSong> playlistSongs) {
		this.playlistSongs = playlistSongs;
	}

	@Override
	public List<? extends PlaylistMediaItem> getPlaylistMediaItems() {
		return getPlaylistSongs();
	}

}
