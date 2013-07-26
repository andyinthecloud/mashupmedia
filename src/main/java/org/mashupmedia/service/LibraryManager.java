package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.service.LibraryManager.LibraryType;

public interface LibraryManager {

	public enum LibraryType {
		ALL, MUSIC
	}

	public List<? extends Library> getLocalLibraries(LibraryType libraryType);

	public Library getLibrary(long id);

	public void deleteLibrary(Library library);

	public void saveLibrary(Library library);

	public List<Library> getLibrariesForGroup(long groupId);

	public void saveRemoteShares(Long[] remoteShareIds, String remoteShareStatus);

	public List<Library> getRemoteLibraries();

	public Library getRemoteLibrary(long libraryId);

	public Library getRemoteLibrary(String uniqueName);

	public boolean hasRemoteLibrary(String url);

	public List<Library> getLibraries(LibraryType libraryType);
	
	

}
