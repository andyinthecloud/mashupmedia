package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;

public interface MediaManager {

	public List<MediaItem> getMediaItemsForLibrary(long libraryId);

	public MediaItem getMediaItem(long mediaItemId);

	public void deleteMediaItems(List<MediaItem> mediaList);


	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	public List<AlbumArtImage> getAlbumArtImages(long libraryId);

	public void updateMediaItem(MediaItem mediaItem);


}
