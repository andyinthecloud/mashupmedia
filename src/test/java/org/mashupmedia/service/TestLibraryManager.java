package org.mashupmedia.service;

import junit.framework.Assert;

import org.junit.Test;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.springframework.beans.factory.annotation.Autowired;

public class TestLibraryManager extends TestBaseService {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;
	
	
	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	@Test
	public void testSaveMusicLibrary() {
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setEnabled(true);
		Location location = new Location();
		location.setPath("path");
		musicLibrary.setLocation(location);
		musicLibrary.setName("name");

		libraryManager.saveLibrary(musicLibrary);

		Assert.assertTrue(musicLibrary.getId() > 0);

	}

	
//	@Test
//	public void testDeleteLibrary() {
//		MusicLibrary musicLibrary = new MusicLibrary();
//		musicLibrary.setEnabled(true);
//		FtpLocation location = new FtpLocation();
//		location.setHost("host");
//		location.setPassword("password");
//		location.setPort(100);
//		location.setUsername("username");
//		location.setPath("path");
//		musicLibrary.setLocation(location);
//		musicLibrary.setName("name");
//
//		libraryManager.saveLibrary(musicLibrary);
//
//		long musicLibraryId = musicLibrary.getId();
//		Assert.assertTrue(musicLibrary.getId() > 0);
//		
//		Song song = TestHelper.prepareSong(musicLibrary, "title");
//		List<Song> songs = new ArrayList<Song>();
//		songs.add(song);
//		musicLibraryUpdateManager.saveSongs(musicLibrary, songs);
//		
//		
//		libraryManager.deleteLibrary(musicLibrary);
//		
//		musicLibrary = (MusicLibrary) libraryManager.getLibrary(musicLibraryId);
//		
//		Assert.assertTrue(true);
//		
//	}
	



}
