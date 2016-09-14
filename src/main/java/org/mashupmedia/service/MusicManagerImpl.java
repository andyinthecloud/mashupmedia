package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.mashupmedia.comparator.MediaItemComparator;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MusicManagerImpl implements MusicManager {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MusicDao musicDao;

	@Autowired
	private SecurityManager securityManager;

	@Autowired
	private AdminManager adminManager;

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

		List<Song> songs = album.getSongs();
		Hibernate.initialize(songs);
		Collections.sort(songs, new MediaItemComparator());
		
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
	public Artist getArtist(Long artistId, boolean isFullyInitialise) {
		User user = AdminHelper.getLoggedInUser();

		if (!isFullyInitialise && user == null) {
			logger.error("No user found in session, using system user...");
			user = adminManager.getSystemUser();
		}

		List<Long> userGroupIds = securityManager.getUserGroupIds(user);
		Artist artist = musicDao.getArtist(userGroupIds, artistId);
		if (!isFullyInitialise) {
			return artist;
		}

		Hibernate.initialize(artist.getAlbums());
		return artist;
	}

	@Override
	public Artist getArtist(Long artistId) {
		Artist artist = getArtist(artistId, true);
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
