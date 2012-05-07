package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Media;

public interface MediaManager {

	public List<Media> getMedia(long libraryId);

	public void deleteMediaList(List<Media> mediaList);


	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	public List<AlbumArtImage> getAlbumArtImages(long libraryId);


}
