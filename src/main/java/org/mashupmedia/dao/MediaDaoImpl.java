package org.mashupmedia.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.StringHelper;
import org.springframework.stereotype.Repository;

@Repository
public class MediaDaoImpl extends BaseDaoImpl implements MediaDao {

	@Override
	public List<MediaItem> getMedia(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from MediaItem where library.id = :libraryId order by title");
		query.setLong("libraryId", libraryId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaList = (List<MediaItem>) query.list();
		return mediaList;
	}

	@Override
	public void deleteMediaList(List<MediaItem> mediaItems) {

		// Query query =
		// sessionFactory.getCurrentSession().createQuery("delete MediaItem where library.id = :libraryId order by title");
		// query.setLong("libraryId", libraryId);
		// query.setCacheable(true);

		for (MediaItem mediaItem : mediaItems) {
			sessionFactory.getCurrentSession().delete(mediaItem);
		}

	}

	@Override
	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages) {

		for (AlbumArtImage albumArtImage : albumArtImages) {
			Album album = albumArtImage.getAlbum();
			if (album == null) {
				logger.debug(albumArtImage.getName() + " has no album.");
				continue;
			}

			album.setAlbumArtImage(null);
			// sessionFactory.getCurrentSession().saveOrUpdate(album);
			sessionFactory.getCurrentSession().delete(albumArtImage);
		}
	}

	@Override
	public List<AlbumArtImage> getAlbumArtImages(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from AlbumArtImage where library.id = :libraryId");
		query.setLong("libraryId", libraryId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<AlbumArtImage> albumArtImages = (List<AlbumArtImage>) query.list();
		return albumArtImages;
	}

	@Override
	public MediaItem getMediaItem(long mediaItemId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from MediaItem where id = :mediaItemId");
		query.setLong("mediaItemId", mediaItemId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaItems = (List<MediaItem>) query.list();
		if (mediaItems == null || mediaItems.isEmpty()) {
			return null;
		}

		return mediaItems.get(0);
	}

	@Override
	public void updateMediaItem(MediaItem mediaItem) {
		sessionFactory.getCurrentSession().update(mediaItem);

	}

	@Override
	public List<String> findAutoCompleteMediaItems(String searchWords) {

		searchWords = StringUtils.trimToEmpty(searchWords);
		if (StringUtils.isEmpty(searchWords)) {
			return new ArrayList<String>();
		}
		
		String[] searchWordsArray = searchWords.split("\\s");
		String lastWord = searchWordsArray[searchWordsArray.length - 1];
		String suggestionPrefix = StringUtils.trimToEmpty(searchWords.replaceFirst("\\b\\w*$", ""));
		
		MediaItemSearchCriteria mediaItemSearchCriteria = new MediaItemSearchCriteria();
		mediaItemSearchCriteria.setFetchSize(10);
		mediaItemSearchCriteria.setSearchWords(searchWords + "*");
		List<MediaItem> mediaItems = findMediaItems(mediaItemSearchCriteria);

		List<String> suggestedWords = new ArrayList<String>();
		if (mediaItems == null || mediaItems.isEmpty()) {
			return suggestedWords;
		}

		for (MediaItem mediaItem : mediaItems) {
			String searchText = mediaItem.getSearchText();
			String suggestion = StringHelper.find(searchText, lastWord);
			suggestedWords.add(suggestionPrefix + " " + suggestion);
		}
		
		return suggestedWords;

	}

	private List<MediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(MediaItem.class).get();
		org.apache.lucene.search.Query luceneQuery = queryBuilder.keyword().wildcard().onField("searchText").matching(mediaItemSearchCriteria.getSearchWords())
				.createQuery();
		Query query = fullTextSession.createFullTextQuery(luceneQuery, MediaItem.class);
		query.setFetchSize(mediaItemSearchCriteria.getFetchSize());
		query.setFirstResult(mediaItemSearchCriteria.getFirstResult());

		@SuppressWarnings("unchecked")
		List<MediaItem> mediaItems = query.list();
		return mediaItems;
	}
}
