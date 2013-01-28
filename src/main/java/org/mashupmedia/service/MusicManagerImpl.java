package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dao.GroupDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.SecurityHelper;
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

	@Override
	public List<Album> getAlbums(String searchLetter, int pageNumber, int totalItems) {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		List<Album> albums = musicDao.getAlbums(userGroupIds, searchLetter, pageNumber, totalItems);
		return albums;
	}

	@Override
	public List<String> getAlbumIndexLetters() {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		List<String> indexLetters = musicDao.getAlbumIndexLetters(userGroupIds);
		return indexLetters;
	}

	@Override
	public List<Artist> getArtists() {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		List<Artist> artists = musicDao.getArtists(userGroupIds);
		for (Artist artist : artists) {
			Hibernate.initialize(artist.getAlbums());
		}
		return artists;
	}

	public void saveAlbum(Album album) {
		musicDao.saveAlbum(album);
	}

	protected void saveArtist(Artist artist) {
		musicDao.saveArtist(artist);

	}

	@Override
	public List<String> getArtistIndexLetters() {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		List<String> indexLetters = musicDao.getArtistIndexLetters(userGroupIds);
		return indexLetters;
	}

	@Override
	public Album getAlbum(long albumId) {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		Album album = musicDao.getAlbum(userGroupIds, albumId);
		if (album == null) {
			return null;
		}
		
		Hibernate.initialize(album.getSongs());
		return album;
	}

	@Override
	public List<Album> getRandomAlbums(int numberOfAlbums) {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		List<Album> albums = new ArrayList<Album>();
		List<Album> randomAlbums = musicDao.getRandomAlbums(userGroupIds, numberOfAlbums);
		if (randomAlbums.isEmpty()) {
			return albums;
		}

		albums.addAll(randomAlbums);

		while (numberOfAlbums > albums.size()) {
			int appendItemsTotal = randomAlbums.size();
			int numberOfAlbumsAfterAppend = albums.size() + appendItemsTotal;
			if (numberOfAlbumsAfterAppend > numberOfAlbums) {
				appendItemsTotal = numberOfAlbums - albums.size();
				randomAlbums = randomAlbums.subList(0, appendItemsTotal);
			}

			albums.addAll(randomAlbums);
		}

		return albums;
	}

	@Override
	public Album getAlbum(String artistName, String albumName) {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		Album album = musicDao.getAlbum(userGroupIds, artistName, albumName);
		return album;
	}

	@Override
	public List<Album> getAlbumsByArtist(Long artistId) {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
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
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		List<Song> songs = musicDao.getSongs(userGroupIds, albumId);
		return songs;
	}

	@Override
	public Artist getArtist(Long artistId) {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
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
	public List<MediaItem> findSongs(MediaItemSearchCriteria mediaItemSearchCriteria) {
		List<Long> userGroupIds = SecurityHelper.getLoggedInUserGroupIds();
		List<MediaItem> mediaItems = musicDao.findSongs(userGroupIds, mediaItemSearchCriteria);
		return mediaItems;
	}
}
