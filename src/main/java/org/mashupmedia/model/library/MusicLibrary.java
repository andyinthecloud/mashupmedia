package org.mashupmedia.model.library;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable
public class MusicLibrary extends Library {
	private String albumArtImagePattern;

	public String getAlbumArtImagePattern() {
		return albumArtImagePattern;
	}

	public void setAlbumArtImagePattern(String albumArtImagePattern) {
		this.albumArtImagePattern = albumArtImagePattern;
	}

}
