package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.media.Album;

public class AlbumsPage {

	private List<String> albumIndexLetters;
	private List<Album> albums;

	public List<String> getAlbumIndexLetters() {
		return albumIndexLetters;
	}

	public void setAlbumIndexLetters(List<String> albumIndexLetters) {
		this.albumIndexLetters = albumIndexLetters;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

}
