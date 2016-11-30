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
import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryStatusType;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.PhotoLibrary;
import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.model.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibraryUpdateManagerImpl implements LibraryUpdateManager {

	private Logger logger = Logger.getLogger(getClass());

	private final int LIBRARY_UPDATE_TIMEOUT_HOURS = 6;

	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	@Autowired
	private VideoLibraryUpdateManager videoLibraryUpdateManager;

	@Autowired
	private PhotoLibraryUpdateManager photoLibraryUpdateManager;

	@Autowired
	private MapperManager mapperManager;

	@Autowired
	private LibraryManager libraryManager;

	@Override
	public void updateLibrary(Library library) {

		library = libraryManager.getLibrary(library.getId());

		if (!library.isEnabled()) {
			logger.info("Library is disabled, will not update:" + library.toString());
			return;
		}

		Date date = new Date();
		date = DateUtils.addHours(date, -LIBRARY_UPDATE_TIMEOUT_HOURS);

		Date lastSuccessfulScanOn = library.getLastSuccessfulScanOn();
		if (lastSuccessfulScanOn == null) {
			lastSuccessfulScanOn = DateUtils.addHours(date, -LIBRARY_UPDATE_TIMEOUT_HOURS);
		}

		if (library.getLibraryStatusType() != LibraryStatusType.OK && date.before(lastSuccessfulScanOn)) {
			logger.info("Library is already updating, exiting:" + library.toString());
			return;
		}

		try {
			library.setLastSuccessfulScanOn(new Date());
			library.setLibraryStatusType(LibraryStatusType.WORKING);
			libraryManager.saveLibrary(library, true);

			Location location = library.getLocation();
			File folder = new File(location.getPath());
			if (!folder.isDirectory()) {
				logger.error("Media library points to a file not a directory, exiting...");
				return;
			}

			processLibrary(library);

			library.setLibraryStatusType(LibraryStatusType.OK);
			library.setLastSuccessfulScanOn(new Date());

		} catch (Exception e) {
			logger.error("Error updating library", e);
			library.setLibraryStatusType(LibraryStatusType.ERROR);
		} finally {
			libraryManager.saveLibrary(library);
		}

	}

	protected void processLibrary(Library library) throws Exception {
		Date date = new Date();
		long libraryId = library.getId();
		LibraryType libraryType = library.getLibraryType();
		mapperManager.writeStartRemoteMusicLibraryXml(libraryId, libraryType);
		Location location = library.getLocation();
		File locationFolder = new File(location.getPath());
		File[] files = locationFolder.listFiles();
		Arrays.sort(files);

		for (File file : files) {
			if (library instanceof MusicLibrary) {
				MusicLibrary musicLibrary = (MusicLibrary) library;
				musicLibraryUpdateManager.updateLibrary(musicLibrary, file, date);
			} else if (library instanceof VideoLibrary) {
				VideoLibrary videoLibrary = (VideoLibrary) library;
				videoLibraryUpdateManager.updateLibrary(videoLibrary, file, date);
			} else if (library instanceof PhotoLibrary) {
				PhotoLibrary photoLibrary = (PhotoLibrary) library;
				photoLibraryUpdateManager.updateLibrary(photoLibrary, file, date);
			}
		}

		deleteObsoleteMediaItems(library, date);
		mapperManager.writeEndRemoteMusicLibraryXml(libraryId);

	}

	@Override
	public void deleteObsoleteMediaItems(Library library, Date date) {
		long libraryId = library.getId();
		if (library instanceof MusicLibrary) {
			musicLibraryUpdateManager.deleteObsoleteSongs(libraryId, date);
		} else if (library instanceof VideoLibrary) {
			videoLibraryUpdateManager.deleteObsoleteVideos(libraryId, date);
		} else if (library instanceof PhotoLibrary) {
			photoLibraryUpdateManager.deleteObsoletePhotos(libraryId, date);
		}
	}

	@Override
	public void updateRemoteLibrary(Library library) {
		library = libraryManager.getLibrary(library.getId());

		if (!library.isEnabled()) {
			logger.info("Library is disabled, exiting..");
			return;
		}

		if (library.getLibraryStatusType() == LibraryStatusType.WORKING) {
			logger.info("Library is already updating, exiting:" + library.toString());
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
			logger.error("Error updating remote library", e);
			library.setLibraryStatusType(LibraryStatusType.ERROR);
		} finally {
			libraryManager.saveLibrary(library);
		}

	}

}
