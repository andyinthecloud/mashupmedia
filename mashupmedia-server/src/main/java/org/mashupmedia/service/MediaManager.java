package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.AlbumArtImage;

public interface MediaManager {

	List<MediaItem> getMediaItemsForLibrary(long libraryId);

	MediaItem getMediaItem(long mediaItemId);

	void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	List<AlbumArtImage> getAlbumArtImages(long libraryId);

	void updateMediaItem(MediaItem mediaItem);

	List<String> findAutoCompleteMediaItems(String searchWords);

	List<MediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria);

	void saveMediaItem(MediaItem mediaItem);

}
