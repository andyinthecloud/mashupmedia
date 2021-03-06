package org.mashupmedia.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.criteria.MediaItemSearchCriteria.MediaSortType;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.DaoHelper;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MusicDaoImpl extends BaseDaoImpl implements MusicDao {

	@Autowired
	private LibraryDao libraryDao;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private ArtistRepository artistRepository;

	// Used for remote library synchronisation
	private final static int NUMBER_OF_DAYS_TO_KEEP_DISABLED_SONGS = 1;

	protected int getFirstResult(int pageNumber, int maxResults) {
		int firstResult = pageNumber * maxResults;
		return firstResult;
	}

	@Override
	public List<Album> getAlbums(List<Long> groupIds, String searchLetter, int pageNumber, int maxResults) {

		int firstResult = getFirstResult(pageNumber, maxResults);
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		searchLetter = StringUtils.trimToEmpty(searchLetter);
		if (StringUtils.isNotEmpty(searchLetter)) {
			queryBuilder.append(" and a.indexLetter = '" + searchLetter.toLowerCase() + "'");
		}
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);

		queryBuilder.append(" order by a.indexText");

		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		List<Album> albums = query.getResultList();
		return albums;
	}

	@Override
	public List<String> getAlbumIndexLetters(List<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a.indexLetter from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.indexLetter");
		TypedQuery<String> query = entityManager.createQuery(queryBuilder.toString(), String.class);
		List<String> indexLetters = query.getResultList();
		return indexLetters;
	}

	@Override
	public List<Artist> getArtists(List<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from Artist a join a.albums album join album.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.indexText");
		TypedQuery<Artist> query = entityManager.createQuery(queryBuilder.toString(), Artist.class);
		List<Artist> artists = query.getResultList();
		return artists;
	}



	@Override
	public void deleteObsoleteSong(Song song) {
		Date updatedOn = song.getUpdatedOn();
		Date deleteDate = DateUtils.addDays(updatedOn, NUMBER_OF_DAYS_TO_KEEP_DISABLED_SONGS);

		if (deleteDate.after(new Date())) {
			song.setEnabled(false);
			entityManager.merge(song);
		} else {
			deleteSong(song);
		}
	}

	@Override
	public void deleteSong(Song song) {
		Genre genre = song.getGenre();
		deleteGenre(genre);

		String path = song.getPath();

		StringBuilder queryBuilder = new StringBuilder("delete from Song  where path = :path");

		entityManager.createQuery(queryBuilder.toString()).setParameter("path", path)
				.executeUpdate();
		flushSession(true);
	}

	private void deleteGenre(Genre genre) {
		if (genre == null) {
			return;
		}

		TypedQuery<Long> query  =  entityManager
				.createQuery("select count(s.id) from Song s where s.genre.id = :genreId", Long.class);
query.setParameter("genreId", genre.getId());
		
				Long numberOfSongs = getUniqueResult(query);
				if (numberOfSongs > 0) {
			return;
		}

		entityManager.remove(genre);
	}

	@Override
	public Album getAlbum(List<Long> groupIds, String artistName, String albumName) {
		StringBuilder queryBuilder = new StringBuilder(
				"select a from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g where lower(a.artist.name) = :artistName and lower(a.name) = :albumName");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		query.setParameter("artistName", artistName.toLowerCase());
		query.setParameter("albumName", albumName.toLowerCase());
		Album album = getUniqueResult(query);
		return album;
	}

	@Override
	public Album getAlbum(List<Long> groupIds, long albumId) {

		StringBuilder albumQueryBuilder = new StringBuilder(
				"from org.mashupmedia.model.media.music.Album a where id = :id");
		TypedQuery<Album> albumQuery = entityManager.createQuery(albumQueryBuilder.toString(),
				Album.class);
		albumQuery.setParameter("id", albumId);
		Album album = getUniqueResult(albumQuery);

		StringBuilder songsQueryBuilder = new StringBuilder(
				"select distinct s from Song s join s.library.groups g where s.album.id = :id and s.enabled = true");
		songsQueryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(songsQueryBuilder, groupIds);
		songsQueryBuilder.append(" order by s.trackNumber");
		TypedQuery<Song> songsQuery = entityManager.createQuery(songsQueryBuilder.toString(),
				Song.class);
		songsQuery.setParameter("id", albumId);
		List<Song> songs = (List<Song>) songsQuery.getResultList();
		if (songs == null || songs.isEmpty()) {
			return null;
		}

		album.setSongs(songs);

		return album;
	}

	@Override
	public Song getSong(List<Long> groupIds, long libraryId, String songPath, long fileLastModifiedOn) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct s from Song s inner join s.library.groups g where s.library.id = :libraryId and s.path = :path and s.fileLastModifiedOn = :fileLastModifiedOn");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		TypedQuery<Song> query = entityManager.createQuery(queryBuilder.toString(), Song.class);
		query.setParameter("libraryId", libraryId);
		query.setParameter("path", songPath);
		query.setParameter("fileLastModifiedOn", fileLastModifiedOn);

		List<Song> songs = query.getResultList();
		if (songs.size() > 1) {
			log.error("Returning duplicate songs, using first in list...");
		}

		if (songs.isEmpty()) {
			return null;
		}

		return songs.get(0);
	}

	@Override
	public Song getSong(String path) {
		StringBuilder queryBuilder = new StringBuilder("from Song s where s.path = :path");
		TypedQuery<Song> query = entityManager.createQuery(queryBuilder.toString(), Song.class);
		query.setParameter("path", path);

		List<Song> songs = query.getResultList();
		if (songs.size() > 1) {
			log.error("Returning duplicate songs, using first in list...");
		}

		if (songs.isEmpty()) {
			return null;
		}

		return songs.get(0);
	}

	@Override
	public List<Song> getSongsToDelete(long libraryId, Date date) {
		TypedQuery<Song> query = entityManager
				.createQuery("from Song where library.id = :libraryId and updatedOn < :updatedOn", Song.class);
		query.setParameter("libraryId", libraryId);
		query.setParameter("updatedOn", date);

		List<Song> songs = query.getResultList();
		return songs;
	}

	@Override
	public void saveSong(Song song) {
		saveSong(song, false);
	}

	@Override
	public void saveSong(Song song, boolean isSessionFlush) {
		Artist artist = song.getArtist();
		Optional<Artist> persistedArtist = artistRepository.findArtistByNameIgnoreCase(artist.getName());
		if (persistedArtist.isPresent()) {
			artist = persistedArtist.get();
		} else {
			artistRepository.save(artist);
		}

//		saveOrMerge(artist);

		Album album = song.getAlbum();
		saveOrMerge(album);
		song.setAlbum(album);

		saveOrMerge(song.getYear());
		saveOrMerge(song.getGenre());
		saveOrUpdate(song);

		Library library = song.getLibrary();
//		saveOrUpdate(library.getLocation());
		libraryDao.saveLibrary(library);

		flushSession(isSessionFlush);

		log.debug("Saved song: " + artist.getName() + " - " + album.getName() + " - " + song.getTitle());
	}

	@Override
	public void saveAlbum(Album album) {
		saveOrUpdate(album);
	}

	@Override
	public void saveArtist(Artist artist) {
		saveOrUpdate(artist);
	}

	@Override
	public List<Album> getRandomAlbums(List<Long> groupIds, int numberOfAlbums) {

		// List<Long> albumIds = getAlbumIds(groupIds);
		Collection<Long> randomAlbumIds = getRandomAlbumIds(numberOfAlbums, groupIds);

		StringBuilder viewableAlbumIdsQueryBuilder = new StringBuilder(
				"select distinct(a.id) from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g");
		viewableAlbumIdsQueryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(viewableAlbumIdsQueryBuilder, groupIds);
		viewableAlbumIdsQueryBuilder.append(" and a.id in (:albumIds)");
		TypedQuery<Long> query = entityManager.createQuery(viewableAlbumIdsQueryBuilder.toString(),
				Long.class);
		query.setParameter("albumIds", randomAlbumIds);
		query.setMaxResults(numberOfAlbums);

		List<Long> allowedAlbumIds = query.getResultList();

		StringBuilder randomAlbumsQueryBuilder = new StringBuilder(
				"select a from org.mashupmedia.model.media.music.Album a where a.id in (:albumIds) order by rand()");
				TypedQuery<Album> randomAlbumsQuery = entityManager
				.createQuery(randomAlbumsQueryBuilder.toString(), Album.class);
		randomAlbumsQuery.setParameter("albumIds", allowedAlbumIds);
		randomAlbumsQuery.setMaxResults(numberOfAlbums);
		List<Album> randomAlbums = randomAlbumsQuery.getResultList();

		return randomAlbums;
	}

	private Collection<Long> getRandomAlbumIds(int maxResults, List<Long> groupIds) {
		List<Long> albumIds = getAlbumIds(groupIds);
		List<Long> randomAlbumIds = new ArrayList<>();
		if (albumIds == null || albumIds.isEmpty()) {
			return randomAlbumIds;
		}

		while (randomAlbumIds.size() < maxResults) {
			int randomIndex = ThreadLocalRandom.current().nextInt(albumIds.size());
			long randomAlbumId = albumIds.get(randomIndex);
			randomAlbumIds.add(randomAlbumId);
		}

		return randomAlbumIds;
	}

	private List<Long> getAlbumIds(List<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder(
				"select a.id from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);

		TypedQuery<Long> query = entityManager.createQuery(queryBuilder.toString(), Long.class);

		// @SuppressWarnings("unchecked")
		// List<Long> albumIds = SetUniqueList.decorate(query.getResultList());
		return query.getResultList();
	}

	protected int getTotalGroups() {
		int totalGroups = groupDao.getGroupIds().size();
		return totalGroups;
	}

	@Override
	public List<Album> getLatestAlbums(List<Long> groupIds, int pageNumber, int maxResults) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.updatedOn desc");

		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		int firstResult = getFirstResult(pageNumber, maxResults);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);

		List<Album> albums = query.getResultList();
		return albums;
	}

	@Override
	public Year getYear(int year) {
		TypedQuery<Year> query = entityManager.createQuery("from Year where year = :year", Year.class);
		query.setParameter("year", year);
		Year album = getUniqueResult(query);
		return album;
	}

	@Override
	public Genre getGenre(String name) {
		TypedQuery<Genre> query = entityManager.createQuery("from Genre where name = :name",
				Genre.class);
		query.setParameter("name", name);
		Genre genre = getUniqueResult(query);
		return genre;
	}

	@Override
	public List<Song> getSongs(List<Long> groupIds, Long albumId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select s from Song s inner join s.library.groups g where s.album.id = :albumId ");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by trackNumber");
		TypedQuery<Song> query = entityManager.createQuery(queryBuilder.toString(), Song.class);
		query.setParameter("albumId", albumId);

		List<Song> songs = query.getResultList();
		return songs;
	}

	@Override
	public List<Album> getAlbumsByArtist(List<Long> groupIds, long artistId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g where a.artist.id = :artistId ");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.name");

		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		query.setParameter("artistId", artistId);
		List<Album> albums = query.getResultList();
		return albums;
	}

	@Override
	public List<String> getArtistIndexLetters(List<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a.indexLetter from Artist a join a.albums album join album.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.indexLetter");
		TypedQuery<String> query = entityManager.createQuery(queryBuilder.toString(), String.class);
		List<String> indexLetters = query.getResultList();
		return indexLetters;
	}

	@Override
	public Artist getArtist(List<Long> groupIds, Long artistId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select a from Artist a join a.albums album join album.songs s join s.library.groups g where a.id = :artistId");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		TypedQuery<Artist> query = entityManager.createQuery(queryBuilder.toString(), Artist.class);
		query.setParameter("artistId", artistId);
		Artist artist = getUniqueResult(query);
		return artist;
	}

	@Override
	public List<Genre> getGenres() {
		TypedQuery<Genre> query = entityManager.createQuery("from Genre order by name", Genre.class);
		List<Genre> genres = query.getResultList();
		return genres;
	}

	@Override
	public List<Song> findSongs(List<Long> groupIds, MediaItemSearchCriteria mediaItemSearchCriteria) {

		FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);

		QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Song.class).get();
		@SuppressWarnings("rawtypes")
		BooleanJunction<BooleanJunction> booleanJunction = queryBuilder.bool();

		String searchWordsValue = mediaItemSearchCriteria.getSearchWords();
		if (StringUtils.isNotBlank(searchWordsValue)) {
			String[] searchWords = searchWordsValue.split("\\s");
			for (String searchWord : searchWords) {
				booleanJunction.must(
						queryBuilder.keyword().wildcard().onField("searchText").matching(searchWord).createQuery());
			}
		}

		String mediaTypeValue = StringHelper.normaliseTextForDatabase(MediaType.SONG.toString());
		booleanJunction.must(queryBuilder.keyword().onField("mediaTypeValue").matching(mediaTypeValue).createQuery());
		booleanJunction.must(
				queryBuilder.keyword().onField("enabled").matching(mediaItemSearchCriteria.isEnabled()).createQuery());

		@SuppressWarnings("rawtypes")
		BooleanJunction<BooleanJunction> groupJunction = queryBuilder.bool();
		for (Long groupId : groupIds) {
			groupJunction.should(queryBuilder.keyword().onField("library.groups.id").matching(groupId).createQuery());
		}
		booleanJunction.must(groupJunction.createQuery());

		// long libraryId = mediaItemSearchCriteria.getLibraryId();
		// if (libraryId > 0) {
		// booleanJunction.must(queryBuilder.keyword().onField("library.id").matching(libraryId).createQuery());
		// }

		org.apache.lucene.search.Query luceneQuery = booleanJunction.createQuery();
		FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Song.class,
				MediaItem.class);

		boolean isReverse = !mediaItemSearchCriteria.isAscending();

		Sort sort = new Sort(new SortField("displayTitle", SortField.Type.STRING, isReverse));
		// Sort sort = new Sort(new SortField("lastAccessed", SortField.LONG,
		// isReverse));
		MediaSortType mediaSortType = mediaItemSearchCriteria.getMediaSortType();

		if (mediaSortType == MediaSortType.FAVOURITES) {
			sort = new Sort(new SortField("vote", SortField.Type.INT, isReverse));
		} else if (mediaSortType == MediaSortType.LAST_PLAYED) {
			sort = new Sort(new SortField("lastAccessed", SortField.Type.LONG, isReverse));
		} else if (mediaSortType == MediaSortType.ALBUM_NAME) {
			sort = new Sort(new SortField("album.indexText", SortField.Type.STRING, isReverse));
		} else if (mediaSortType == MediaSortType.ARTIST_NAME) {
			sort = new Sort(new SortField("artist.indexText", SortField.Type.STRING, isReverse));
		}

		query.setSort(sort);

		int maximumResults = mediaItemSearchCriteria.getMaximumResults();
		int firstResult = mediaItemSearchCriteria.getPageNumber() * maximumResults;
		query.setFirstResult(firstResult);
		query.setMaxResults(maximumResults);

		@SuppressWarnings("unchecked")
		List<Song> songs = query.getResultList();
		return songs;
	}

	@Override
	public long getTotalSongsFromLibrary(long libraryId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select count(s.id) from Song s where s.library.id = :libraryId ");
		TypedQuery<Long> query = entityManager.createQuery(queryBuilder.toString(), Long.class);
		query.setParameter("libraryId", libraryId);
		Long totalSongs = getUniqueResult(query);
		return totalSongs;
	}

	@Override
	public void deleteEmptyAlbums() {
		StringBuilder queryBuilder = new StringBuilder(
				"delete org.mashupmedia.model.media.music.Album a where a.songs is empty");
		int albumsDeleted = entityManager.createQuery(queryBuilder.toString()).executeUpdate();
		log.info(albumsDeleted + " empty albums deleted");

	}

	@Override
	public void deleteEmptyArtists() {
		StringBuilder queryBuilder = new StringBuilder("delete Artist a where a.albums is empty");
		int albumsDeleted = entityManager.createQuery(queryBuilder.toString()).executeUpdate();
		log.info(albumsDeleted + " empty artists deleted");
	}
}
