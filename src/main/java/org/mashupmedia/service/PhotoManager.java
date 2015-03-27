package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;

public interface PhotoManager {

	public List<Photo> getLatestPhotos();

	public List<Album> getAlbums();

	public Album getAlbum(long albumId);	
}
