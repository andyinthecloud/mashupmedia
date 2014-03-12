package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MediaManagerImpl implements MediaManager {

	@Autowired
	private MediaDao mediaDao;

	@Autowired
	private SecurityManager securityManager;

	@Override
	public List<MediaItem> getMediaItemsForLibrary(long libraryId) {
		List<MediaItem> mediaList = mediaDao.getMedia(libraryId);
		return mediaList;
	}

	@Override
	public MediaItem getMediaItem(long mediaItemId) {
		MediaItem mediaItem = mediaDao.getMediaItem(mediaItemId);
		return mediaItem;
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

	@Override
	public void updateMediaItem(MediaItem mediaItem) {
		mediaItem.setUpdatedOn(new Date());
		mediaDao.updateMediaItem(mediaItem);
	}

	@Override
	public List<String> findAutoCompleteMediaItems(String searchWords) {
		List<String> suggestionWords = mediaDao.findAutoCompleteMediaItems(searchWords);
		return suggestionWords;
	}

	@Override
	public List<MediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria) {
		List<MediaItem> mediaItems = mediaDao.findMediaItems(mediaItemSearchCriteria);
		return mediaItems;
	}

	@Override
	public void saveMediaItem(MediaItem mediaItem) {
		mediaItem.setUpdatedOn(new Date());
		mediaDao.saveMediaItem(mediaItem);
	}
	
	@Override
	public void saveMediaEncoding(MediaEncoding mediaEncoding) {
		mediaDao.saveMediaEncoding(mediaEncoding);		
	}
	
	@Override
	public MediaEncoding getMediaEncoding(MediaContentType mediaContentType) {
		MediaEncoding mediaEncoding = mediaDao.getMediaEncoding(mediaContentType);
		return mediaEncoding;
	}

}
