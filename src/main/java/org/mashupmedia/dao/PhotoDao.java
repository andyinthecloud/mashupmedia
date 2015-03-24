package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;

public interface PhotoDao {


	void savePhoto(Photo photo, boolean isSessionFlush);

	Photo getPhotoByAbsolutePath(String path);

	List<Album> getAlbums(String albumName);

	List<Photo> getLatestPhotos(int firstResult);

	List<Album> getAlbums();

}
