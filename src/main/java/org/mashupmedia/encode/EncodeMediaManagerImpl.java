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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncodeMediaManagerImpl implements EncodeMediaManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private FfMpegManager encodeManager;
	
	@Autowired
	private ProcessManager processManager;

	@Override
	public synchronized void encodeMedia(long mediaItemId, MediaContentType mediaContentType) {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

		try {
//			EncodeStatusType encodeStatusType = mediaItem.getEncodeStatusType();
//
//			if (encodeStatusType == EncodeStatusType.PROCESSING) {
//				logger.info("Media file is being encoded, exiting...");
//				return;
//			}
//
//			mediaItem.setEncodeStatusType(EncodeStatusType.PROCESSING);
//			mediaManager.saveMediaItem(mediaItem);

			boolean isCurrentlyEncoding = processManager.isCurrentlyEncoding(mediaItemId, mediaContentType);
			if (isCurrentlyEncoding) {
				logger.info("Media file is being encoded, exiting...");
				return;				
			}
						
			logger.info("Starting to encode media file to html5 format");

//			MediaContentType mediaContentType = MediaContentType.UNSUPPORTED;

//			if (mediaItem instanceof Song) {
//				mediaContentType = MediaContentType.MP3;
//			} else if (mediaItem instanceof Video) {
//				mediaContentType = MediaContentType.WEBM;
//			}

			String outputText = encodeManager.encodeMediaItem(mediaItem, mediaContentType);
			outputText = StringUtils.trimToEmpty(outputText);
			boolean hasError = false;
			if (outputText.matches("^Error")) {
				hasError = true;
			}

			if (hasError) {
				logger.error("FFMpeg reported an error saving media file to html5 format, please view the log file to get more information.");
//				mediaItem.setEncodeStatusType(EncodeStatusType.ERROR);
			} else {
				logger.info("Media file decoded to " + mediaContentType.getName());
//				mediaItem.setEncodeStatusType(EncodeStatusType.ENCODED);
				MediaEncoding mediaEncoding = mediaManager.getMediaEncoding(mediaContentType);
				mediaItem.addMediaEncoding(mediaEncoding);
			}

			mediaManager.saveMediaItem(mediaItem);
		} catch (Exception e) {
			logger.error("Error encoding media item: " + mediaItemId, e);
//			mediaItem.setEncodeStatusType(EncodeStatusType.ERROR);
//			mediaManager.saveMediaItem(mediaItem);
		}

	}

}
