package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.TestHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class TestMusicManager extends TestBaseService {
	@Autowired
	private MusicManager musicManager;

	@Autowired
	LibraryManager libraryManager;

	@Test
	public void testSaveMedia() {
		Location location = new Location();
		location.setPath("path");
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setLocation(location);
		musicLibrary.setName("test");
		libraryManager.saveMusicLibrary(musicLibrary);
		Assert.assertTrue(musicLibrary.getId() > 0);
		
		List<Song> songs = new ArrayList<Song>();
		Song song1 = TestHelper.prepareSong(musicLibrary, "song1");
		songs.add(song1);
		musicManager.saveSongs(songs);
		

		songs = new ArrayList<Song>();
		Song song2 = TestHelper.prepareSong(musicLibrary, "song2");
		songs.add(song2);
		musicManager.saveSongs(songs);
		
		String albumName = song2.getAlbum().getName();
		
		Album album = musicManager.getAlbum(albumName);
		Assert.assertTrue(album.getId() > 0);

	}
}
