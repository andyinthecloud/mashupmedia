package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
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
		
		
		playlist = playlistManager.getPlaylist(playlist.getId());
		playlistMediaItems = playlist.getPlaylistMediaItems();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			Assert.assertNotNull(playlistMediaItem.getPlaylist());
		}
		
		Assert.assertTrue("Playlist was not saved", totalSongsInPlaylist == playlistMediaItems.size());
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

}
