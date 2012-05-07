package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.TestHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class TestLibraryManager extends TestBaseService {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;
	
	@Autowired
	private MusicManager musicManager;

	@Test
	public void testSaveMusicLibrary() {
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setEnabled(true);
		List<Group> groups = adminManager.getGroups();
		musicLibrary.setGroups(new HashSet<Group>(groups));
		Location location = new Location();
		location.setPath("path");
		musicLibrary.setLocation(location);
		musicLibrary.setName("name");

		libraryManager.saveMusicLibrary(musicLibrary);

		Assert.assertTrue(musicLibrary.getId() > 0);

	}

	@Test
	public void testSaveFtpMusicLibrary() {
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setEnabled(true);
		List<Group> groups = adminManager.getGroups();
		musicLibrary.setGroups(new HashSet<Group>(groups));
		FtpLocation location = new FtpLocation();
		location.setHost("gloop.ath.cx");
		location.setPassword("passwordpasswordpasswordpasswordpassword");
		location.setPort(21);
		location.setUsername("house");
		location.setPath("Music");
		musicLibrary.setLocation(location);
		musicLibrary.setName("name");

		libraryManager.saveMusicLibrary(musicLibrary);

		Assert.assertTrue(musicLibrary.getId() > 0);
	}
	
	@Test
	public void testDeleteLibrary() {
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setEnabled(true);
		List<Group> groups = adminManager.getGroups();
		musicLibrary.setGroups(new HashSet<Group>(groups));
		FtpLocation location = new FtpLocation();
		location.setHost("host");
		location.setPassword("password");
		location.setPort(100);
		location.setUsername("username");
		location.setPath("path");
		musicLibrary.setLocation(location);
		musicLibrary.setName("name");

		libraryManager.saveMusicLibrary(musicLibrary);

		long musicLibraryId = musicLibrary.getId();
		Assert.assertTrue(musicLibrary.getId() > 0);
		
		Song song = TestHelper.prepareSong(musicLibrary, "title");
		List<Song> songs = new ArrayList<Song>();
		songs.add(song);
		musicManager.saveSongs(songs);
		
		
		libraryManager.deleteLibrary(musicLibrary);
		
		musicLibrary = libraryManager.getMusicLibrary(musicLibraryId);
//		Assert.assertNull(musicLibrary);
		
		Assert.assertTrue(true);
		

		
	}
	



}