package org.mashupmedia.dao;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.mashupmedia.comparator.MediaItemComparator;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.criteria.MediaItemSearchCriteria.MediaSortType;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.util.StringHelper;
import org.springframework.stereotype.Repository;

@Repository
public class MusicDaoImpl extends BaseDaoImpl implements MusicDao {

	@Override
	public List<Album> getAlbums(String searchLetter, int pageNumber, int totalItems) {

		int firstResult = pageNumber * totalItems;

		StringBuilder queryBuilder = new StringBuilder("from Album");
		searchLetter = StringUtils.trimToEmpty(searchLetter);
		if (StringUtils.isNotEmpty(searchLetter)) {
			queryBuilder.append(" where indexLetter = '" + searchLetter.toLowerCase() + "'");
		}
		queryBuilder.append(" order by indexText");

		Query query = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString());
		query.setFirstResult(firstResult);
		query.setMaxResults(totalItems);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Album> albums = (List<Album>) query.list();
		return albums;
	}

	@Override
	public List<String> getAlbumIndexLetters() {
		Query query = sessionFactory.getCurrentSession().createQuery("select distinct indexLetter from Album order by indexLetter");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<String> indexLetters = query.list();
		return indexLetters;
	}

	@Override
	public List<Artist> getArtists() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Artist order by indexText");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Artist> artists = (List<Artist>) query.list();
		return artists;
	}

	@Override
	public Artist getArtist(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Artist where lower(name) = :name");
		query.setCacheable(true);
		query.setString("name", name.toLowerCase());
		Artist artist = (Artist) query.uniqueResult();
		return artist;
	}

	@Override
	public void deleteSongs(List<Song> songs) {
		StringBuilder songIdsBuilder = new StringBuilder();
		Set<Genre> genres = new HashSet<Genre>();

		for (Song song : songs) {
			if (songIdsBuilder.length() > 0) {
				songIdsBuilder.append(",");
			}

			Genre genre = song.getGenre();
			if (genre != null) {
				genres.add(genre);
			}

			songIdsBuilder.append(song.getId());
		}

		deleteEmptyGenres(genres);

		if (songIdsBuilder.length() == 0) {
			return;
		}

		 for (Song song : songs) {
			 sessionFactory.getCurrentSession().delete(song);
		 }
	}

	private void deleteEmptyGenres(Set<Genre> genres) {
		if (genres == null || genres.isEmpty()) {
			return;
		}

		for (Genre genre : genres) {
			Query query = sessionFactory.getCurrentSession().createQuery("select count(s.id) from Song s where s.genre.id = :genreId");
			query.setCacheable(true);
			query.setLong("genreId", genre.getId());
			Long numberOfSongs = (Long) query.uniqueResult();
			if (numberOfSongs > 0) {
				continue;
			}
			
			sessionFactory.getCurrentSession().delete(genre);			
		}

	}

	@Override
	public Album getAlbum(String artistName, String albumName) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from Album where lower(artist.name) = :artistName and lower(name) = :albumName");
		query.setCacheable(true);
		query.setString("artistName", artistName.toLowerCase());
		query.setString("albumName", albumName.toLowerCase());
		Album album = (Album) query.uniqueResult();
		return album;
	}

	@Override
	public Album getAlbum(long albumId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album where id = :id");
		query.setCacheable(true);
		query.setLong("id", albumId);
		Album album = (Album) query.uniqueResult();
		return album;
	}

	@Override
	public Song getSong(long libraryId, String songPath, long songSizeInBytes) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Song where library.id = :libraryId and path = :path and sizeInBytes = :sizeInBytes");
		query.setCacheable(true);
		query.setLong("libraryId", libraryId);
		query.setString("path", songPath);
		query.setLong("sizeInBytes", songSizeInBytes);

		@SuppressWarnings("unchecked")
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
		Query query = sessionFactory.getCurrentSession().createQuery("from Song where library.id = :libraryId and updatedOn < :updatedOn");
		query.setCacheable(true);
		query.setLong("libraryId", libraryId);
		query.setDate("updatedOn", date);

		@SuppressWarnings("unchecked")
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
		saveOrUpdate(artist);

		Album album = song.getAlbum();
		saveOrUpdate(album);
		song.setAlbum(album);

		saveOrUpdate(song.getYear());
		saveOrUpdate(song.getGenre());
		saveOrUpdate(song);

		if (isSessionFlush) {
			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().clear();
			logger.debug("Flushed and cleared session.");
		}

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
	public List<Album> getRandomAlbums(int numberOfAlbums) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album order by rand()");
		@SuppressWarnings("unchecked")
		List<Album> albums = query.setMaxResults(numberOfAlbums).list();
		return albums;
	}

	@Override
	public Year getYear(int year) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Year where year = :year");
		query.setCacheable(true);
		query.setInteger("year", year);
		Year album = (Year) query.uniqueResult();
		return album;
	}

	@Override
	public Genre getGenre(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Genre where name = :name");
		query.setCacheable(true);
		query.setString("name", name);
		Genre genre = (Genre) query.uniqueResult();
		return genre;
	}

	@Override
	public List<Song> getSongs(Long albumId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Song where album.id = :albumId order by trackNumber");
		query.setCacheable(true);
		query.setLong("albumId", albumId);

		@SuppressWarnings("unchecked")
		List<Song> songs = query.list();
		return songs;
	}

	@Override
	public void deleteAlbum(Album album) {
		Artist artist = album.getArtist();
		sessionFactory.getCurrentSession().delete(album);
		List<Album> albums = getAlbumsByArtist(artist.getId());
		if (albums == null || albums.isEmpty()) {
			sessionFactory.getCurrentSession().delete(artist);
		}
	}

	@Override
	public void deleteArtist(Artist artist) {
		sessionFactory.getCurrentSession().delete(artist);
	}

	@Override
	public List<Album> getAlbumsByArtist(long artistId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album where artist.id = :artistId order by name");
		query.setCacheable(true);
		query.setLong("artistId", artistId);
		@SuppressWarnings("unchecked")
		List<Album> albums = query.list();
		return albums;
	}

	@Override
	public List<String> getArtistIndexLetters() {
		Query query = sessionFactory.getCurrentSession().createQuery("select distinct indexLetter from Artist order by indexLetter");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<String> indexLetters = query.list();
		return indexLetters;
	}

	@Override
	public Artist getArtist(Long artistId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Artist where id = :artistId");
		query.setCacheable(true);
		query.setLong("artistId", artistId);
		Artist artist = (Artist) query.uniqueResult();
		return artist;
	}

	@Override
	public List<Genre> getGenres() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Genre order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Genre> genres = query.list();
		return genres;
	}
	
	@Override
	public List<MediaItem> findSongs(MediaItemSearchCriteria mediaItemSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		
		QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(MediaItem.class).get();
		@SuppressWarnings("rawtypes")
		BooleanJunction<BooleanJunction> booleanJunction = queryBuilder.bool();

		String searchWordsValue = mediaItemSearchCriteria.getSearchWords();
		String[] searchWords = searchWordsValue.split("\\s");		
		for (String searchWord : searchWords) {
			booleanJunction.must(queryBuilder.keyword().wildcard().onField("searchText").matching(searchWord).createQuery());
		}		
		
		
//		MediaType mediaType = mediaItemSearchCriteria.getMediaType();
//		if (mediaType != null) {
			String mediaTypeValue = StringHelper.normaliseTextForDatabase(MediaType.SONG.toString());
			booleanJunction.must(queryBuilder.keyword().onField("mediaTypeValue").matching(mediaTypeValue).createQuery());			
//		}
		
		org.apache.lucene.search.Query luceneQuery = booleanJunction.createQuery();
		org.hibernate.search.FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, MediaItem.class);
		
		Sort sort = new Sort(new SortField("title", SortField.STRING));
		
		boolean isDescending = mediaItemSearchCriteria.isDescending();
		MediaSortType mediaSortType = mediaItemSearchCriteria.getMediaItemSortType();
		if (mediaSortType == MediaSortType.FAVOURITES) {
			sort = new Sort(new SortField("vote", SortField.INT, isDescending));
		} else if (mediaSortType == MediaSortType.LAST_PLAYED) {
			sort = new Sort(new SortField("lastAccessed", SortField.LONG, isDescending));
		}else if (mediaSortType == MediaSortType.ALBUM_NAME) {
			sort = new Sort(new SortField("album.name", SortField.STRING, isDescending));
		} else if (mediaSortType == MediaSortType.ARTIST_NAME) {
			sort = new Sort(new SortField("artist.name", SortField.STRING, isDescending));
		}  
		
		query.setSort(sort);
		
		int maximumResults = mediaItemSearchCriteria.getMaximumResults();
		int firstResult = mediaItemSearchCriteria.getPageNumber() * maximumResults;
		query.setFirstResult(firstResult);
		query.setMaxResults(maximumResults);

		@SuppressWarnings("unchecked")
		List<MediaItem> mediaItems = query.list();
		Collections.sort(mediaItems, new MediaItemComparator());
		return mediaItems;
	}

}
