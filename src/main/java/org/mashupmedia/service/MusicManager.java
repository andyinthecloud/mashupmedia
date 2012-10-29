package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;

public interface MusicManager {

	public List<Album> getAlbums(String searchLetter, int pageNumber, int totalItems);

	public List<Artist> getArtists();

	public List<String> getArtistIndexLetters();

	public Album getAlbum(long albumId);

	public List<Album> getRandomAlbums(int numberOfAlbums);

	public Album getAlbum(String artistName, String albumName);

	public List<Song> getSongs(Long albumId);

	public void saveAlbum(Album album);

	public void deleteEmpty();

	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs);

	public Artist getArtist(Long artistId);

	public List<String> getAlbumIndexLetters();

	public void deleteObsoleteSongs(long libraryId, Date date);

	public List<Album> getAlbumsByArtist(Long artistId);

}
