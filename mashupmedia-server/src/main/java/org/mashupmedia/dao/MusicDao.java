package org.mashupmedia.dao;

import java.util.Date;
import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Song;

public interface MusicDao {

	public List<Album> getAlbums(List<Long> groupIds, String searchLetter, int pageNumber, int maxResults);

	public List<Artist> getArtists(List<Long> groupIds);

	public Song getSong(List<Long> groupIds, long libraryId, String songPath, long fileLastModifiedOn);

	public Album getAlbum(List<Long> groupIds, String artistName, String albumName);

	public void saveSong(Song song);

//	public void deleteSongs(List<Song> songsToDelete);
	public void deleteSong(Song song);

	public List<Song> getSongsToDelete(long libraryId, Date date);

	public void saveAlbum(Album album);

	public void saveArtist(Artist artist);

	public Album getAlbum(List<Long> groupIds, long albumId);

	public List<Album> getRandomAlbums(List<Long> groupIds, int maxResults);

	public Year getYear(int year);

	public List<Song> getSongs(List<Long> groupIds, Long albumId);


	public List<Album> getAlbumsByArtist(List<Long> groupIds, long artistId);

	public Genre getGenre(String name);

	public List<String> getArtistIndexLetters(List<Long> groupIds);

	public Artist getArtist(List<Long> groupIds, Long artistId);

	public void saveSong(Song song, boolean isSessionFlush);

	public List<String> getAlbumIndexLetters(List<Long> groupIds);

	public List<Genre> getGenres();
	
	public List<Song> findSongs(List<Long> groupIds, MediaItemSearchCriteria mediaItemSearchCriteria);

	public long getTotalSongsFromLibrary(long libraryId);

//	public void deleteObsoleteSongs(List<Song> songsToDelete);
	public void deleteObsoleteSong(Song songToDelete);

	public void deleteEmptyAlbums();

	public void deleteEmptyArtists();

	public List<Album> getLatestAlbums(List<Long> userGroupIds, int pageNumber, int maxResults);

	public Song getSong(String path);
	
	
}
