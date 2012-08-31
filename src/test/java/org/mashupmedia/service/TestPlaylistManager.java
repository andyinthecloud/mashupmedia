package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.TestHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class TestPlaylistManager extends TestBaseService {
	
	@Autowired
	private PlaylistManager playlistManager;
	
	@Autowired
	private MusicManager musicManager;
	
	@Autowired
	private LibraryManager libraryManager;
	
	@Autowired
	private PlaylistDao playlistDao;
	
	@Autowired
	private MusicDao musicDao;
		
	@Test
	public void testSavePlaylist() {
		
		MusicLibrary musicLibrary = saveMusicLibrary();
		
		Playlist playlist = new Playlist();
		playlist.setName("test");
		
		List<PlaylistMediaItem> playlistMediaItems = new ArrayList<PlaylistMediaItem>();
		for (int i = 0; i < 20; i++) {
			PlaylistMediaItem playlistMediaItem = preparePlaylistMediaItem(musicLibrary, "playListMediaItem-" + i, playlist);
			playlistMediaItems.add(playlistMediaItem);			
		}
		
		int totalSongsInPlaylist = playlistMediaItems.size();
		
		playlist.setPlaylistMediaItems(playlistMediaItems);		
		playlistManager.savePlaylist(playlist);
				
		Assert.assertTrue("Playlist was not saved", playlist.getId() > 0);
		
		long playlistId = playlist.getId();
		playlist = playlistManager.getPlaylist(playlistId);
		playlistMediaItems = playlist.getPlaylistMediaItems();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			Assert.assertNotNull(playlistMediaItem.getPlaylist());
		}
		
		Assert.assertTrue("Playlist was not saved", totalSongsInPlaylist == playlistMediaItems.size());
		
		playlistManager.deletePlaylist(playlistId);
		
		playlist = playlistManager.getPlaylist(playlistId);
		Assert.assertNull(playlist);
		
		
	}
	
	
	protected MusicLibrary saveMusicLibrary() {
		Location location = new Location();
		location.setPath("path");
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setLocation(location);
		musicLibrary.setName("test");
		libraryManager.saveMusicLibrary(musicLibrary);
		Assert.assertTrue(musicLibrary.getId() > 0);
		return musicLibrary;


	}
	

	protected PlaylistMediaItem preparePlaylistMediaItem(MusicLibrary musicLibrary, String songName, Playlist playlist) {
		List<Song> songs = new ArrayList<Song>();
		Song song1 = TestHelper.prepareSong(musicLibrary, songName);
		songs.add(song1);
		musicManager.saveSongs(musicLibrary, songs);
		
		PlaylistMediaItem playlistMediaItem = new PlaylistMediaItem();
		playlistMediaItem.setMediaItem(song1);
		playlistMediaItem.setPlaylist(playlist);
		return playlistMediaItem;
	}

	
	
	@Test
	public void testDeleteSongsInPlaylist() {
		
		MusicLibrary musicLibrary = saveMusicLibrary();
		
		Playlist playlist = new Playlist();
		playlist.setName("test");
		
		List<PlaylistMediaItem> playlistMediaItems = new ArrayList<PlaylistMediaItem>();
		for (int i = 0; i < 20; i++) {
			PlaylistMediaItem playlistMediaItem = preparePlaylistMediaItem(musicLibrary, "playListMediaItem-" + i, playlist);
			playlistMediaItems.add(playlistMediaItem);
			
			Assert.assertTrue(playlistMediaItem.getMediaItem().getId() > 0);
		}
		
		int totalSongsInPlaylist = playlistMediaItems.size();
		
		playlist.setPlaylistMediaItems(playlistMediaItems);		
		playlistManager.savePlaylist(playlist);
		
		long playlistId = playlist.getId();
		
		
		List<PlaylistMediaItem> playlistMediaItemsToDelete = playlistMediaItems.subList(0, totalSongsInPlaylist / 2);
		int remainingPlaylistMediaItems = playlistMediaItems.size() - playlistMediaItemsToDelete.size();
		List<Song> songsToDelete = new ArrayList<Song>();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItemsToDelete) {
			songsToDelete.add((Song)playlistMediaItem.getMediaItem());
		}
		
		
		
		
		
//		playlistDao.deletePlaylistMediaItems(songsToDelete);
		musicDao.deleteSongs(songsToDelete);
		
//		mediaDao.deleteMediaList(mediaItemsToDelete);
		
		playlist = playlistManager.getPlaylist(playlistId);
		playlistMediaItems = playlist.getPlaylistMediaItems();
		Assert.assertEquals(remainingPlaylistMediaItems, playlistMediaItems.size());
		
		
	}
}
