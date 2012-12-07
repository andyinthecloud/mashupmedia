package org.mashupmedia.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.mashupmedia.comparator.MediaItemComparator;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
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

//		 Query query =
//		 sessionFactory.getCurrentSession().createQuery("delete MediaItem where library.id = :libraryId");
//		 query.setLong("libraryId", libraryId);
////		 query.setCacheable(true);
//		 query.executeUpdate();

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
		
		searchWords = searchWords.replaceAll("\\s.*?\\b", " ").toLowerCase();
		
		List<String> searchWordsList = Arrays.asList(searchWords.split("\\s"));
		String lastWord = searchWordsList.get(searchWordsList.size() - 1);
		lastWord = "\\b" + lastWord + ".*?\\b";
		String suggestionPrefix = StringUtils.trimToEmpty(searchWords.replaceFirst("\\b\\w*$", ""));
		
		MediaItemSearchCriteria mediaItemSearchCriteria = new MediaItemSearchCriteria();
		mediaItemSearchCriteria.setMaximumResults(50);
		mediaItemSearchCriteria.setSearchWords(searchWords + "*");
		List<MediaItem> mediaItems = findMediaItems(mediaItemSearchCriteria);

		Set<String> suggestedTextItems = new HashSet<String>();
		if (mediaItems == null || mediaItems.isEmpty()) {
			return new ArrayList<String>();
		}

		for (MediaItem mediaItem : mediaItems) {
			String searchText = mediaItem.getSearchText();
			String suggestion = StringHelper.find(searchText, lastWord);
			if (searchWordsList.contains(suggestion)) {
				continue;
			}			
			String suggestedTextItem = StringUtils.trimToEmpty(suggestionPrefix + " " + suggestion);			
			suggestedTextItems.add(suggestedTextItem);			
			if (suggestedTextItems.size() > 10) {
				return new ArrayList<String>(suggestedTextItems);
			}
		}
					
		return new ArrayList<String>(suggestedTextItems);

	}

	@Override
	public List<MediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria) {
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
		
		
		MediaType mediaType = mediaItemSearchCriteria.getMediaType();
		if (mediaType != null) {
			String mediaTypeValue = StringHelper.normaliseTextForDatabase(mediaType.toString());
			booleanJunction.must(queryBuilder.keyword().onField("mediaTypeValue").matching(mediaTypeValue).createQuery());			
		}
		
		org.apache.lucene.search.Query luceneQuery = booleanJunction.createQuery();
		Query query = fullTextSession.createFullTextQuery(luceneQuery, MediaItem.class);
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
