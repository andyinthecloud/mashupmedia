package org.mashupmedia.dao;

import java.util.Date;
import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;

public interface MusicDao {

	public List<Album> getAlbums();

	public List<Artist> getArtists();

	public Artist getArtist(String name);

	public Song getSong(long libraryId, String songPath, long songSizeInBytes);

	public Album getAlbum(String name);

	public void saveSong(Song song);

	public void deleteSongs(List<Song> songsToDelete);

	public List<Song> getSongsToDelete(long libraryId, Date date);

	public void saveAlbum(Album album);

	public void saveArtist(Artist artist);

	public Album getAlbum(long albumId);

	public List<Album> getRandomAlbums(int numberOfAlbums);

	public Year getYear(int year);

	public List<Song> getSongs(Long albumId);

	public void deleteArtist(Artist artist);

	public void deleteAlbum(Album album);

	public List<Album> getAlbumsByArtist(long artistId);

	public Genre getGenre(String name);
	

}
