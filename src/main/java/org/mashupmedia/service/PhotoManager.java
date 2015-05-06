package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;

public interface PhotoManager {
	
	public enum PhotoSequenceType {
		PREVIOUS, NEXT
	}

	public List<Photo> getLatestPhotos(int pageNumber, int totalItems);

	public List<Album> getAlbums();

	public Album getAlbum(long albumId);

	public Photo getPhotoInSequence(Photo photo, PhotoSequenceType sequenceType);	
}
