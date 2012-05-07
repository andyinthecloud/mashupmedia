package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MediaManagerImpl implements MediaManager{
	
	@Autowired
	private MediaDao mediaDao;

	@Override
	public List<Media> getMedia(long libraryId) {
		List<Media> mediaList = mediaDao.getMedia(libraryId);
		return mediaList;
	}

	@Override
	public void deleteMediaList(List<Media> mediaList) {
		mediaDao.deleteMediaList(mediaList);		
	}

	@Override
	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages) {
		mediaDao.deleteAlbumArtImages(albumArtImages);
		
	}
	
	@Override
	public List<AlbumArtImage> getAlbumArtImages(long libraryId) {
		List<AlbumArtImage> albumArtImages = mediaDao.getAlbumArtImages(libraryId);
		return albumArtImages;
	}
	
}
