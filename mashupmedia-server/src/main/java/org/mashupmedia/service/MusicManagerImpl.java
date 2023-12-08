package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
@Slf4j
@RequiredArgsConstructor
public class MusicManagerImpl implements MusicManager {

	private final MusicDao musicDao;
	private final MashupMediaSecurityManager securityManager;
	private final AdminManager adminManager;
	private final ArtistRepository artistRepository;
	private final LibraryManager libraryManager;


	protected enum ListAlbumsType {
		RANDOM, LATEST, ALL
	}

	@Override
	public List<Album> getAlbums(String searchLetter, int pageNumber, int totalItems) {
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		List<Album> albums = musicDao.getAlbums(loggedInUserId, searchLetter, pageNumber, totalItems);
		return albums;
	}

	@Override
	public List<String> getAlbumIndexLetters() {
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		List<String> indexLetters = musicDao.getAlbumIndexLetters(loggedInUserId);
		return indexLetters;
	}

	@Override
	public List<Artist> getArtists() {
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		List<Artist> artists = musicDao.getArtists(loggedInUserId);
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
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		List<String> indexLetters = musicDao.getArtistIndexLetters(loggedInUserId);
		return indexLetters;
	}

	@Override
	public Album getAlbum(long albumId) {
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		Album album = musicDao.getAlbum(loggedInUserId, albumId);
		if (album == null) {
			return null;
		}

		List<Track> tracks = album.getTracks();
		if (tracks == null || tracks.isEmpty()) {
			return null;
		}		

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
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		List<Album> albums = new ArrayList<Album>();

		List<Album> fetchedAlbums = null;

		if (listAlbumsType == ListAlbumsType.RANDOM) {
			fetchedAlbums = musicDao.getRandomAlbums(loggedInUserId, maxResults);
		} else {
			fetchedAlbums = musicDao.getLatestAlbums(loggedInUserId, pageNumber, maxResults);
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
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		Album album = musicDao.getAlbum(loggedInUserId, artistName, albumName);
		return album;
	}

	@Override
	public List<Album> getAlbumsByArtist(Long artistId) {
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		List<Album> albums = musicDao.getAlbumsByArtist(loggedInUserId, artistId);
		if (albums == null || albums.isEmpty()) {
			return albums;
		}

		for (Album album : albums) {
			Hibernate.initialize(album.getTracks());
		}

		return albums;
	}

	@Override
	public List<Track> getTracks(Long albumId) {
		Long loggedInUserId = AdminHelper.getLoggedInUser().getId();
		List<Track> tracks = musicDao.getTracks(loggedInUserId, albumId);
		return tracks;
	}

	@Override
	public Artist getArtist(Long artistId, boolean isFullyInitialise) {
		User user = AdminHelper.getLoggedInUser();

		if (!isFullyInitialise && user == null) {
			log.error("No user found in session, using system user...");
			user = adminManager.getSystemUser();
		}


		Artist artist;
		List<Library> libraries = artistRepository.findLibrariesById(artistId);

		if (hasLibraryAccess(libraries, user)) {
			artist = artistRepository.getReferenceById(artistId);
		} else {
			return null;
		}

		// if (adminManager.hasAccessToLibrary())
		// if (AdminHelper.isAllowedGroup(groups)) {
		// 	artist = artistRepository.getOne(artistId);
		// } else {
		// 	return null;
		// }

		if (!isFullyInitialise) {
			return artist;
		}

		Hibernate.initialize(artist.getAlbums());
		return artist;
	}



	private boolean hasLibraryAccess(List<Library> libraries, final User user) {
		for(Library library : libraries) {
			if (library.hasAccess(user)) {
				return true;
			}
		}

		return false;

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
	public List<Track> findTracks(MediaItemSearchCriteria mediaItemSearchCriteria) {
		Long userId = AdminHelper.getLoggedInUser().getId();
		List<Track> tracks = musicDao.findTracks(userId, mediaItemSearchCriteria);
		return tracks;
	}

	@Override
	public long getTotalTracksFromLibrary(long libraryId) {
		long totalTracksFromLibrary = musicDao.getTotalTracksFromLibrary(libraryId);
		return totalTracksFromLibrary;
	}

}
