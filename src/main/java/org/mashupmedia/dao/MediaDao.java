package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Media;

public interface MediaDao {

	List<Media> getMedia(long libraryId);

	void deleteMediaList(List<Media> mediaList);

	void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	List<AlbumArtImage> getAlbumArtImages(long libraryId);



}
