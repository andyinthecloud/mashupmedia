package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;

public interface MediaDao {

	List<MediaItem> getMedia(long libraryId);

	void deleteMediaList(List<MediaItem> mediaList);

	void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	List<AlbumArtImage> getAlbumArtImages(long libraryId);

}
