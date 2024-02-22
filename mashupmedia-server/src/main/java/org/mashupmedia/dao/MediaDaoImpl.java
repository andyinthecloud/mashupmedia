package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.MusicArtImage;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;

@Repository
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
	public List<MusicArtImage> getAlbumArtImages(long libraryId) {
		Query query = entityManager
				.createQuery("from AlbumArtImage where library.id = :libraryId");
		query.setParameter("libraryId", libraryId);
		@SuppressWarnings("unchecked")
		List<MusicArtImage> albumArtImages = (List<MusicArtImage>) query.getResultList();
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
