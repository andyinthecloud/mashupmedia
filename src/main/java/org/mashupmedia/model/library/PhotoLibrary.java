package org.mashupmedia.model.library;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable
public class PhotoLibrary extends Library{

	private static final long serialVersionUID = -1183767759421059466L;

	@Override
	public LibraryType getLibraryType() {
		return LibraryType.PHOTO;
	}

}
