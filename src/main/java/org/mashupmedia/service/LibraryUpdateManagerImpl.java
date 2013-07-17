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

import org.apache.log4j.Logger;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibraryUpdateManagerImpl implements LibraryUpdateManager{
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;
	
	@Autowired
	private MapperManager mapperManager;

	@Override
	public void updateLibrary(Library library) {
		
		if (!library.isEnabled()) {
			logger.info("Library is disabled, will not update:" + library.toString());
			return;
		}
		
		Location location = library.getLocation();
		File folder = new File(location.getPath());
		if (!folder.isDirectory()) {
			logger.error("Media library points to a file not a directory, exiting...");
			return;
		}
		
		if (library instanceof MusicLibrary) {			
			try {
				updateMusicLibrary((MusicLibrary) library);
			} catch (Exception e) {
				throw new MashupMediaRuntimeException("Error updating library", e);
			}
		}
		
		
	}

	protected void updateMusicLibrary(MusicLibrary library) throws Exception {
		long libraryId = library.getId();
		mapperManager.writeStartRemoteMusicLibraryXml(libraryId);

		Location location = library.getLocation();
		
		
//		prepareSongs(date, songs, musicFolder, musicLibrary, null, null);
		
		
		
		File locationFolder = new File(location.getPath());
		File[] files = locationFolder.listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				continue;
			}
			musicLibraryUpdateManager.updateLibrary(library, file);
			
		}
		
		mapperManager.writeEndRemoteMusicLibraryXml(libraryId);
		
	}
	
	@Override
	public void updateRemoteLibrary(MusicLibrary musicLibrary) {		
		try {
			musicLibraryUpdateManager.updateRemoteLibrary(musicLibrary);
		} catch (Exception e) {
			throw new MashupMediaRuntimeException("Error updating remote library", e);
		}		
	}

}
