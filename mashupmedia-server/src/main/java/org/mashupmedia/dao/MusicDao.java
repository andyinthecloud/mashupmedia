package org.mashupmedia.dao;

import java.util.Date;
import java.util.List;

import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;

public interface MusicDao {

	public List<Album> getAlbums(Long userId, String searchLetter, int pageNumber, int maxResults);

	public List<Artist> getArtists(Long userId);

	public Track getTrack(Long userId, long libraryId, String trackPath, long fileLastModifiedOn);

	public Album getAlbum(Long userId, String artistName, String albumName);

	public void saveTrack(Track track);

	public List<Track> getTracksToDelete(long libraryId, Date date);

	public void saveAlbum(Album album);

	public void saveArtist(Artist artist);

	public Album getAlbum(Long userId, long albumId);

	public List<Album> getRandomAlbums(Long userId, int maxResults);

	public Year getYear(int year);

	public List<Track> getTracks(Long userId, Long albumId);


	public List<Album> getAlbumsByArtist(Long userId, long artistId);

	public Genre getGenre(String name);

	public List<String> getArtistIndexLetters(Long userId);

	public Artist getArtist(Long userId, Long artistId);

	public void saveTrack(Track track, boolean isSessionFlush);

	public List<String> getAlbumIndexLetters(Long userId);

	public List<Genre> getGenres();
	
	public List<Track> findTracks(Long userId, MediaItemSearchCriteria mediaItemSearchCriteria);

	public long getTotalTracksFromLibrary(Long libraryId);

	public List<Album> getLatestAlbums(Long userId, int pageNumber, int maxResults);

	public Track getTrack(String path);
	
	
}
