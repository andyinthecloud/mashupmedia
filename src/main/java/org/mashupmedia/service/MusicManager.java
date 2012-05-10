package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;

public interface MusicManager {

	public List<Album> getAlbums();

	public List<Artist> getArtists();

//	public void saveArtists(Library library, List<Artist> artists);

	public List<String> getArtistIndexLetters();

	public Album getAlbum(long albumId);

	public List<Album> getRandomAlbums();

//	public void saveSongs(List<Song> songs);

	public Album getAlbum(String name);

	public List<Song> getSongs(Long albumId);

	public void saveAlbum(Album album);

	public void deleteEmpty();

	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs);
	
	

}
