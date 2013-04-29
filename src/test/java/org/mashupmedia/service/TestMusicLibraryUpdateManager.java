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
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.remote.RemoteMusicLibrary;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper;

public class TestMusicLibraryUpdateManager extends TestBaseService {

	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	private LibraryManager libraryManager;

	public MusicManager musicManager;

	private AdminManager adminManager;

	@Test
	public void testSaveRemoteLibrary() throws Exception {

		MusicLibrary musicLibrary = new MusicLibrary();
		Location location = new Location();
		File musicDirectory = new File("");
		location.setPath(musicDirectory.getAbsolutePath());
		musicLibrary.setLocation(location);
		musicLibrary.setEnabled(true);

		Collection<Group> groups = adminManager.getGroups();
		musicLibrary.setGroups(new HashSet<Group>(groups));
		libraryManager.saveLibrary(musicLibrary);
		musicLibraryUpdateManager.updateLibrary(musicLibrary);

		File file = FileHelper.getLibraryXmlFile(musicLibrary.getId());

		MediaItemSearchCriteria criteria = new MediaItemSearchCriteria();
		criteria.setMaximumResults(100);
		List<MediaItem> mediaItems = musicManager.findSongs(criteria);

		int maxSongs = mediaItems.size();

		libraryManager.deleteLibrary(musicLibrary);

		String libraryXml = FileUtils.readFileToString(file);
		RemoteMusicLibrary remoteMusicLibrary;
		remoteMusicLibrary = MediaItemHelper.convertXmltoRemoteMusicLibrary(libraryXml);
		List<Song> songs = remoteMusicLibrary.getSongs();

		MusicLibrary musicLibrary2 = new MusicLibrary();
		musicLibraryUpdateManager.saveSongs(musicLibrary2, songs);
		mediaItems = musicManager.findSongs(criteria);

		Assert.assertEquals(maxSongs, mediaItems.size());
	}

}
