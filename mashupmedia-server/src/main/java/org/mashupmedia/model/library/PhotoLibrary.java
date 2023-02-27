package org.mashupmedia.model.library;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "photo_libraries")
@Cacheable
public class PhotoLibrary extends Library{

	private static final long serialVersionUID = -1183767759421059466L;

	@Override
	public LibraryType getLibraryType() {
		return LibraryType.PHOTO;
	}

}
