package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.service.LibraryManager.LibraryType;

public interface LibraryDao {

	public List<Library> getLibraries(LibraryType libraryType);

	public void saveLibrary(Library musicLibrary);

	public Library getLibrary(long id);

	public void deleteLibrary(Library library);

	public List<Library> getLibrariesForGroup(long groupId);

	public void saveRemoteShare(RemoteShare remoteShare);

	public RemoteShare getRemoteShare(Long remoteShareId);

	public List<Library> getRemoteLibraries();

}
