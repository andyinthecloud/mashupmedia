package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.RemoteShare;

public interface LibraryDao {

	public List<Library> getLocalLibraries(LibraryType libraryType);

	public void saveLibrary(Library musicLibrary, boolean isFlushSession);

	public Library getLibrary(long id);

	public void deleteLibrary(Library library);

	public List<Library> getLibrariesForGroup(long groupId);

	public void saveRemoteShare(RemoteShare remoteShare);

	public RemoteShare getRemoteShare(Long remoteShareId);

	public List<Library> getRemoteLibraries();

	public Library getRemoteLibrary(long libraryId);

	public Library getRemoteLibrary(String uniqueName);

	boolean hasRemoteLibrary(String url);

	public List<Library> getLibraries(LibraryType libraryType);

	public void reinitialiseLibrary(Library library);

}
