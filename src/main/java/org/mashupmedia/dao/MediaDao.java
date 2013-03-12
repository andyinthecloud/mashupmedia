package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;

public interface MediaDao {

	public List<MediaItem> getMedia(long libraryId);

	public void deleteMediaList(List<MediaItem> mediaList);

	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	public List<AlbumArtImage> getAlbumArtImages(long libraryId);

	public MediaItem getMediaItem(long mediaItemId);

	public void updateMediaItem(MediaItem mediaItem);

	public List<String> findAutoCompleteMediaItems(String searchWords);

	public List<MediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria);

	public void saveMediaItem(MediaItem mediaItem);

}
