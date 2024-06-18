package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.exception.ContainsMediaItemsException;
import org.mashupmedia.exception.NameNotUniqueException;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;

public interface MusicManager {

	public List<Album> getAlbums(String searchLetter, int pageNumber, int maxResults);

	public List<Artist> getArtists();

	public List<String> getArtistIndexLetters();

	public Album getAlbum(long albumId);

	public List<Album> getRandomAlbums(int maxResults);

	public Album getAlbum(String artistName, String albumName);

	public List<Track> getTracks(Long albumId);

	public void saveAlbum(Album album) throws NameNotUniqueException;

	public Artist getArtist(Long artistId);

	public Artist getArtist(Long artistId, boolean isFullyInitialise);

	public List<String> getAlbumIndexLetters();

	public List<Album> getAlbumsByArtist(Long artistId);

	public List<Genre> getGenres();

	public List<Track> findTracks(MediaItemSearchCriteria mediaItemSearchCriteria);

	public Artist saveArtist(Artist artist);

	public long getTotalTracksFromLibrary(long libraryId);

	public List<Album> getLatestAlbums(int pageNumber, int maxResults);

	public void deleteArtist(long artistId) throws ContainsMediaItemsException;

    public Artist getArtist(String name);

    public void deleteAlbum(long albumId) throws ContainsMediaItemsException;
	
}
