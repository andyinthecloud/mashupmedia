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
import java.util.Date;

import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryStatusType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibraryUpdateManagerImpl implements LibraryUpdateManager {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	@Autowired
	private MapperManager mapperManager;

	@Autowired
	private LibraryManager libraryManager;

	@Override
	public void updateLibrary(Library library) {

		if (!library.isEnabled()) {
			logger.info("Library is disabled, will not update:" + library.toString());
			return;
		}

		if (library.getLibraryStatusType() == LibraryStatusType.WORKING) {
			logger.info("Library is already updating, exiting:" + library.toString());
			return;
		}

		try {
			library.setLibraryStatusType(LibraryStatusType.WORKING);
			libraryManager.saveLibrary(library);

			Location location = library.getLocation();
			File folder = new File(location.getPath());
			if (!folder.isDirectory()) {
				logger.error("Media library points to a file not a directory, exiting...");
				return;
			}

			if (library instanceof MusicLibrary) {
				updateMusicLibrary((MusicLibrary) library);
			}

			library.setLibraryStatusType(LibraryStatusType.OK);

		} catch (Exception e) {
			logger.error("Error updating library", e);
			library.setLibraryStatusType(LibraryStatusType.ERROR);
		} finally {
			libraryManager.saveLibrary(library);
		}

	}

	protected void updateMusicLibrary(MusicLibrary library) throws Exception {
		Date date = new Date();
		long libraryId = library.getId();
		mapperManager.writeStartRemoteMusicLibraryXml(libraryId);
		Location location = library.getLocation();
		File locationFolder = new File(location.getPath());
		File[] files = locationFolder.listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				continue;
			}
			musicLibraryUpdateManager.updateLibrary(library, file, date);

		}

		mapperManager.writeEndRemoteMusicLibraryXml(libraryId);

		musicLibraryUpdateManager.deleteObsoleteSongs(libraryId, date);

	}

	@Override
	public void updateRemoteLibrary(Library library) {
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
