package org.mashupmedia.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.mashupmedia.comparator.MetaEntityComparator;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.exception.ContainsMediaItemsException;
import org.mashupmedia.exception.NameNotUniqueException;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.ExternalLink;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.music.AlbumRepository;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.service.storage.StorageManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.MetaEntityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
@Slf4j
@RequiredArgsConstructor
public class MusicManagerImpl implements MusicManager {

	private final MusicDao musicDao;
	private final AdminManager adminManager;
	private final ArtistRepository artistRepository;
	private final StorageManager storageManager;
	private final AlbumRepository albumRepository;

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
		return artists;
	}

	@Override
	public void deleteArtist(long artistId) throws ContainsMediaItemsException {
		Artist artist = getArtist(artistId);
		AdminHelper.checkAccess(artist.getUser());

		boolean hasTracks = artist.getAlbums()
				.stream()
				.anyMatch(album -> album.getTracks() != null && !album.getTracks().isEmpty());
		if (hasTracks) {
			throw new ContainsMediaItemsException("Unable to delete. Artist contains tracks.");
		}

		for (Album album : artist.getAlbums()) {
			deleteAlbum(album.getId());
		}

		deleteMetaImageFiles(artist.getMetaImages());

		artistRepository.deleteById(artistId);
	}

	@Override
	public void deleteAlbum(long albumId) throws ContainsMediaItemsException {
		Album album = getAlbum(albumId);
		AdminHelper.checkAccess(album.getArtist().getUser());
		if (album.getMetaImages().isEmpty()) {
			deleteMetaImageFiles(album.getMetaImages());
			albumRepository.deleteById(albumId);
			return;
		}

		if (album.getTracks().isEmpty()) {
			albumRepository.delete(album);
			return;
		}

		throw new ContainsMediaItemsException("Unable to delete. Album contains tracks.");
	}

	@Override
	public void saveAlbum(Album album) {
		Assert.hasText(album.getName(), "Expecting an album name");
		Artist artist = album.getArtist();
		Assert.notNull(artist, "artist should not be empty");
		long artistId = artist.getId();
		Assert.notNull(artistId, "artistId should not be empty");

		Artist savedArtist = getArtist(artistId, false);
		Assert.notNull(savedArtist, "artist should already exist");

		AdminHelper.checkAccess(savedArtist.getUser());

		long albumId = album.getId();
		boolean isNameNotUnique = savedArtist.getAlbums()
				.stream()
				.filter(a -> a.getId() != albumId)
				.anyMatch(a -> a.getName().equalsIgnoreCase(album.getName()));
		if (isNameNotUnique) {
			throw new NameNotUniqueException("Album name should be unique");
		}

		Album albumToSave = null;
		if (album.getId() > 0) {
			albumToSave = prepareUpdateAlbum(album, savedArtist);
		} else {
			albumToSave = prepareCreateAlbum(album, savedArtist);
		}

		musicDao.saveAlbum(albumToSave);
	}

	private Album prepareCreateAlbum(Album album, Artist savedArtist) {
		Date date = new Date();
		album.setCreatedOn(date);
		album.setArtist(savedArtist);
		album.setUpdatedOn(date);
		return album;
	}

	private Album prepareUpdateAlbum(Album album, Artist savedArtist) {
		Album savedAlbum = getAlbum(album.getId());
		savedAlbum.setArtist(savedArtist);
		savedAlbum.setName(album.getName());
		savedAlbum.setSummary(album.getSummary());
		savedAlbum.setUpdatedOn(new Date());

		Set<ExternalLink> savedExternalLinks = savedAlbum.getExternalLinks();
		Set<ExternalLink> externalLinks = album.getExternalLinks();

		MetaEntityHelper<ExternalLink> externalLinkHelper = new MetaEntityHelper<>();
		externalLinkHelper.mergeSet(savedExternalLinks, externalLinks);

		Set<MetaImage> savedMetaImages = savedAlbum.getMetaImages();
		Set<MetaImage> metaImages = album.getMetaImages();

		MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
		Set<MetaImage> deletedMetaImages = metaImageHelper.mergeSet(savedMetaImages, metaImages);
		deleteMetaImageFiles(deletedMetaImages);

		return savedAlbum;
	}

	@Override
	public Artist saveArtist(Artist artist) {
		Assert.notNull(artist, "Expecting an artist");

		long artistId = artist.getId();

		Artist albumToSave = null;
		if (artistId > 0) {
			albumToSave = prepareUpdateArtist(artist);
		} else {
			albumToSave = prepareCreateArtist(artist);
		}

		List<ExternalLink> externalLinks = new ArrayList<>(albumToSave.getExternalLinks());
		Collections.sort(externalLinks, new MetaEntityComparator());
		for (int i = 0; i < externalLinks.size(); i++) {
			ExternalLink externalLink = externalLinks.get(i);
			externalLink.setRank(i);
		}

		return musicDao.saveArtist(albumToSave);
	}

	private Artist prepareUpdateArtist(Artist artist) {
		long artistId = artist.getId();
		Artist savedArtist = getArtist(artistId);
		Assert.notNull(savedArtist, "Expecting to find an artist with id: " + artistId);

		Set<ExternalLink> savedExternalLinks = savedArtist.getExternalLinks();
		Set<ExternalLink> externalLinks = artist.getExternalLinks();

		MetaEntityHelper<ExternalLink> externalLinkHelper = new MetaEntityHelper<>();
		externalLinkHelper.mergeSet(savedExternalLinks, externalLinks);

		Set<MetaImage> savedMetaImages = savedArtist.getMetaImages();
		Set<MetaImage> metaImages = artist.getMetaImages();

		MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
		Set<MetaImage> deletedMetaImages = metaImageHelper.mergeSet(savedMetaImages, metaImages);
		deleteMetaImageFiles(deletedMetaImages);

		savedArtist.setProfile(artist.getProfile());
		savedArtist.setName(artist.getName());

		savedArtist.setUpdatedOn(new Date());

		return savedArtist;
	}

	private void deleteMetaImageFiles(Set<MetaImage> metaImages) {
		if (metaImages == null || metaImages.isEmpty()) {
			return;
		}

		for (MetaImage metaImage : metaImages) {
			String url = metaImage.getUrl();
			if (StringUtils.isNotBlank(url)) {
				storageManager.delete(Path.of(url));
			}

			String thumbnailUrl = metaImage.getThumbnailUrl();
			if (StringUtils.isNotBlank(thumbnailUrl)) {
				storageManager.delete(Path.of(thumbnailUrl));
			}
		}
	}

	private Artist prepareCreateArtist(Artist artist) {
		Date date = new Date();
		artist.setCreatedOn(date);
		artist.setUpdatedOn(date);
		artist.setUser(AdminHelper.getLoggedInUser());
		artist.setExternalLinks(new HashSet<>());
		return artist;
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

		Artist artist = artistRepository.getReferenceById(artistId);
		// List<Library> libraries = artistRepository.findLibrariesById(artistId);

		// if (hasLibraryAccess(libraries, user)) {
		// artist = artistRepository.getReferenceById(artistId);
		// } else {
		// return null;
		// }

		// if (adminManager.hasAccessToLibrary())
		// if (AdminHelper.isAllowedGroup(groups)) {
		// artist = artistRepository.getOne(artistId);
		// } else {
		// return null;
		// }

		if (!isFullyInitialise) {
			return artist;
		}

		Hibernate.initialize(artist.getAlbums());
		Hibernate.initialize(artist.getExternalLinks());
		Hibernate.initialize(artist.getMetaImages());

		return artist;
	}

	@Override
	public Artist getArtist(Long artistId) {
		Artist artist = getArtist(artistId, true);
		return artist;
	}

	@Override
	public Artist getArtist(String name) {
		User user = AdminHelper.getLoggedInUser();
		return artistRepository.findArtistByNameIgnoreCase(name, user.getId()).orElse(null);
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
