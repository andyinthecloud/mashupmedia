package org.mashupmedia.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Query;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MusicDaoImpl extends BaseDaoImpl implements MusicDao {

	@Autowired
	private LibraryDao libraryDao;

	@Autowired
	private GroupDao groupDao;

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

		Query<Album> query = getCurrentSession().createQuery(queryBuilder.toString(), Album.class);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		query.setCacheable(true);
		List<Album> albums = (List<Album>) query.list();
		return albums;
	}

	@Override
	public List<String> getAlbumIndexLetters(List<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a.indexLetter from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.indexLetter");
		Query<String> query = getCurrentSession().createQuery(queryBuilder.toString(), String.class);
		query.setCacheable(true);
		List<String> indexLetters = query.list();
		return indexLetters;
	}

	@Override
	public List<Artist> getArtists(List<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from Artist a join a.albums album join album.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.indexText");
		Query<Artist> query = getCurrentSession().createQuery(queryBuilder.toString(), Artist.class);
		query.setCacheable(true);
		List<Artist> artists = (List<Artist>) query.list();
		return artists;
	}

	@Override
	public Artist getArtist(List<Long> groupIds, String name) {

		StringBuilder queryBuilder = new StringBuilder(
				"select a from Artist a join a.albums as album join album.songs as s join s.library.groups as g where lower(a.name) = :name");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);

		Query<Artist> query = getCurrentSession().createQuery(queryBuilder.toString(), Artist.class);
		query.setCacheable(true);
		query.setParameter("name", name.toLowerCase());
		Artist artist = (Artist) query.uniqueResult();
		return artist;
	}

	@Override
	public void deleteObsoleteSong(Song song) {
		Date updatedOn = song.getUpdatedOn();
		Date deleteDate = DateUtils.addDays(updatedOn, NUMBER_OF_DAYS_TO_KEEP_DISABLED_SONGS);

		if (deleteDate.after(new Date())) {
			song.setEnabled(false);
			getCurrentSession().merge(song);
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

		getCurrentSession().createQuery(queryBuilder.toString()).setParameter("path", path)
				.executeUpdate();
		flushSession(true);
	}

	private void deleteGenre(Genre genre) {
		if (genre == null) {
			return;
		}

		Long numberOfSongs = (Long) getCurrentSession()
				.createQuery("select count(s.id) from Song s where s.genre.id = :genreId").setCacheable(true)
				.setParameter("genreId", genre.getId()).uniqueResult();
		if (numberOfSongs > 0) {
			return;
		}

		getCurrentSession().delete(genre);
	}

	@Override
	public Album getAlbum(List<Long> groupIds, String artistName, String albumName) {
		StringBuilder queryBuilder = new StringBuilder(
				"select a from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g where lower(a.artist.name) = :artistName and lower(a.name) = :albumName");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		Query<Album> query = getCurrentSession().createQuery(queryBuilder.toString(), Album.class);
		query.setCacheable(true);
		query.setParameter("artistName", artistName.toLowerCase());
		query.setParameter("albumName", albumName.toLowerCase());
		Album album = (Album) query.uniqueResult();
		return album;
	}

	@Override
	public Album getAlbum(List<Long> groupIds, long albumId) {

		StringBuilder albumQueryBuilder = new StringBuilder(
				"from org.mashupmedia.model.media.music.Album a where id = :id");
		Query<Album> albumQuery = getCurrentSession().createQuery(albumQueryBuilder.toString(),
				Album.class);
		albumQuery.setCacheable(true);
		albumQuery.setParameter("id", albumId);
		Album album = (Album) albumQuery.uniqueResult();

		StringBuilder songsQueryBuilder = new StringBuilder(
				"select distinct s from Song s join s.library.groups g where s.album.id = :id and s.enabled = true");
		songsQueryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(songsQueryBuilder, groupIds);
		songsQueryBuilder.append(" order by s.trackNumber");
		Query<Song> songsQuery = getCurrentSession().createQuery(songsQueryBuilder.toString(),
				Song.class);
		songsQuery.setCacheable(true);
		songsQuery.setParameter("id", albumId);
		List<Song> songs = (List<Song>) songsQuery.list();
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
		Query<Song> query = getCurrentSession().createQuery(queryBuilder.toString(), Song.class);
		query.setCacheable(true);
		query.setParameter("libraryId", libraryId);
		query.setParameter("path", songPath);
		query.setParameter("fileLastModifiedOn", fileLastModifiedOn);

		List<Song> songs = query.list();
		if (songs.size() > 1) {
			logger.error("Returning duplicate songs, using first in list...");
		}

		if (songs.isEmpty()) {
			return null;
		}

		return songs.get(0);
	}

	@Override
	public Song getSong(String path) {
		StringBuilder queryBuilder = new StringBuilder("from Song s where s.path = :path");
		Query<Song> query = getCurrentSession().createQuery(queryBuilder.toString(), Song.class);
		query.setCacheable(true);
		query.setParameter("path", path);

		List<Song> songs = query.list();
		if (songs.size() > 1) {
			logger.error("Returning duplicate songs, using first in list...");
		}

		if (songs.isEmpty()) {
			return null;
		}

		return songs.get(0);
	}

	@Override
	public List<Song> getSongsToDelete(long libraryId, Date date) {
		Query<Song> query = getCurrentSession()
				.createQuery("from Song where library.id = :libraryId and updatedOn < :updatedOn", Song.class);
		query.setCacheable(true);
		query.setParameter("libraryId", libraryId);
		query.setParameter("updatedOn", date);

		List<Song> songs = query.list();
		return songs;
	}

	@Override
	public void saveSong(Song song) {
		saveSong(song, false);
	}

	@Override
	public void saveSong(Song song, boolean isSessionFlush) {
		Artist artist = song.getArtist();
		saveOrMerge(artist);

		Album album = song.getAlbum();
		saveOrMerge(album);
		song.setAlbum(album);

		saveOrMerge(song.getYear());
		saveOrMerge(song.getGenre());
		saveOrUpdate(song);

		Library library = song.getLibrary();
		libraryDao.saveLibrary(library);

		flushSession(isSessionFlush);

		logger.debug("Saved song: " + artist.getName() + " - " + album.getName() + " - " + song.getTitle());
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
		Query<Long> query = getCurrentSession().createQuery(viewableAlbumIdsQueryBuilder.toString(),
				Long.class);
		query.setParameterList("albumIds", randomAlbumIds);
		query.setMaxResults(numberOfAlbums);

		List<Long> allowedAlbumIds = query.list();

		StringBuilder randomAlbumsQueryBuilder = new StringBuilder(
				"select a from org.mashupmedia.model.media.music.Album a where a.id in (:albumIds) order by rand()");
		Query<Album> randomAlbumsQuery = getCurrentSession()
				.createQuery(randomAlbumsQueryBuilder.toString(), Album.class);
		randomAlbumsQuery.setParameterList("albumIds", allowedAlbumIds);
		randomAlbumsQuery.setMaxResults(numberOfAlbums);
		List<Album> randomAlbums = randomAlbumsQuery.list();

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

		Query<Long> query = getCurrentSession().createQuery(queryBuilder.toString(), Long.class);
		query.setCacheable(true);

		@SuppressWarnings("unchecked")
		List<Long> albumIds = SetUniqueList.decorate(query.list());
		return albumIds;
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

		Query<Album> query = getCurrentSession().createQuery(queryBuilder.toString(), Album.class);
		int firstResult = getFirstResult(pageNumber, maxResults);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);

		List<Album> albums = query.list();
		return albums;
	}

	@Override
	public Year getYear(int year) {
		Query<Year> query = getCurrentSession().createQuery("from Year where year = :year", Year.class);
		query.setCacheable(true);
		query.setParameter("year", year);
		Year album = (Year) query.uniqueResult();
		return album;
	}

	@Override
	public Genre getGenre(String name) {
		Query<Genre> query = getCurrentSession().createQuery("from Genre where name = :name",
				Genre.class);
		query.setCacheable(true);
		query.setParameter("name", name);
		Genre genre = (Genre) query.uniqueResult();
		return genre;
	}

	@Override
	public List<Song> getSongs(List<Long> groupIds, Long albumId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select s from Song s inner join s.library.groups g where s.album.id = :albumId ");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by trackNumber");
		Query<Song> query = getCurrentSession().createQuery(queryBuilder.toString(), Song.class);
		query.setCacheable(true);
		query.setParameter("albumId", albumId);

		List<Song> songs = query.list();
		return songs;
	}

	@Override
	public List<Album> getAlbumsByArtist(List<Long> groupIds, long artistId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from org.mashupmedia.model.media.music.Album a join a.songs s join s.library.groups g where a.artist.id = :artistId ");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.name");

		Query<Album> query = getCurrentSession().createQuery(queryBuilder.toString(), Album.class);
		query.setCacheable(true);
		query.setParameter("artistId", artistId);
		List<Album> albums = query.list();
		return albums;
	}

	@Override
	public List<String> getArtistIndexLetters(List<Long> groupIds) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a.indexLetter from Artist a join a.albums album join album.songs s join s.library.groups g");
		queryBuilder.append(" where s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by a.indexLetter");
		Query<String> query = getCurrentSession().createQuery(queryBuilder.toString(), String.class);
		query.setCacheable(true);
		List<String> indexLetters = query.list();
		return indexLetters;
	}

	@Override
	public Artist getArtist(List<Long> groupIds, Long artistId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select a from Artist a join a.albums album join album.songs s join s.library.groups g where a.id = :artistId");
		queryBuilder.append(" and s.library.enabled = true");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		Query<Artist> query = getCurrentSession().createQuery(queryBuilder.toString(), Artist.class);
		query.setCacheable(true);
		query.setParameter("artistId", artistId);
		Artist artist = (Artist) query.uniqueResult();
		return artist;
	}

	@Override
	public List<Genre> getGenres() {
		Query<Genre> query = getCurrentSession().createQuery("from Genre order by name", Genre.class);
		query.setCacheable(true);
		List<Genre> genres = query.list();
		return genres;
	}

	@Override
	public List<Song> findSongs(List<Long> groupIds, MediaItemSearchCriteria mediaItemSearchCriteria) {
return null;
//		Session session = getCurrentSession();
//		FullTextSession fullTextSession = Search.getFullTextSession(session);
//
//		QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Song.class).get();
//		@SuppressWarnings("rawtypes")
//		BooleanJunction<BooleanJunction> booleanJunction = queryBuilder.bool();
//
//		String searchWordsValue = mediaItemSearchCriteria.getSearchWords();
//		if (StringUtils.isNotBlank(searchWordsValue)) {
//			String[] searchWords = searchWordsValue.split("\\s");
//			for (String searchWord : searchWords) {
//				booleanJunction.must(
//						queryBuilder.keyword().wildcard().onField("searchText").matching(searchWord).createQuery());
//			}
//		}
//
//		String mediaTypeValue = StringHelper.normaliseTextForDatabase(MediaType.SONG.toString());
//		booleanJunction.must(queryBuilder.keyword().onField("mediaTypeValue").matching(mediaTypeValue).createQuery());
//		booleanJunction.must(
//				queryBuilder.keyword().onField("enabled").matching(mediaItemSearchCriteria.isEnabled()).createQuery());
//
//		@SuppressWarnings("rawtypes")
//		BooleanJunction<BooleanJunction> groupJunction = queryBuilder.bool();
//		for (Long groupId : groupIds) {
//			groupJunction.should(queryBuilder.keyword().onField("library.groups.id").matching(groupId).createQuery());
//		}
//		booleanJunction.must(groupJunction.createQuery());
//
//		// long libraryId = mediaItemSearchCriteria.getLibraryId();
//		// if (libraryId > 0) {
//		// booleanJunction.must(queryBuilder.keyword().onField("library.id").matching(libraryId).createQuery());
//		// }
//
//		org.apache.lucene.search.Query luceneQuery = booleanJunction.createQuery();
//		org.hibernate.search.FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Song.class,
//				MediaItem.class);
//
//		boolean isReverse = !mediaItemSearchCriteria.isAscending();
//
//		Sort sort = new Sort(new SortField("displayTitle", SortField.Type.STRING, isReverse));
//		// Sort sort = new Sort(new SortField("lastAccessed", SortField.LONG,
//		// isReverse));
//		MediaSortType mediaSortType = mediaItemSearchCriteria.getMediaSortType();
//
//		if (mediaSortType == MediaSortType.FAVOURITES) {
//			sort = new Sort(new SortField("vote", SortField.Type.INT, isReverse));
//		} else if (mediaSortType == MediaSortType.LAST_PLAYED) {
//			sort = new Sort(new SortField("lastAccessed", SortField.Type.LONG, isReverse));
//		} else if (mediaSortType == MediaSortType.ALBUM_NAME) {
//			sort = new Sort(new SortField("album.indexText", SortField.Type.STRING, isReverse));
//		} else if (mediaSortType == MediaSortType.ARTIST_NAME) {
//			sort = new Sort(new SortField("artist.indexText", SortField.Type.STRING, isReverse));
//		}
//
//		query.setSort(sort);
//
//		int maximumResults = mediaItemSearchCriteria.getMaximumResults();
//		int firstResult = mediaItemSearchCriteria.getPageNumber() * maximumResults;
//		query.setFirstResult(firstResult);
//		query.setMaxResults(maximumResults);
//
//		@SuppressWarnings("unchecked")
//		List<Song> songs = query.list();
//
//		return songs;
	}

	@Override
	public long getTotalSongsFromLibrary(long libraryId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select count(s.id) from Song s where s.library.id = :libraryId ");
		Query<Long> query = getCurrentSession().createQuery(queryBuilder.toString(), Long.class);
		query.setCacheable(true);
		query.setParameter("libraryId", libraryId);
		Long totalSongs = (Long) query.uniqueResult();
		return totalSongs;
	}

	@Override
	public void deleteEmptyAlbums() {
		StringBuilder queryBuilder = new StringBuilder(
				"delete org.mashupmedia.model.media.music.Album a where a.songs is empty");
		int albumsDeleted = getCurrentSession().createQuery(queryBuilder.toString()).executeUpdate();
		logger.info(albumsDeleted + " empty albums deleted");

	}

	@Override
	public void deleteEmptyArtists() {
		StringBuilder queryBuilder = new StringBuilder("delete Artist a where a.albums is empty");
		int albumsDeleted = getCurrentSession().createQuery(queryBuilder.toString()).executeUpdate();
		logger.info(albumsDeleted + " empty artists deleted");
	}
}
