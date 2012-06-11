package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.playlist.Playlist;

public class MusicPage {
	private List<Album> albums;
	private Playlist playlist;

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

}
