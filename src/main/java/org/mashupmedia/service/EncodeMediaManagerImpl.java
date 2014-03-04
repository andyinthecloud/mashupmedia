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

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Video;
import org.mashupmedia.service.ConnectionManager.EncodeType;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.FileHelper.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncodeMediaManagerImpl implements EncodeMediaManager {
	private Logger logger = Logger.getLogger(getClass());
	
	

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConfigurationManager configurationManager;
	
	@Autowired
	private EncodeManager encodeManager;

	@Override
	public synchronized void encodeMedia(long mediaItemId) {


		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

		String pathToFfMpeg = configurationManager.getConfigurationValue(MashUpMediaConstants.FFMPEG_PATH);
		if (StringUtils.isBlank(pathToFfMpeg)) {
			logger.info("Unable to encode media, ffmpeg is not configured.");
			mediaItem.setEncodeStatusType(EncodeStatusType.ERROR);
			mediaManager.saveMediaItem(mediaItem);
			return;
		}

		try {
			EncodeStatusType encodeStatusType = mediaItem.getEncodeStatusType();

			if (encodeStatusType == EncodeStatusType.PROCESSING) {
				logger.info("Media file is being encoded, exiting...");
				return;
			} 

			mediaItem.setEncodeStatusType(EncodeStatusType.PROCESSING);
			mediaManager.saveMediaItem(mediaItem);

			logger.info("Starting to encode media file to html5 format");

			Library library = mediaItem.getLibrary();

			File inputFile = connectionManager.getMediaItemStreamFile(mediaItemId, EncodeType.UNPROCESSED);
			File outputFile = FileHelper.createMediaFile(library.getId(), mediaItemId, FileType.MEDIA_ITEM_STREAM_ENCODED);
			boolean isDeleted = FileHelper.deleteFile(outputFile);
			
			if (!isDeleted) {
				logger.info("Exiting, unable to delete encoded media file: " + outputFile.getAbsolutePath());
				return;
			}
			
			boolean hasError = false;
			
			if (mediaItem instanceof Song) {
				hasError = encodeManager.encodeAudioToHtml5(pathToFfMpeg, inputFile, outputFile);	
			} else if (mediaItem instanceof Video) {
				hasError = encodeManager.encodeVideoToHtml5(pathToFfMpeg, inputFile, outputFile);	
			}
			
			if (hasError) {
				logger.info("FFMpeg reported an error saving media file to html5 format");
				mediaItem.setEncodeStatusType(EncodeStatusType.ERROR);								
			} else {
				logger.info("Media file decoded to html5 format");
				mediaItem.setEncodeStatusType(EncodeStatusType.ENCODED);				
			}

			mediaManager.saveMediaItem(mediaItem);
		} catch (Exception e) {
			logger.error("Error encoding media item: " + mediaItemId, e);
			mediaItem.setEncodeStatusType(EncodeStatusType.ERROR);
			mediaManager.saveMediaItem(mediaItem);
		} 

	}

}
