package org.mashupmedia.service;

import java.io.File;
import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;

public interface LibraryManager {

	public List<Library> getLocalLibraries(LibraryType libraryType);

	public Library getLibrary(long id);

	public void saveLibrary(Library library);

	public void saveLibrary(Library library, boolean isFlushSession);

	public List<Library> getLibrariesForGroup(long groupId);

	public void saveRemoteShares(Long[] remoteShareIds, String remoteShareStatus);

	public List<Library> getRemoteLibraries();

	public Library getRemoteLibrary(long libraryId);

	public Library getRemoteLibrary(String uniqueName);

	public boolean hasRemoteLibrary(String url);

	public List<Library> getLibraries(LibraryType libraryType);

	public void saveAndReinitialiseLibrary(Library library);

	public void deactivateLibrary(long libraryId);

	public void deleteLibrary(long libraryId);

	public void saveMediaItemLastUpdated(long libraryId);

	public void saveMedia(long librayId, File file);

	public void deleteMedia(long librayId, File file);


}
