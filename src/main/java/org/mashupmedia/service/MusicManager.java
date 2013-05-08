package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
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

	public Artist getArtist(Long artistId);

	public List<String> getAlbumIndexLetters();

	public List<Album> getAlbumsByArtist(Long artistId);

	public List<Genre> getGenres();

	public List<Song> findSongs(MediaItemSearchCriteria mediaItemSearchCriteria);

	public void saveArtist(Artist artist);

	public int getTotalSongsFromLibrary(long libraryId);
	
}
