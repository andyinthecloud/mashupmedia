package org.mashupmedia.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.PhotoLibrary;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Lazy
@Service
@Transactional
@Slf4j
public class LibraryManagerImpl implements LibraryManager {
	@Autowired
	private LibraryDao libraryDao;
	
	@Autowired
	@Lazy
	private ConfigurationManager configurationManager;
	
	@Autowired
	@Lazy
	private MusicLibraryUpdateManager musicLibraryUpdateManager;
	
	@Autowired
	@Lazy
	private PhotoLibraryUpdateManager photoLibraryUpdateManager;
	
	@Autowired
	@Lazy
	private AdminManager adminManager;
	
	@Autowired
	@Lazy
	private LibraryWatchManager libraryWatchManager;

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
			log.error("No user found in session, using system user...");
			user = adminManager.getSystemUser();
		}

		Date date = new Date();
		long libraryId = library.getId();

		if (libraryId == 0) {
			library.setCreatedBy(user);
			library.setCreatedOn(date);
		} else {
			library = copyToExistingLibrary(library);
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

		libraryDao.saveLibrary(library);
	}

	protected Library copyToExistingLibrary(Library library) {
		long libraryId = library.getId();
		Assert.isTrue(libraryId > 0, "Library id should be greater than 0");
		Library savedLibrary = libraryDao.getLibrary(library.getId());

		savedLibrary.setName(library.getName());
		savedLibrary.getLocation().setPath(library.getLocation().getPath());
		savedLibrary.setEnabled(library.isEnabled());

		
		String status = library.getStatus();
		savedLibrary.setStatus(StringUtils.isEmpty(status) ? savedLibrary.getStatus() : status);

		Date lastSuccessfulScan = library.getLastSuccessfulScanOn();
		savedLibrary.setLastSuccessfulScanOn(lastSuccessfulScan == null ? savedLibrary.getLastSuccessfulScanOn() : lastSuccessfulScan);

		Set<Group> groups = savedLibrary.getGroups(); 
		groups.clear();
		groups.addAll(library.getGroups());
		savedLibrary.setGroups(groups);;

		return savedLibrary;
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
	public void deactivateLibrary(long libraryId) {
		Library library = libraryDao.getLibrary(libraryId);
		library.setEnabled(false);
		libraryDao.saveLibrary(library);
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
		libraryWatchManager.removeWatchLibraryListener(libraryId);
	}

	@Override
	public void saveMediaItemLastUpdated(long libraryId) {
		String key = LibraryHelper.getConfigurationLastUpdatedKey(libraryId);
		String value = String.valueOf(System.currentTimeMillis());
		configurationManager.saveConfiguration(key, value);
	}

	@Override
	public synchronized void saveMedia(long librayId, File file) {
		log.info("Saving media file: " + file.getAbsolutePath());

		Date date = new Date();

		if (!file.exists()) {
			return;
		}

		if (file.isDirectory()) {
			File[] filesInFolder = file.listFiles();
			for (File fileInFolder : filesInFolder) {
				saveMedia(librayId, fileInFolder);
			}
		}

		Library library = libraryDao.getLibrary(librayId);
		LibraryType libraryType = library.getLibraryType();

		if (libraryType == LibraryType.MUSIC) {
			musicLibraryUpdateManager.saveFile((MusicLibrary) library, file, date);
		} else if (libraryType == LibraryType.PHOTO) {
			photoLibraryUpdateManager.saveFile((PhotoLibrary) library, file, date);
		} else if (libraryType == LibraryType.VIDEO) {

		}

	}

	@Override
	public synchronized void deleteMedia(long librayId, File file) {
		log.info("Deleting media file: " + file.getAbsolutePath());
		Library library = libraryDao.getLibrary(librayId);
		LibraryType libraryType = library.getLibraryType();
		if (libraryType == LibraryType.MUSIC) {
			musicLibraryUpdateManager.deleteFile((MusicLibrary) library, file);
		} else if (libraryType == LibraryType.PHOTO) {
			photoLibraryUpdateManager.deleteFile((PhotoLibrary) library, file);
		} else if (libraryType == LibraryType.VIDEO) {

		}

	}

}
