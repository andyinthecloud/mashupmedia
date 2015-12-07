package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dao.GroupDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MusicManagerImpl implements MusicManager {

	@Autowired
	private AlbumArtManager albumArtManager;

	@Autowired
	private MusicDao musicDao;

	@Autowired
	private PlaylistDao playlistDao;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private SecurityManager securityManager;
	
	protected enum ListAlbumsType {
		RANDOM, LATEST, ALL
	}

	@Override
	public List<Album> getAlbums(String searchLetter, int pageNumber, int totalItems) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Album> albums = musicDao.getAlbums(userGroupIds, searchLetter, pageNumber, totalItems);
		return albums;
	}

	@Override
	public List<String> getAlbumIndexLetters() {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<String> indexLetters = musicDao.getAlbumIndexLetters(userGroupIds);
		return indexLetters;
	}

	@Override
	public List<Artist> getArtists() {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Artist> artists = musicDao.getArtists(userGroupIds);
		for (Artist artist : artists) {
			Hibernate.initialize(artist.getAlbums());
		}
		return artists;
	}

	@Override
	public void saveAlbum(Album album) {
		musicDao.saveAlbum(album);
	}

	@Override
	public void saveArtist(Artist artist) {
		musicDao.saveArtist(artist);

	}

	@Override
	public List<String> getArtistIndexLetters() {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<String> indexLetters = musicDao.getArtistIndexLetters(userGroupIds);
		return indexLetters;
	}

	@Override
	public Album getAlbum(long albumId) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		Album album = musicDao.getAlbum(userGroupIds, albumId);
		if (album == null) {
			return null;
		}

		Hibernate.initialize(album.getSongs());
		return album;
	}

	@Override
	public List<Album> getRandomAlbums(int maxResults) {		
		List<Album> albums = getAlbums(ListAlbumsType.RANDOM, 0, maxResults);
		return albums;
	}
	
	@Override
	public List<Album> getLatestAlbums(int pageNumber, int maxResults) {
		List<Album> albums = getAlbums(ListAlbumsType.LATEST, pageNumber, maxResults);
		return albums;
	}

	
	protected List<Album> getAlbums(ListAlbumsType listAlbumsType, int pageNumber, int maxResults) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Album> albums = new ArrayList<Album>();
		
		List<Album> fetchedAlbums = null;
		
		if (listAlbumsType == ListAlbumsType.RANDOM) {
			fetchedAlbums = musicDao.getRandomAlbums(userGroupIds, maxResults);
		} else {
			fetchedAlbums = musicDao.getLatestAlbums(userGroupIds, pageNumber, maxResults);
		}		
		
		if (fetchedAlbums.isEmpty()) {
			return albums;
		}

		albums.addAll(fetchedAlbums);
		
		if (listAlbumsType != ListAlbumsType.RANDOM) {
			return albums;
		}
		

		while (maxResults > albums.size()) {
			int appendItemsTotal = fetchedAlbums.size();
			int numberOfAlbumsAfterAppend = albums.size() + appendItemsTotal;
			if (numberOfAlbumsAfterAppend > maxResults) {
				appendItemsTotal = maxResults - albums.size();
				fetchedAlbums = fetchedAlbums.subList(0, appendItemsTotal);
			}

			albums.addAll(fetchedAlbums);
		}

		return albums;
	}
	
	
	@Override
	public Album getAlbum(String artistName, String albumName) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		Album album = musicDao.getAlbum(userGroupIds, artistName, albumName);
		return album;
	}

	@Override
	public List<Album> getAlbumsByArtist(Long artistId) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Album> albums = musicDao.getAlbumsByArtist(userGroupIds, artistId);
		if (albums == null || albums.isEmpty()) {
			return albums;
		}

		for (Album album : albums) {
			Hibernate.initialize(album.getSongs());
		}

		return albums;
	}

	@Override
	public List<Song> getSongs(Long albumId) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Song> songs = musicDao.getSongs(userGroupIds, albumId);
		return songs;
	}


	@Override
	public Artist getArtist(Long artistId) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		Artist artist = musicDao.getArtist(userGroupIds, artistId);
		Hibernate.initialize(artist.getAlbums());
		return artist;
	}

	@Override
	public List<Genre> getGenres() {
		List<Genre> genres = musicDao.getGenres();
		return genres;
	}

	@Override
	public List<Song> findSongs(MediaItemSearchCriteria mediaItemSearchCriteria) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Song> songs = musicDao.findSongs(userGroupIds, mediaItemSearchCriteria);
		return songs;
	}

	@Override
	public long getTotalSongsFromLibrary(long libraryId) {
		long totalSongsFromLibrary = musicDao.getTotalSongsFromLibrary(libraryId);
		return totalSongsFromLibrary;
	}

}
