/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.service;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryStatusType;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.PhotoLibrary;
import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.util.LibraryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LibraryUpdateManagerImpl implements LibraryUpdateManager {


	private final int LIBRARY_UPDATE_TIMEOUT_HOURS = 1;

	@Autowired
	@Lazy
	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	@Autowired
	@Lazy
	private VideoLibraryUpdateManager videoLibraryUpdateManager;

	@Autowired
	@Lazy
	private PhotoLibraryUpdateManager photoLibraryUpdateManager;

//	@Autowired
//	private MapperManager mapperManager;

	@Autowired
	@Lazy
	private LibraryManager libraryManager;

	@Autowired
	@Lazy
	private ConfigurationManager configurationManager;

	@Autowired
	@Lazy
	private LibraryWatchManager libraryWatchManager;

	@Override
	public synchronized void updateLibrary(Library library) {

		library = libraryManager.getLibrary(library.getId());

		if (!library.isEnabled()) {
			log.info("Library is disabled, will not update:" + library.toString());
			return;
		}

		Date date = new Date();
		date = DateUtils.addHours(date, -LIBRARY_UPDATE_TIMEOUT_HOURS);

		Date lastSavedMediaItemOn = configurationManager
				.getConfigurationDate(LibraryHelper.getConfigurationLastUpdatedKey(library.getId()));
		if (lastSavedMediaItemOn == null) {
			lastSavedMediaItemOn = DateUtils.addHours(date, -LIBRARY_UPDATE_TIMEOUT_HOURS);
		}

		if (library.getLibraryStatusType() != LibraryStatusType.OK && date.before(lastSavedMediaItemOn)) {
			log.info("Library is already updating, exiting:" + library.toString());
			return;
		}

		try {
			library.setLastSuccessfulScanOn(new Date());
			library.setLibraryStatusType(LibraryStatusType.WORKING);
			libraryManager.saveLibrary(library, true);

			Location location = library.getLocation();
			File folder = new File(location.getPath());
			if (!folder.isDirectory()) {
				log.error("Media library points to a file not a directory, exiting...");
				return;
			}

			processLibrary(library);

			library.setLibraryStatusType(LibraryStatusType.OK);
			library.setLastSuccessfulScanOn(new Date());

		} catch (Exception e) {
			log.error("Error updating library", e);
			library.setLibraryStatusType(LibraryStatusType.ERROR);
		} finally {
			libraryManager.saveLibrary(library);
		}
	}

	protected void processLibrary(Library library) throws Exception {
		Date updatingOn = new Date();
		long libraryId = library.getId();
		LibraryType libraryType = library.getLibraryType();
//		mapperManager.writeStartRemoteMusicLibraryXml(libraryId, libraryType);
		Location location = library.getLocation();
		File locationFolder = new File(location.getPath());
		File[] files = locationFolder.listFiles();
		Arrays.sort(files);

		for (File file : files) {
			if (library instanceof MusicLibrary) {
				MusicLibrary musicLibrary = (MusicLibrary) library;
				musicLibraryUpdateManager.updateLibrary(musicLibrary, file, updatingOn);
			} else if (library instanceof VideoLibrary) {
				VideoLibrary videoLibrary = (VideoLibrary) library;
				videoLibraryUpdateManager.updateLibrary(videoLibrary, file, updatingOn);
			} else if (library instanceof PhotoLibrary) {
				PhotoLibrary photoLibrary = (PhotoLibrary) library;
				photoLibraryUpdateManager.updateLibrary(photoLibrary, file, updatingOn);
			}
		}

		deleteObsoleteMediaItems(library, updatingOn);
//		mapperManager.writeEndRemoteMusicLibraryXml(libraryId);

	}

	@Override
	public void deleteObsoleteMediaItems(Library library, Date date) {

//		libraryWatchManager.removeWatchLibraryListeners();

		long libraryId = library.getId();
		if (library instanceof MusicLibrary) {
			musicLibraryUpdateManager.deleteObsoleteSongs(libraryId, date);
		} else if (library instanceof VideoLibrary) {
			videoLibraryUpdateManager.deleteObsoleteVideos(libraryId, date);
		} else if (library instanceof PhotoLibrary) {
			photoLibraryUpdateManager.deleteObsoletePhotos(libraryId, date);
		}

//		libraryWatchManager.registerWatchLibraryListeners();
	}

	@Override
	public void updateRemoteLibrary(Library library) {
		library = libraryManager.getLibrary(library.getId());

		if (!library.isEnabled()) {
			log.info("Library is disabled, exiting..");
			return;
		}

		if (library.getLibraryStatusType() == LibraryStatusType.WORKING) {
			log.info("Library is already updating, exiting:" + library.toString());
			return;
		}

		try {
			library.setLibraryStatusType(LibraryStatusType.WORKING);
			libraryManager.saveLibrary(library);

			if (library instanceof MusicLibrary) {
				musicLibraryUpdateManager.updateRemoteLibrary((MusicLibrary) library);
			}
			library.setLibraryStatusType(LibraryStatusType.OK);

		} catch (Exception e) {
			log.error("Error updating remote library", e);
			library.setLibraryStatusType(LibraryStatusType.ERROR);
		} finally {
			libraryManager.saveLibrary(library);
		}

	}

	@Override
	public void deleteLibrary(long libraryId) {
		libraryManager.deleteLibrary(libraryId);
	}

}
