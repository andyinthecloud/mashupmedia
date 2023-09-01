package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.SearchMediaItem;
import org.mashupmedia.model.media.music.AlbumArtImage;

public interface MediaDao {

	List<MediaItem> getMediaItems(long libraryId);

	void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	List<AlbumArtImage> getAlbumArtImages(long libraryId);

	MediaItem getMediaItem(long mediaItemId);

	void updateMediaItem(MediaItem mediaItem);

	void saveMediaItem(MediaItem mediaItem);

	void saveAndFlushMediaItem(MediaItem mediaItem);

	List<MediaItem> getMediaItems(String path);

}
