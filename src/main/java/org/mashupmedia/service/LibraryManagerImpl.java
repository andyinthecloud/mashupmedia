package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LibraryManagerImpl implements LibraryManager {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private LibraryDao libraryDao;
	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;
	@Autowired
	private AdminManager adminManager;
	@Autowired	
	private LibraryUpdateManager libraryUpdateManager;

	@Override
	public List<Library> getLocalLibraries(LibraryType libraryType) {
		List<Library> musicLibraries = libraryDao.getLocalLibraries(libraryType);
		return musicLibraries;
	}

	@Override
	public List<Library> getLibraries(LibraryType libraryType) {
		List<Library> musicLibraries = libraryDao.getLibraries(libraryType);
		return musicLibraries;
	}

	@Override
	public List<Library> getLibrariesForGroup(long groupId) {
		List<Library> musicLibraries = libraryDao.getLibrariesForGroup(groupId);
		return musicLibraries;
	}

	@Override
	public void saveLibrary(Library library, boolean isFlushSession) {
		User user = AdminHelper.getLoggedInUser();
		if (user == null) {
			logger.error("No user found in session, using system user...");
			user = adminManager.getSystemUser();
		}

		Date date = new Date();
		long libraryId = library.getId();

		if (libraryId == 0) {
			library.setCreatedBy(user);
			library.setCreatedOn(date);
		}

		library.setUpdatedBy(user);
		library.setUpdatedOn(date);

		List<RemoteShare> remoteShares = library.getRemoteShares();
		if (remoteShares != null) {
			for (RemoteShare remoteShare : remoteShares) {
				if (remoteShare.getId() == 0) {
					remoteShare.setCreatedBy(user);
					remoteShare.setCreatedOn(new Date());
				}

				String uniqueName = remoteShare.getUniqueName();
				if (StringUtils.isBlank(uniqueName)) {
					remoteShare.setUniqueName(LibraryHelper.createUniqueName());
				}
			}
		}

		libraryDao.saveLibrary(library, isFlushSession);

	}

	
	@Override
	public void saveAndReinitialiseLibrary(Library library) {
		saveLibrary(library);
		libraryDao.reinitialiseLibrary(library);			
	}
	
	@Override
	public void saveLibrary(Library library) {
		saveLibrary(library, false);		
	}

	@Override
	public Library getLibrary(long id) {
		Library library = libraryDao.getLibrary(id);
		Hibernate.initialize(library.getRemoteShares());
		Hibernate.initialize(library.getGroups());
		return library;
	}

	@Override
	public Library getRemoteLibrary(String uniqueName) {
		Library remoteLibrary = libraryDao.getRemoteLibrary(uniqueName);
		if (remoteLibrary == null) {
			return null;
		}

		Hibernate.initialize(remoteLibrary.getRemoteShares());
		return remoteLibrary;
	}

	@Override
	public boolean hasRemoteLibrary(String url) {
		boolean hasRemoteLibrary = libraryDao.hasRemoteLibrary(url);
		return hasRemoteLibrary;
	}

	@Override
	public Library getRemoteLibrary(long libraryId) {
		Library library = libraryDao.getRemoteLibrary(libraryId);
		return library;
	}

	@Override
	public void deleteLibrary(Library library) {
		libraryDao.deleteLibrary(library);
		if (library instanceof MusicLibrary) {
			musicLibraryUpdateManager.deleteEmpty();
		}

		long libraryId = library.getId();
		FileHelper.deleteLibrary(libraryId);
	}

	@Override
	public void saveRemoteShares(Long[] remoteShareIds, String remoteShareStatus) {
		if (remoteShareIds == null) {
			return;
		}
		for (Long remoteShareId : remoteShareIds) {
			RemoteShare remoteShare = getRemoteShare(remoteShareId);
			remoteShare.setStatusType(remoteShareStatus);
			saveRemoteShare(remoteShare);
		}
	}

	private void saveRemoteShare(RemoteShare remoteShare) {
		libraryDao.saveRemoteShare(remoteShare);
	}

	private RemoteShare getRemoteShare(Long remoteShareId) {
		RemoteShare remoteShare = libraryDao.getRemoteShare(remoteShareId);
		return remoteShare;
	}

	@Override
	public List<Library> getRemoteLibraries() {
		List<Library> remoteLibraries = libraryDao.getRemoteLibraries();
		return remoteLibraries;
	}

}
