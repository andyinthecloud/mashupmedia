package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.watch.WatchLibraryListener;
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
	private LibraryUpdateManager libraryUpdateManager;
	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private AdminManager adminManager;

	private List<WatchLibraryListener> watchLibraryListeners;

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

		removeWatchLibraryListener(libraryId);
		libraryDao.saveLibrary(library);
		registerWatchLibraryListener(library);
	}

	@Override
	public void saveAndReinitialiseLibrary(Library library) {
		saveLibrary(library);
		libraryDao.reinitialiseLibrary(library);

		Date date = new Date();
		libraryUpdateManager.deleteObsoleteMediaItems(library, date);
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
	public void deactivateLibrary(long libraryId) {
		Library library = libraryDao.getLibrary(libraryId);
		library.setEnabled(false);
		libraryDao.saveLibrary(library);
		// TODO Auto-generated method stub

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

	@Override
	public void deleteLibrary(long libraryId) {
		FileHelper.deleteLibrary(libraryId);
		Library library = libraryDao.getLibrary(libraryId);
		libraryDao.deleteLibrary(library);
		removeWatchLibraryListener(libraryId);
	}

	@Override
	public void saveMediaItemLastUpdated(long libraryId) {
		String key = LibraryHelper.getConfigurationLastUpdatedKey(libraryId);
		String value = String.valueOf(System.currentTimeMillis());
		configurationManager.saveConfiguration(key, value);
	}

	@Override
	public void saveMedia(long librayId, File file) {
		logger.info("Saving media file: " + file.getAbsolutePath());
		Library library = libraryDao.getLibrary(librayId);

	}

	@Override
	public void deleteMedia(long librayId, File file) {
		logger.info("Deleting media file: " + file.getAbsolutePath());
		Library library = libraryDao.getLibrary(librayId);

	}

	@Override
	public void registerWatchLibraryListeners() {
		List<Library> libraries = getLocalLibraries(LibraryType.ALL);
		if (libraries == null || libraries.isEmpty()) {
			return;
		}

		for (Library library : libraries) {
			// Try to remove listener just in case it is already registered
			long libraryId = library.getId();
			removeWatchLibraryListener(libraryId);
			registerWatchLibraryListener(library);

		}

	}

	private void removeWatchLibraryListener(long libraryId) {
		if (watchLibraryListeners == null || watchLibraryListeners.isEmpty()) {
			return;
		}

		for (Iterator<WatchLibraryListener> iterator = watchLibraryListeners.iterator(); iterator.hasNext();) {
			WatchLibraryListener watchLibrary = (WatchLibraryListener) iterator.next();
			if (libraryId == watchLibrary.getLibrayId()) {
				watchLibrary.cancel();
				watchLibraryListeners.remove(watchLibrary);
				logger.info("Watch libray removed. Library id = " + libraryId);
				return;
			}
		}
	}

	private void registerWatchLibraryListener(Library library) {

		if (library.isRemote()) {
			return;
		}

		long libraryId = library.getId();
		Location location = library.getLocation();
		String path = location.getPath();
		registerWatchLibraryListener(libraryId, path);

	}

	private void registerWatchLibraryListener(long libraryId, String pathValue) {

		Path path = Paths.get(pathValue);

		try {
			WatchLibraryListener watchLibraryListener = new WatchLibraryListener(libraryId, path, this);
			addWatchLibraryListener(watchLibraryListener);
		} catch (IOException e) {
			logger.error("Error creating watch library", e);
		}

	}

	private void addWatchLibraryListener(WatchLibraryListener watchLibraryListener) {
		if (watchLibraryListeners == null) {
			watchLibraryListeners = new ArrayList<>();
		}

		watchLibraryListeners.add(watchLibraryListener);
	}
}
