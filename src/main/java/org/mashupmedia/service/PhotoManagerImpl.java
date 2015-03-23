package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.dao.PhotoDao;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PhotoManagerImpl implements PhotoManager{
	
	@Autowired
	private PhotoDao photoDao;

	@Override
	public List<Photo> getLatestPhotos() {
		List<Photo> photos = photoDao.getLatestPhotos();
		return photos;
	}

	@Override
	public List<Album> getAlbums() {
		List<Album> albums = photoDao.getAlbums();
		return albums;
	}

}
