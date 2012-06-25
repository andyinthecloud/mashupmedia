package org.mashupmedia.model.library;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable
public class MusicLibrary extends Library {
	private static final long serialVersionUID = -7310122657456811852L;
	
	private String albumArtImagePattern;

	public String getAlbumArtImagePattern() {
		return albumArtImagePattern;
	}

	public void setAlbumArtImagePattern(String albumArtImagePattern) {
		this.albumArtImagePattern = albumArtImagePattern;
	}

}
