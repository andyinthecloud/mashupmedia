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
import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Test;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.util.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class TestMusicLibraryUpdateManager extends TestBaseService {

	// private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private LibraryUpdateManager libraryUpdateManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	public MusicManager musicManager;

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private MapperManager mapperManager;

	@Test
	public void testSaveRemoteLibrary() throws Exception {

		MusicLibrary musicLibrary = new MusicLibrary();
		Location location = new Location();
		File musicDirectory = new File(testResourceBundle.getString("test.music.folder"));
		location.setPath(musicDirectory.getAbsolutePath());
		musicLibrary.setLocation(location);
		musicLibrary.setEnabled(true);

		Collection<Group> groups = adminManager.getGroups();
		musicLibrary.setGroups(new HashSet<Group>(groups));
		libraryManager.saveLibrary(musicLibrary);
		libraryUpdateManager.updateLibrary(musicLibrary);

		File file = FileHelper.getLibraryXmlFile(musicLibrary.getId());
		
		long totalSavedSongs = musicManager.getTotalSongsFromLibrary(musicLibrary.getId());

		Location location2 = new Location();
		location2.setPath(file.getAbsolutePath());
		MusicLibrary musicLibrary2 = new MusicLibrary();
		musicLibrary2.setLocation(location2);
		musicLibrary2.setEnabled(true);
		musicLibrary2.setRemote(true);

		libraryManager.deleteLibrary(musicLibrary);

		libraryManager.saveLibrary(musicLibrary2);
		
		libraryUpdateManager.updateRemoteLibrary(musicLibrary2);
				
		long totalRemoteSongs = musicManager.getTotalSongsFromLibrary(musicLibrary2.getId());

		Assert.assertEquals(totalSavedSongs, totalRemoteSongs);
	}

}
