package org.mashupmedia.dao;

import java.util.Date;
import java.util.List;

import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.service.PhotoManager.PhotoSequenceType;

public interface PhotoDao {


	public void savePhoto(Photo photo, boolean isSessionFlush);

	public Photo getPhotoByAbsolutePath(String path);

	public List<Album> getAlbums(String albumName);

	public List<Photo> getLatestPhotos(List<Long> groupIds, int firstResult, int totalItems);

	public List<Album> getAlbums();

	public List<Photo> getObsoletePhotos(long libraryId, Date date);

	public int removeObsoletePhotos(long libraryId, Date date);

	public Album getAlbum(List<Long> groupIds, long albumId);

	public Photo getPhotoInSequence(List<Long> groupIds, Date photoCreatedOn, Long albumId,PhotoSequenceType photoSequenceType);

}
