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
import org.mashupmedia.exception.LibraryUpdateException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryStatusType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.PhotoLibrary;
import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.LibraryHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class LibraryUpdateManagerImpl implements LibraryUpdateManager {

	private final int LIBRARY_UPDATE_TIMEOUT_HOURS = 1;
	private final MusicLibraryUpdateManager musicLibraryUpdateManager;
	private final VideoLibraryUpdateManager videoLibraryUpdateManager;
	private final PhotoLibraryUpdateManager photoLibraryUpdateManager;
	private final LibraryManager libraryManager;
	private final ConfigurationManager configurationManager;
	private final AdminManager adminManager;

	@Override
	@Async
	public void asynchronousUpdateLibrary(long userId, long libraryId) {
		Assert.isTrue(userId > 0, "Expecting a user id");
		User user = adminManager.getUser(userId);
		AdminHelper.setLoggedInUser(user);

		Library library = libraryManager.getLibrary(libraryId);

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
			// library.setLastSuccessfulScanOn(new Date());
			library.setLibraryStatusType(LibraryStatusType.WORKING);
			libraryManager.saveLibrary(library, true);
			// libraryManager.saveLibrary(library, true);

			File folder = new File(library.getPath());
			if (!folder.isDirectory()) {
				throw new LibraryUpdateException("Media library points to a file not a directory, exiting...");
			}

			processLibrary(library);

			library.setLastSuccessfulScanOn(new Date());
			library.setLibraryStatusType(LibraryStatusType.OK);

		} catch (Exception e) {
			log.error("Error updating library", e);
			// library.setLastSuccessfulScanOn(new Date());
			library.setLibraryStatusType(LibraryStatusType.ERROR);
		} finally {
			libraryManager.saveLibrary(library, true);
		}
	}

	protected void processLibrary(Library library) throws Exception {
		Date updatingOn = new Date();
		// mapperManager.writeStartRemoteMusicLibraryXml(libraryId, libraryType);
		File locationFolder = new File(library.getPath());
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

		deleteObsoleteMediaItems(library.getId(), updatingOn);
		// mapperManager.writeEndRemoteMusicLibraryXml(libraryId);

	}

	@Override
	public void deleteObsoleteMediaItems(long libraryId, Date date) {

		Library library = libraryManager.getLibrary(libraryId);

		// libraryWatchManager.removeWatchLibraryListeners();

		if (library instanceof MusicLibrary) {
			musicLibraryUpdateManager.deleteObsoleteTracks(libraryId, date);
		} else if (library instanceof VideoLibrary) {
			videoLibraryUpdateManager.deleteObsoleteVideos(libraryId, date);
		} else if (library instanceof PhotoLibrary) {
			photoLibraryUpdateManager.deleteObsoletePhotos(libraryId, date);
		}

		// libraryWatchManager.registerWatchLibraryListeners();
	}

	@Override
	public void updateRemoteLibrary(long libraryId) {

		Library library = libraryManager.getLibrary(libraryId);

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
