package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.media.Artist;

public class ArtistsPage {
	
	private List<Artist> artists;
	private List<String> artistIndexLetters;
	
	

	public List<String> getArtistIndexLetters() {
		return artistIndexLetters;
	}

	public void setArtistIndexLetters(List<String> artistIndexLetters) {
		this.artistIndexLetters = artistIndexLetters;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}
	
	
	

}
