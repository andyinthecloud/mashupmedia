package org.mashupmedia.model.library;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "music_libraries")
@Cacheable
@Getter
@Setter
public class MusicLibrary extends Library {
	private static final long serialVersionUID = -7310122657456811852L;
	
	private String albumArtImagePattern;

	@Override
	public LibraryType getLibraryType() {
		return LibraryType.MUSIC;
	}
	
}
