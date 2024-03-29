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

	public List<Album> getAlbums(List<Long> groupIds, String searchLetter, int pageNumber, int maxResults);

	public List<Artist> getArtists(List<Long> groupIds);

	public Track getTrack(List<Long> groupIds, long libraryId, String trackPath, long fileLastModifiedOn);

	public Album getAlbum(List<Long> groupIds, String artistName, String albumName);

	public void saveTrack(Track track);

	public List<Track> getTracksToDelete(long libraryId, Date date);

	public void saveAlbum(Album album);

	public void saveArtist(Artist artist);

	public Album getAlbum(List<Long> groupIds, long albumId);

	public List<Album> getRandomAlbums(List<Long> groupIds, int maxResults);

	public Year getYear(int year);

	public List<Track> getTracks(List<Long> groupIds, Long albumId);


	public List<Album> getAlbumsByArtist(List<Long> groupIds, long artistId);

	public Genre getGenre(String name);

	public List<String> getArtistIndexLetters(List<Long> groupIds);

	public Artist getArtist(List<Long> groupIds, Long artistId);

	public void saveTrack(Track track, boolean isSessionFlush);

	public List<String> getAlbumIndexLetters(List<Long> groupIds);

	public List<Genre> getGenres();
	
	public List<Track> findTracks(List<Long> groupIds, MediaItemSearchCriteria mediaItemSearchCriteria);

	public long getTotalTracksFromLibrary(long libraryId);

	public List<Album> getLatestAlbums(List<Long> userGroupIds, int pageNumber, int maxResults);

	public Track getTrack(String path);
	
	
}
