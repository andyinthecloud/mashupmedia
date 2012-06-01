package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Song;

public class AlbumPage {

	private Album album;

	private List<Song> songs;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}

}
