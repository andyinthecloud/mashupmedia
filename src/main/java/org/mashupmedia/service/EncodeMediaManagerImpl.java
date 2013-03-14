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

package org.mashupmedia.service;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EncodeMediaManagerImpl implements EncodeMediaManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ConnectionManager connectionManager;
	
	@Autowired
	private MediaManager mediaManager;

	@Override
	public void encodeMedia(long mediaItemId) throws IOException {
		
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

		EncodeStatusType encodeStatusType = mediaItem.getEncodeStatusType();
		
		if (encodeStatusType == EncodeStatusType.ENCODED) {
			logger.info("Media file has already been encoded, exiting...");
			return;
		} else if (encodeStatusType == EncodeStatusType.PROCESSING) {
			logger.info("Media file is being encoded, exiting...");
			return;			
		}

		logger.info("Starting to decode media file to ogg format");
		
		

		logger.info("Media file decoded to ogg format");
		
		mediaItem.setEncodeStatusType(EncodeStatusType.ENCODED);
		mediaManager.saveMediaItem(mediaItem);
	}
	
	

}
