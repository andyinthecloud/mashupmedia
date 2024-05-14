package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;

public interface LibraryDao {

	public List<Library> getLocalLibraries(LibraryType libraryType);

	public void saveLibrary(Library library);

	public Library getLibrary(long id);

	public void deleteLibrary(Library library);

	public List<Library> getLibrariesForGroup(long groupId);

	public List<Library> getLibraries();

	public List<Library> getLibraries(String username);

	public void reinitialiseLibrary(Library library);
	
	public long getTotalMediaItemsFromLibrary(long libraryId);

}
