package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.streaming.StreamingController;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dao.ConfigurationDao;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.model.Configuration;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MediaManagerImpl implements MediaManager {

	@Autowired
	private MediaDao mediaDao;
	
	@Autowired
	private ConfigurationDao configurationDao; 


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
	public MediaContentType[] getSuppliedStreamingMediaContentTypes(LibraryType libraryType) {
		MediaContentType[] suppliedMediaContentTypes = null;
		if (libraryType == LibraryType.MUSIC) {
			suppliedMediaContentTypes = StreamingController.ESSENTIAL_MUSIC_STREAMING_CONTENT_TYPES;
		} else {
			suppliedMediaContentTypes = StreamingController.ESSENTIAL_MUSIC_STREAMING_CONTENT_TYPES;
		}
				
		Configuration configuration = configurationDao.getConfiguration(MashUpMediaConstants.SUPPLIED_MUSIC_STREAMING_FORMATS);
		if (configuration == null) {
			return suppliedMediaContentTypes;
		}
		
		String value = StringUtils.trimToEmpty(configuration.getValue());
		if (StringUtils.isEmpty(value)) {
			return suppliedMediaContentTypes;
		}
		
		List<MediaContentType> mediaContentTypes = new ArrayList<MediaContentType>();
		String[] formats = value.split(StringHelper.TEXT_DELIMITER);
		for (String format : formats) {
			MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(format);
			mediaContentTypes.add(mediaContentType);			
		}
		
		if (mediaContentTypes.isEmpty()) {
			return suppliedMediaContentTypes;
		}
		
		suppliedMediaContentTypes = mediaContentTypes.toArray(new MediaContentType[mediaContentTypes.size()]);
		return suppliedMediaContentTypes;
	}
}
