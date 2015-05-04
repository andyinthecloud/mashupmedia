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
public class PhotoManagerImpl implements PhotoManager {

	@Autowired
	private SecurityManager securityManager;
	
	@Autowired
	private PhotoDao photoDao;

	@Override
	public List<Photo> getLatestPhotos(int pageNumber, int totalItems) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Photo> photos = photoDao.getLatestPhotos(userGroupIds, pageNumber, totalItems);
		return photos;
	}

	@Override
	public List<Album> getAlbums() {
		List<Album> albums = photoDao.getAlbums();
		return albums;
	}

	@Override
	public Album getAlbum(long albumId) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		Album album = photoDao.getAlbum(userGroupIds, albumId);
		return album;
	}

}
