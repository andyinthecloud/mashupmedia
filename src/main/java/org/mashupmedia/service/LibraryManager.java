package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.library.Library;

public interface LibraryManager {

	public enum LibraryType {
		ALL, MUSIC
	}

	public List<? extends Library> getLibraries(LibraryType libraryType);

	public Library getLibrary(long id);

	public void deleteLibrary(Library library);

	public void saveLibrary(Library library);

	public List<Library> getLibrariesForGroup(long groupId);

	public void saveRemoteShares(Long[] remoteShareIds, String remoteShareStatus);

}
