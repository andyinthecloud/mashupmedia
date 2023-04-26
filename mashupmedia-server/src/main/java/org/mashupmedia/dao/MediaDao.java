package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.AlbumArtImage;

public interface MediaDao {

	List<MediaItem> getMediaItems(long libraryId);

	void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	List<AlbumArtImage> getAlbumArtImages(long libraryId);

	MediaItem getMediaItem(long mediaItemId);

	void updateMediaItem(MediaItem mediaItem);

	List<String> findAutoCompleteMediaItems(String searchWords);

	List<MediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria);

	void saveMediaItem(MediaItem mediaItem);

	void saveAndFlushMediaItem(MediaItem mediaItem);

	List<MediaItem> getMediaItems(String path);

}
