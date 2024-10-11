package org.mashupmedia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ConnectionManagerImpl implements ConnectionManager {

	@Autowired
	private final MediaManager mediaManager;

	@Override
	public byte[] getAlbumArtImageBytes(MetaImage image, ImageType imageType) throws IOException {
		if (image == null) {
			return null;
		}

		String filePath = null;
		if (imageType == ImageType.THUMBNAIL) {
			filePath = image.getThumbnailUrl();
		} else {
			filePath = image.getUrl();
		}

		byte[] bytes = getFileBytes(filePath);
		return bytes;
	}

	private byte[] getFileBytes(String filePath) throws IOException {
		if (StringUtils.isBlank(filePath)) {
			return null;
		}

		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(fileInputStream);
		IOUtils.closeQuietly(fileInputStream);
		return bytes;
	}

	// @Override
	// public long getMediaItemFileSize(long mediaItemId) {
	// 	MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
	// 	if (mediaItem == null) {
	// 		log.error("Unable to start media stream, no media type found");
	// 		return 0;
	// 	}

	// 	MediaResource mediaResource = mediaItem.getMediaResourceForWeb();
	// 	long size = mediaResource.getSizeInBytes();
	// 	return size;
	// }

}
