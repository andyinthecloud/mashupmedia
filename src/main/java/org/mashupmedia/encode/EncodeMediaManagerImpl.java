/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.encode;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EncodeMediaManagerImpl implements EncodeMediaManager {

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ProcessManager processManager;

	@Override
	public void encodeMedia(ProcessQueueItem processQueueItem) {

		long mediaItemId = processQueueItem.getMediaItemId();
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		MediaContentType mediaContentType = processQueueItem.getMediaContentType();

		try {
			boolean isCurrentlyEncoding = false;
			if (processQueueItem.getProcess() != null) {
				isCurrentlyEncoding = true;
			}

			if (isCurrentlyEncoding) {
				log.info("Media file is being encoded, exiting...");
				return;
			}

			log.info("Starting to encode media file to html5 format");

			processManager.startProcess(processQueueItem);

			log.info("Media file decoded to " + mediaContentType.getName());
			MediaEncoding mediaEncoding = new MediaEncoding();
			mediaEncoding.setMediaContentType(mediaContentType);
			mediaEncoding.setOriginal(false);
			Set<MediaEncoding> mediaEncodings = mediaItem.getMediaEncodings();
			if (mediaEncodings == null) {
				mediaEncodings = new HashSet<MediaEncoding>();
				mediaItem.setMediaEncodings(mediaEncodings);
			}
			mediaEncodings.add(mediaEncoding);
			
			long sizeInBytes = getSizeInBytes(mediaItem, mediaEncoding);
			mediaItem.setSizeInBytes(sizeInBytes);
			mediaManager.saveMediaItem(mediaItem);
		} catch (Exception e) {
			log.error("Error encoding media item: " + mediaItemId, e);
		}

	}

	private long getSizeInBytes(MediaItem mediaItem, MediaEncoding mediaEncoding) {
		File originalMediaFile =  new File(mediaItem.getPath());		
		File encodedMediaFile = FileHelper.getMediaFile(mediaItem, mediaEncoding);
		if (!encodedMediaFile.exists()) {
			return originalMediaFile.length();			
		}
		
		if (encodedMediaFile.length() == 0) {
			return originalMediaFile.length();
		}
		
		return encodedMediaFile.length();
	}

}
