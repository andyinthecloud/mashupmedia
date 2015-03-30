package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.TestHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class TestMusicManager extends TestBaseService {
	@Autowired
	private MusicManager musicManager;

	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;
	
	@Autowired
	LibraryManager libraryManager;

	@Test
	public void testSaveMedia() {
		Location location = new Location();
		location.setPath("path");
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setLocation(location);
		musicLibrary.setName("test");
		libraryManager.saveLibrary(musicLibrary);
		Assert.assertTrue(musicLibrary.getId() > 0);
		Date date =  new Date();
		
		List<Song> songs = new ArrayList<Song>();
		Song song1 = TestHelper.prepareSong(musicLibrary, "song1");
		songs.add(song1);
		musicLibraryUpdateManager.saveSongs(musicLibrary, songs, date);
		

		songs = new ArrayList<Song>();
		Song song2 = TestHelper.prepareSong(musicLibrary, "song2");
		songs.add(song2);
		musicLibraryUpdateManager.saveSongs(musicLibrary, songs, date);
		
		String artistName = song2.getArtist().getName();
		String albumName = song2.getAlbum().getName();
		
		Album album = musicManager.getAlbum(artistName, albumName);
		Assert.assertTrue(album.getId() > 0);

	}
	
	@Test
	public void testGetRandomAlbums() {
		Location location = new Location();
		location.setPath("path");
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setLocation(location);
		musicLibrary.setName("test");
		libraryManager.saveLibrary(musicLibrary);
		
		Date date = new Date();	
		
		List<Song> songs = new ArrayList<Song>();
		Song song1 = TestHelper.prepareSong(musicLibrary, "song1");
		songs.add(song1);
		musicLibraryUpdateManager.saveSongs(musicLibrary, songs, date);

		songs = new ArrayList<Song>();
		Song song2 = TestHelper.prepareSong(musicLibrary, "song2");
		song2.getAlbum().setName("album2");
		songs.add(song2);
		musicLibraryUpdateManager.saveSongs(musicLibrary, songs, date);

		songs = new ArrayList<Song>();
		Song song3 = TestHelper.prepareSong(musicLibrary, "song2");
		song3.getAlbum().setName("album3");
		songs.add(song3);
		musicLibraryUpdateManager.saveSongs(musicLibrary, songs, date);
		
		
		int totalAlbums = 30;
		List<Album> albums = musicManager.getRandomAlbums(totalAlbums);
		Assert.assertEquals(totalAlbums, albums.size());
	}
	
}
