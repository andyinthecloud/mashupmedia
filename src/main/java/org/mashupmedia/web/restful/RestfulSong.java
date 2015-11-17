package org.mashupmedia.web.restful;

import org.mashupmedia.model.media.music.Song;

public class RestfulSong extends RestfulMediaItem{
	
	private String title;
	private String artistName;
	private String artistUrl;
	private String albumName;
	private String albumUrl;
	
	public RestfulSong(Song song) {
		
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistUrl() {
		return artistUrl;
	}

	public void setArtistUrl(String artistUrl) {
		this.artistUrl = artistUrl;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getAlbumUrl() {
		return albumUrl;
	}

	public void setAlbumUrl(String albumUrl) {
		this.albumUrl = albumUrl;
	}
	
	
	

}
