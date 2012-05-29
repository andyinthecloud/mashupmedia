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
		musicManager.saveSongs(musicLibrary, songs);
		

		songs = new ArrayList<Song>();
		Song song2 = TestHelper.prepareSong(musicLibrary, "song2");
		songs.add(song2);
		musicManager.saveSongs(musicLibrary, songs);
		
		String albumName = song2.getAlbum().getName();
		
		Album album = musicManager.getAlbum(albumName);
		Assert.assertTrue(album.getId() > 0);

	}
	
	@Test
	public void testGetRandomAlbums() {
		Location location = new Location();
		location.setPath("path");
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setLocation(location);
		musicLibrary.setName("test");
		libraryManager.saveMusicLibrary(musicLibrary);
		
		List<Song> songs = new ArrayList<Song>();
		Song song1 = TestHelper.prepareSong(musicLibrary, "song1");
		songs.add(song1);
		musicManager.saveSongs(musicLibrary, songs);

		songs = new ArrayList<Song>();
		Song song2 = TestHelper.prepareSong(musicLibrary, "song2");
		song2.getAlbum().setName("album2");
		songs.add(song2);
		musicManager.saveSongs(musicLibrary, songs);

		songs = new ArrayList<Song>();
		Song song3 = TestHelper.prepareSong(musicLibrary, "song2");
		song3.getAlbum().setName("album3");
		songs.add(song3);
		musicManager.saveSongs(musicLibrary, songs);
		
		
		int totalAlbums = 30;
		List<Album> albums = musicManager.getRandomAlbums(totalAlbums);
		Assert.assertEquals(totalAlbums, albums.size());
	}
	
}
