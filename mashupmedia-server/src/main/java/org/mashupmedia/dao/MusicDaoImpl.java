package org.mashupmedia.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.util.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MusicDaoImpl extends BaseDaoImpl implements MusicDao {

	@Autowired
	private ArtistRepository artistRepository;

	// Used for remote library synchronisation
	private final static int NUMBER_OF_DAYS_TO_KEEP_DISABLED_TRACKS = 1;

	protected int getFirstResult(int pageNumber, int maxResults) {
		int firstResult = pageNumber * maxResults;
		return firstResult;
	}

	@Override
	public List<Album> getAlbums(Long userId, String searchLetter, int pageNumber, int maxResults) {

		int firstResult = getFirstResult(pageNumber, maxResults);
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from org.mashupmedia.model.media.music.Album");
		queryBuilder.append(" join a.tracks t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");

		queryBuilder.append(" where t.library.enabled = true");
		queryBuilder.append(" and u.id = :userId");

		searchLetter = StringUtils.trimToEmpty(searchLetter);

		if (StringUtils.isNotEmpty(searchLetter)) {
			queryBuilder.append(" and a.indexLetter = '" + searchLetter.toLowerCase() + "'");
		}

		DaoHelper.appendUserIdFilter(queryBuilder, userId);

		queryBuilder.append(" order by a.indexText");

		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		query.setParameter("userId", userId);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		List<Album> albums = query.getResultList();
		return albums;
	}

	@Override
	public List<String> getAlbumIndexLetters(Long userId) {
		throw new UnsupportedOperationException("Not implemented");

		// StringBuilder queryBuilder = new StringBuilder(
		// "select distinct a.indexLetter from org.mashupmedia.model.media.music.Album a
		// join a.tracks s join s.library.groups g");
		// queryBuilder.append(" where t.library.enabled = true");
		// DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		// queryBuilder.append(" order by a.name");
		// TypedQuery<String> query = entityManager.createQuery(queryBuilder.toString(),
		// String.class);
		// List<String> indexLetters = query.getResultList();
		// return indexLetters;
	}

	@Override
	public List<Artist> getArtists(Long userId) {

		Assert.notNull(userId, "Expecting a userId");

		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from Artist a");
		queryBuilder.append(" join a.albums album");
		queryBuilder.append(" join album.tracks t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" where t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);

		queryBuilder.append(" order by a.name");
		TypedQuery<Artist> query = entityManager.createQuery(queryBuilder.toString(), Artist.class);
		List<Artist> artists = query.getResultList();
		return artists;
	}

	// @Override
	// public void deleteObsoleteTrack(Track track) {
	// Date updatedOn = track.getUpdatedOn();
	// Date deleteDate = DateUtils.addDays(updatedOn,
	// NUMBER_OF_DAYS_TO_KEEP_DISABLED_TRACKS);

	// if (deleteDate.after(new Date())) {
	// track.setEnabled(false);
	// entityManager.merge(track);
	// } else {
	// deleteTrack(track);
	// }
	// }

	// @Override
	// public void deleteTrack(Track track) {
	// Genre genre = track.getGenre();
	// deleteGenre(genre);

	// String path = track.getPath();

	// StringBuilder queryBuilder = new StringBuilder("delete from Track where path
	// = :path");

	// entityManager.createQuery(queryBuilder.toString()).setParameter("path", path)
	// .executeUpdate();
	// flushSession(true);
	// }

	// private void deleteGenre(Genre genre) {
	// if (genre == null) {
	// return;
	// }

	// TypedQuery<Long> query = entityManager
	// .createQuery("select count(s.id) from Track s where s.genre.name = :name",
	// Long.class);
	// query.setParameter("name", genre.getName());

	// Long numberOfTracks = getUniqueResult(query);
	// if (numberOfTracks > 0) {
	// return;
	// }

	// entityManager.remove(genre);
	// }

	@Override
	public Album getAlbum(Long userId, String artistName, String albumName) {
		StringBuilder queryBuilder = new StringBuilder(
				"select a from org.mashupmedia.model.media.music.Album a");
		queryBuilder.append(" join a.tracks t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");

		queryBuilder.append(" where lower(a.artist.name) = :artistName");
		queryBuilder.append(" and lower(a.name) = :albumName");

		queryBuilder.append(" and t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);

		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		query.setParameter("artistName", artistName.toLowerCase());
		query.setParameter("albumName", albumName.toLowerCase());
		Album album = getUniqueResult(query);
		return album;
	}

	@Override
	public Album getAlbum(Long userId, long albumId) {

		StringBuilder albumQueryBuilder = new StringBuilder(
				"from org.mashupmedia.model.media.music.Album a where id = :id");
		TypedQuery<Album> albumQuery = entityManager.createQuery(albumQueryBuilder.toString(),
				Album.class);
		albumQuery.setParameter("id", albumId);
		Album album = getUniqueResult(albumQuery);

		StringBuilder tracksQueryBuilder = new StringBuilder(
				"select distinct t from Track t");
		tracksQueryBuilder.append(" join t.library l");
		tracksQueryBuilder.append(" left join l.shareUsers u");
		tracksQueryBuilder.append(" where t.album.id = :id");
		tracksQueryBuilder.append(" and t.enabled = true");
		tracksQueryBuilder.append(" and t.library.enabled = true");

		DaoHelper.appendUserIdFilter(tracksQueryBuilder, userId);
		tracksQueryBuilder.append(" order by t.trackNumber");
		TypedQuery<Track> tracksQuery = entityManager.createQuery(tracksQueryBuilder.toString(),
				Track.class);
		tracksQuery.setParameter("id", albumId);
		List<Track> tracks = (List<Track>) tracksQuery.getResultList();
		if (tracks == null || tracks.isEmpty()) {
			return null;
		}

		album.setTracks(tracks);

		return album;
	}

	@Override
	public Track getTrack(Long userId, long libraryId, String trackPath, long fileLastModifiedOn) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct t from Track t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" where t.library.id = :libraryId");
		queryBuilder.append(" and t.path = :path");
		queryBuilder.append(" and t.fileLastModifiedOn = :fileLastModifiedOn");
		queryBuilder.append(" and t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);

		TypedQuery<Track> query = entityManager.createQuery(queryBuilder.toString(), Track.class);
		query.setParameter("libraryId", libraryId);
		query.setParameter("path", trackPath);
		query.setParameter("fileLastModifiedOn", fileLastModifiedOn);

		List<Track> tracks = query.getResultList();
		if (tracks.size() > 1) {
			log.error("Returning duplicate tracks, using first in list...");
		}

		if (tracks.isEmpty()) {
			return null;
		}

		return tracks.get(0);
	}

	@Override
	public Track getTrack(String path) {
		StringBuilder queryBuilder = new StringBuilder("from Track s where s.path = :path");
		TypedQuery<Track> query = entityManager.createQuery(queryBuilder.toString(), Track.class);
		query.setParameter("path", path);

		List<Track> tracks = query.getResultList();
		if (tracks.size() > 1) {
			log.error("Returning duplicate tracks, using first in list...");
		}

		if (tracks.isEmpty()) {
			return null;
		}

		return tracks.get(0);
	}

	@Override
	public List<Track> getTracksToDelete(long libraryId, Date date) {
		TypedQuery<Track> query = entityManager
				.createQuery("from Track where library.id = :libraryId and updatedOn < :updatedOn", Track.class);
		query.setParameter("libraryId", libraryId);
		query.setParameter("updatedOn", date);

		List<Track> tracks = query.getResultList();
		return tracks;
	}

	@Override
	public void saveTrack(Track track) {
		saveTrack(track, false);
	}

	@Override
	public void saveTrack(Track track, boolean isSessionFlush) {
		Artist artist = track.getArtist();
		Optional<Artist> persistedArtist = artistRepository.findArtistByNameIgnoreCase(artist.getName());
		if (persistedArtist.isPresent()) {
			artist = persistedArtist.get();
		} else {
			artistRepository.save(artist);
		}

		// saveOrMerge(artist);

		// Album album = track.getAlbum();
		// saveOrMerge(album);
		// track.setAlbum(album);

		// saveOrMerge(track.getYear());
		// saveOrMerge(track.getGenre());
		saveOrUpdate(track);

		// Library library = track.getLibrary();
		// // saveOrUpdate(library.getLocation());
		// libraryDao.saveLibrary(library);

		flushSession(isSessionFlush);

		log.debug("Saved track: " + track.getArtist().getName() + " - " + track.getAlbum().getName() + " - "
				+ track.getTitle());
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
	public List<Album> getRandomAlbums(Long userId, int numberOfAlbums) {
		List<Long> randomAlbumIds = getRandomAlbumIds(numberOfAlbums, userId);

		StringBuilder randomAlbumsQueryBuilder = new StringBuilder(
				"select a from org.mashupmedia.model.media.music.Album a where a.id in :albumIds");
		TypedQuery<Album> randomAlbumsQuery = entityManager
				.createQuery(randomAlbumsQueryBuilder.toString(), Album.class);
		randomAlbumsQuery.setParameter("albumIds", randomAlbumIds);
		List<Album> randomAlbums = randomAlbumsQuery.getResultList();

		return randomAlbums;
	}

	private List<Long> getRandomAlbumIds(int maxResults, Long userId) {
		List<Long> albumIds = getAlbumIds(userId);
		Collections.shuffle(albumIds);

		int totalAlbums = albumIds.size();
		int toIndex = totalAlbums < maxResults ? totalAlbums : maxResults;
		return albumIds.subList(0, toIndex);
	}

	private List<Long> getAlbumIds(Long userId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a.id from org.mashupmedia.model.media.music.Album a");
		queryBuilder.append(" join a.tracks t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" where t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);

		TypedQuery<Long> query = entityManager.createQuery(queryBuilder.toString(),
				Long.class);

		return query.getResultList();
	}

	// protected int getTotalGroups() {
	// int totalGroups = groupDao.getGroupIds().size();
	// return totalGroups;
	// }

	@Override
	public List<Album> getLatestAlbums(Long userId, int pageNumber, int maxResults) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from org.mashupmedia.model.media.music.Album a");
		queryBuilder.append(" join a.tracks t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" where l.shareUsers u");

		queryBuilder.append(" where t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
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
	public List<Track> getTracks(Long userId, Long albumId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select t from Track s inner join s.library.groups g where s.album.id = :albumId ");
		queryBuilder.append(" inner join t.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" and t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
		queryBuilder.append(" order by trackNumber");
		TypedQuery<Track> query = entityManager.createQuery(queryBuilder.toString(), Track.class);
		query.setParameter("albumId", albumId);

		List<Track> tracks = query.getResultList();
		return tracks;
	}

	@Override
	public List<Album> getAlbumsByArtist(Long userId, long artistId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a from org.mashupmedia.model.media.music.Album a");
		queryBuilder.append(" join a.tracks t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" where a.artist.id = :artistId");
		queryBuilder.append(" and t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
		queryBuilder.append(" order by a.name");

		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		query.setParameter("artistId", artistId);
		List<Album> albums = query.getResultList();
		return albums;
	}

	@Override
	public List<String> getArtistIndexLetters(Long userId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select distinct a.indexLetter from Artist a");
		queryBuilder.append(" join a.albums album");
		queryBuilder.append(" join album.tracks t");
		queryBuilder.append(" join s.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" where t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
		queryBuilder.append(" order by a.indexLetter");
		TypedQuery<String> query = entityManager.createQuery(queryBuilder.toString(), String.class);
		List<String> indexLetters = query.getResultList();
		return indexLetters;
	}

	@Override
	public Artist getArtist(Long userId, Long artistId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select a from Artist a");
		queryBuilder.append(" join a.albums album");
		queryBuilder.append(" join album.tracks t");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" where a.id = :artistId");
		queryBuilder.append(" and t.library.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
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
	public List<Track> findTracks(Long userId, MediaItemSearchCriteria mediaItemSearchCriteria) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select t from Track t ");
		queryBuilder.append(" join t.library l");
		queryBuilder.append(" left join l.shareUsers u");
		queryBuilder.append(" where t.enabled = true");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
		// queryBuilder.append(" and lower(t.fileName) like '%" +
		// mediaItemSearchCriteria.getSearchText() + "%'");
		queryBuilder.append(" and upper(t.title) like %:searchText%");
		queryBuilder.append(" order by t.title");

		TypedQuery<Track> query = entityManager.createQuery(queryBuilder.toString(), Track.class);
		query.setParameter("searchText", mediaItemSearchCriteria.getSearchText());
		return query.getResultList();

		// throw new UnsupportedOperationException();

		// FullTextEntityManager fullTextSession =
		// Search.getFullTextEntityManager(entityManager);

		// QueryBuilder queryBuilder =
		// fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Track.class).get();
		// @SuppressWarnings("rawtypes")
		// BooleanJunction<BooleanJunction> booleanJunction = queryBuilder.bool();

		// String searchWordsValue = mediaItemSearchCriteria.getSearchWords();
		// if (StringUtils.isNotBlank(searchWordsValue)) {
		// String[] searchWords = searchWordsValue.split("\\s");
		// for (String searchWord : searchWords) {
		// booleanJunction.must(
		// queryBuilder.keyword().wildcard().onField("searchText").matching(searchWord).createQuery());
		// }
		// }

		// String mediaTypeValue =
		// StringHelper.normaliseTextForDatabase(MashupMediaType.TRACK.toString());
		// booleanJunction.must(queryBuilder.keyword().onField("mediaTypeValue").matching(mediaTypeValue).createQuery());
		// booleanJunction.must(
		// queryBuilder.keyword().onField("enabled").matching(mediaItemSearchCriteria.isEnabled()).createQuery());

		// @SuppressWarnings("rawtypes")
		// BooleanJunction<BooleanJunction> groupJunction = queryBuilder.bool();
		// for (Long groupId : groupIds) {
		// groupJunction.should(queryBuilder.keyword().onField("library.groups.id").matching(groupId).createQuery());
		// }
		// booleanJunction.must(groupJunction.createQuery());

		// // long libraryId = mediaItemSearchCriteria.getLibraryId();
		// // if (libraryId > 0) {
		// //
		// booleanJunction.must(queryBuilder.keyword().onField("library.id").matching(libraryId).createQuery());
		// // }

		// org.apache.lucene.search.Query luceneQuery = booleanJunction.createQuery();
		// FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery,
		// Track.class,
		// MediaItem.class);

		// boolean isReverse = !mediaItemSearchCriteria.isAscending();

		// Sort sort = new Sort(new SortField("displayTitle", SortField.Type.STRING,
		// isReverse));
		// // Sort sort = new Sort(new SortField("lastAccessed", SortField.LONG,
		// // isReverse));
		// MediaSortType mediaSortType = mediaItemSearchCriteria.getMediaSortType();

		// if (mediaSortType == MediaSortType.FAVOURITES) {
		// sort = new Sort(new SortField("vote", SortField.Type.INT, isReverse));
		// } else if (mediaSortType == MediaSortType.LAST_PLAYED) {
		// sort = new Sort(new SortField("lastAccessed", SortField.Type.LONG,
		// isReverse));
		// } else if (mediaSortType == MediaSortType.ALBUM_NAME) {
		// sort = new Sort(new SortField("album.indexText", SortField.Type.STRING,
		// isReverse));
		// } else if (mediaSortType == MediaSortType.ARTIST_NAME) {
		// sort = new Sort(new SortField("artist.indexText", SortField.Type.STRING,
		// isReverse));
		// }

		// query.setSort(sort);

		// int maximumResults = mediaItemSearchCriteria.getMaximumResults();
		// int firstResult = mediaItemSearchCriteria.getPageNumber() * maximumResults;
		// query.setFirstResult(firstResult);
		// query.setMaxResults(maximumResults);

		// @SuppressWarnings("unchecked")
		// List<Track> tracks = query.getResultList();
		// return tracks;
	}

	@Override
	public long getTotalTracksFromLibrary(Long libraryId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select count(t.id) from Track t where t.library.id = :libraryId ");
		TypedQuery<Long> query = entityManager.createQuery(queryBuilder.toString(), Long.class);
		query.setParameter("libraryId", libraryId);
		Long totalTracks = getUniqueResult(query);
		return totalTracks;
	}

}
