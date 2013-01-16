package org.mashupmedia.dao;

import java.util.Date;
import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;

public interface MusicDao {

	public List<Album> getAlbums(List<Long> groupIds, String searchLetter, int pageNumber, int totalItems);

	public List<Artist> getArtists(List<Long> groupIds);

	public Artist getArtist(List<Long> groupIds, String name);

	public Song getSong(List<Long> groupIds, long libraryId, String songPath, long songSizeInBytes);

	public Album getAlbum(List<Long> groupIds, String artistName, String albumName);

	public void saveSong(Song song);

	public void deleteSongs(List<Song> songsToDelete);

	public List<Song> getSongsToDelete(long libraryId, Date date);

	public void saveAlbum(Album album);

	public void saveArtist(Artist artist);

	public Album getAlbum(List<Long> groupIds, long albumId);

	public List<Album> getRandomAlbums(List<Long> groupIds, int numberOfAlbums);

	public Year getYear(int year);

	public List<Song> getSongs(List<Long> groupIds, Long albumId);

	public void deleteArtist(Artist artist);

	public void deleteAlbum(Album album);

	public List<Album> getAlbumsByArtist(List<Long> groupIds, long artistId);

	public Genre getGenre(String name);

	public List<String> getArtistIndexLetters(List<Long> groupIds);

	public Artist getArtist(List<Long> groupIds, Long artistId);

	public void saveSong(Song song, boolean isSessionFlush);

	public List<String> getAlbumIndexLetters(List<Long> groupIds);

	public List<Genre> getGenres();
	
	public List<MediaItem> findSongs(List<Long> groupIds, MediaItemSearchCriteria mediaItemSearchCriteria);
	
}
