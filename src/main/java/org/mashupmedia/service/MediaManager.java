package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;

public interface MediaManager {

	public List<MediaItem> getMedia(long libraryId);

	public MediaItem getMediaItem(long mediaItemId);

	public void deleteMediaList(List<MediaItem> mediaList);


	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	public List<AlbumArtImage> getAlbumArtImages(long libraryId);


}
