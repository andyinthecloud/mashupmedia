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
import java.io.IOException;

import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.FileHelper.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

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
		mediaItem.setEncodeStatusType(EncodeStatusType.PROCESSING);
		mediaManager.saveMediaItem(mediaItem);

		File originalFile = connectionManager.getMediaItemStreamFile(mediaItemId);
		if (originalFile.length() == 0) {
			mediaItem.setEncodeStatusType(EncodeStatusType.UNPROCESSED);
			mediaManager.saveMediaItem(mediaItem);
			originalFile = connectionManager.getMediaItemStreamFile(mediaItemId);			
		}		

		// create a media reader
		IMediaReader reader = ToolFactory.makeReader(originalFile.getAbsolutePath());

		// add a viewer to the reader, to see progress as the media is
		// transcoded
//		reader.addListener(ToolFactory.makeViewer(true));
		reader.addListener(ToolFactory.makeDebugListener());
//		reader.addListener(ToolFactory.isTurboCharged());

		// add a viewer to the reader, to see the decoded media
		Library library = mediaItem.getLibrary();
		File encodedFile = FileHelper.createMediaFile(library.getId(), mediaItemId, FileType.MEDIA_ITEM_ENCODED);
		encodedFile.createNewFile();
		reader.addListener(ToolFactory.makeWriter(encodedFile.getAbsolutePath(), reader));

		// read and decode packets from the source file and and dispatch decoded
		// audio and video to the writer
		while (reader.readPacket() == null) {
			do {
			} while (false);
		}

		logger.info("Media file decoded to ogg format");
		
		mediaItem.setEncodeStatusType(EncodeStatusType.ENCODED);
		mediaManager.saveMediaItem(mediaItem);
	}

}
