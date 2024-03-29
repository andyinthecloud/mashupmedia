package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.mashupmedia.dao.PhotoDao;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.util.MediaItemHelper.MediaItemSequenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Lazy(true)
@Transactional (isolation = Isolation.READ_UNCOMMITTED)
public class PhotoManagerImpl implements PhotoManager {

	@Autowired
	private MashupMediaSecurityManager securityManager;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private PhotoDao photoDao;

	@Override	
	public List<Photo> getLatestPhotos(int pageNumber, int totalItems) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Photo> photos = photoDao.getLatestPhotos(userGroupIds, pageNumber, totalItems);
		return photos;
	}

	@Override	
	public List<Album> getAlbums(MediaItemSequenceType mediaItemSequenceType) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Album> albums = photoDao.getAlbums(userGroupIds, mediaItemSequenceType);
		return albums;
	}

	@Override
	public Album getAlbum(long albumId) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		Album album = photoDao.getAlbum(userGroupIds, albumId);
		return album;
	}

	@Override
	public Photo getPhoto(Long photoId, MediaItemSequenceType mediaItemSequenceType) {

		MediaItem mediaItem = mediaManager.getMediaItem(photoId);
		if (!mediaItem.getClass().isAssignableFrom(Photo.class)) {
			throw new MashupMediaRuntimeException("Expected to find a photo but got " + mediaItem.getClass().getName()
					+ " instead for id = " + photoId);
		}

		Photo photo = (Photo) mediaItem;
		preparePhotoSequence(photo, mediaItemSequenceType);
		return photo;
	}

	protected void preparePhotoSequence(Photo photo, MediaItemSequenceType mediaItemSequenceType) {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		Date takenOn = photo.getTakenOn();
		Long albumId = photo.getAlbum().getId();

		Photo previousPhoto = photoDao.getPreviousPhotoInSequence(userGroupIds, takenOn, albumId, mediaItemSequenceType);
		if (previousPhoto == null) {
			previousPhoto = photoDao.getLastPhotoInSequence(userGroupIds, takenOn, albumId, mediaItemSequenceType);
		}
		photo.setPreviousPhoto(previousPhoto);
		
		Photo nextPhoto = photoDao.getNextPhotoInSequence(userGroupIds, takenOn, albumId, mediaItemSequenceType);
		if (nextPhoto == null) {
			nextPhoto = photoDao.getFirstPhotoInSequence(userGroupIds, takenOn, albumId, mediaItemSequenceType);
		}		
		photo.setNextPhoto(nextPhoto);
		
	}

}
