package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Song;

public interface MusicManager {

	public List<Album> getAlbums(String searchLetter, int pageNumber, int maxResults);

	public List<Artist> getArtists();

	public List<String> getArtistIndexLetters();

	public Album getAlbum(long albumId);

	public List<Album> getRandomAlbums(int maxResults);

	public Album getAlbum(String artistName, String albumName);

	public List<Song> getSongs(Long albumId);

	public void saveAlbum(Album album);

	public Artist getArtist(Long artistId);

	public Artist getArtist(Long artistId, boolean isFullyInitialise);

	public List<String> getAlbumIndexLetters();

	public List<Album> getAlbumsByArtist(Long artistId);

	public List<Genre> getGenres();

	public List<Song> findSongs(MediaItemSearchCriteria mediaItemSearchCriteria);

	public void saveArtist(Artist artist);

	public long getTotalSongsFromLibrary(long libraryId);

	public List<Album> getLatestAlbums(int pageNumber, int maxResults);
	
}
