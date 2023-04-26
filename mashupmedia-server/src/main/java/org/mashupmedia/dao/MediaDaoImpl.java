package org.mashupmedia.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.util.StringHelper;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MediaDaoImpl extends BaseDaoImpl implements MediaDao {

	@Override
	public List<MediaItem> getMediaItems(long libraryId) {
		Query query = entityManager.createQuery(
				"from MediaItem where library.id = :libraryId order by title");
		query.setParameter("libraryId", libraryId);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaList = (List<MediaItem>) query.getResultList();
		return mediaList;
	}

	@Override
	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages) {

		for (AlbumArtImage albumArtImage : albumArtImages) {
			Album album = albumArtImage.getAlbum();
			if (album == null) {
				log.debug(albumArtImage.getName() + " has no album.");
				continue;
			}

			album.setAlbumArtImage(null);
			// entityManager.saveOrUpdate(album);
			entityManager.remove(albumArtImage);
		}
	}

	@Override
	public List<AlbumArtImage> getAlbumArtImages(long libraryId) {
		Query query = entityManager
				.createQuery("from AlbumArtImage where library.id = :libraryId");
		query.setParameter("libraryId", libraryId);
		@SuppressWarnings("unchecked")
		List<AlbumArtImage> albumArtImages = (List<AlbumArtImage>) query.getResultList();
		return albumArtImages;
	}

	@Override
	public MediaItem getMediaItem(long mediaItemId) {
		Query query = entityManager.createQuery("from MediaItem where id = :mediaItemId");
		query.setParameter("mediaItemId", mediaItemId);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaItems = (List<MediaItem>) query.getResultList();
		if (mediaItems == null || mediaItems.isEmpty()) {
			return null;
		}

		return mediaItems.get(0);
	}
	
	@Override
	public List<MediaItem> getMediaItems(String path) {
		Query query = entityManager.createQuery("from MediaItem where path = :path");
		query.setParameter("path", path);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaItems = (List<MediaItem>) query.getResultList();
		return mediaItems;
	}

	@Override
	public void updateMediaItem(MediaItem mediaItem) {
		saveOrUpdate(mediaItem);

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
		throw new UnsupportedOperationException("Operation no longer supported");
		
		// FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);


		// QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(MediaItem.class)
		// 		.get();
		// @SuppressWarnings("rawtypes")
		// BooleanJunction<BooleanJunction> booleanJunction = queryBuilder.bool();

		// String searchWordsValue = mediaItemSearchCriteria.getSearchWords();
		// String[] searchWords = searchWordsValue.split("\\s");
		// for (String searchWord : searchWords) {
		// 	booleanJunction.must(queryBuilder.keyword().wildcard().onField("searchText").matching(searchWord)
		// 			.createQuery());
		// }

		// MashupMediaType mediaType = mediaItemSearchCriteria.getMediaType();
		// if (mediaType != null) {
		// 	String mediaTypeValue = StringHelper.normaliseTextForDatabase(mediaType.toString());
		// 	booleanJunction.must(queryBuilder.keyword().onField("mediaTypeValue").matching(mediaTypeValue)
		// 			.createQuery());
		// }

		// org.apache.lucene.search.Query luceneQuery = booleanJunction.createQuery();
		// Query query = fullTextEntityManager.createFullTextQuery(luceneQuery, MediaItem.class);
		// int maximumResults = mediaItemSearchCriteria.getMaximumResults();
		// int firstResult = mediaItemSearchCriteria.getPageNumber() * maximumResults;
		// query.setFirstResult(firstResult);
		// query.setMaxResults(maximumResults);

		// @SuppressWarnings("unchecked")
		// List<MediaItem> mediaItems = query.getResultList();
		// Collections.sort(mediaItems, new MediaItemComparator());
		// return mediaItems;
	}

	@Override
	public void saveMediaItem(MediaItem mediaItem) {
		long mediaItemId = mediaItem.getId();
		if (mediaItemId == 0) {
			saveOrUpdate(mediaItem);
		} else {
			entityManager.merge(mediaItem);
		}
	}

	@Override
	public void saveAndFlushMediaItem(MediaItem mediaItem) {
		saveMediaItem(mediaItem);
		entityManager.flush();
	}

}
