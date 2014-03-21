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
	public void encodeMedia(long mediaItemId, MediaContentType mediaContentType) {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

		try {
			boolean isCurrentlyEncoding = processManager.isInProcessQueue(mediaItemId, mediaContentType);
			if (isCurrentlyEncoding) {
				logger.info("Media file is being encoded, exiting...");
				return;
			}

			logger.info("Starting to encode media file to html5 format");

			encodeManager.encodeMediaItem(mediaItem, mediaContentType);

			logger.info("Media file decoded to " + mediaContentType.getName());
			MediaEncoding mediaEncoding = mediaManager.getMediaEncoding(mediaContentType);
			mediaItem.addMediaEncoding(mediaEncoding);
			mediaManager.saveMediaItem(mediaItem);
		} catch (Exception e) {
			logger.error("Error encoding media item: " + mediaItemId, e);
		}

	}

}
