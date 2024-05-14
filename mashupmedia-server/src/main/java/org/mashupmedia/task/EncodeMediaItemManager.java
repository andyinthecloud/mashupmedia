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

package org.mashupmedia.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.encode.ProcessHelper;
import org.mashupmedia.encode.ProcessQueueItem;
import org.mashupmedia.encode.command.EncodeCommands;
import org.mashupmedia.encode.command.FfMpegCommands;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.FileHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EncodeMediaItemManager {

	private final EncodeCommands encodeCommands = new FfMpegCommands();
	private final ConfigurationManager configurationManager;
	private final MediaManager mediaManager;


	private List<ProcessQueueItem> processQueueItems = new CopyOnWriteArrayList<ProcessQueueItem>();

	public void processMediaItemForEncodingDuringAutomaticUpdate(MediaItem mediaItem,
			MediaContentType mediaContentType) {
		MediaContentType savedMediaContentType = MediaContentType.MEDIA_UNSUPPORTED;
		MediaEncoding mediaEncoding = mediaItem.getBestMediaEncoding();
		if (mediaEncoding != null) {
			savedMediaContentType = mediaEncoding.getMediaContentType();
		}

		if (savedMediaContentType == MediaContentType.MEDIA_UNSUPPORTED) {
			try {
				processMediaItemForEncoding(mediaItem, mediaContentType);
			} catch (MediaItemEncodeException exception) {
				log.error("Error while encoding media item", exception);
			}
		}
	}

	public String getEncoderPath() {
		return configurationManager.getConfigurationValue(encodeCommands.getEncoderPathKey());
	}

	public void saveEncoderPath(String path) {
		configurationManager.saveConfiguration(encodeCommands.getEncoderPathKey(), path);
	}

	public boolean isEncoderInstalled() {
		String encoderPath = configurationManager.getConfigurationValue(encodeCommands.getEncoderPathKey());
		return isEncoderInstalled(encoderPath);
	}

	public boolean isEncoderInstalled(String encoderPath) {
		if (StringUtils.isBlank(encoderPath)) {
			return false;
		}

		List<String> commands = new ArrayList<>();
		commands.add(encoderPath);
		commands.add("-version");
		
		try {
			String outputText = ProcessHelper.callProcess(commands);
			return outputText.contains(encodeCommands.getTestOutputParameter());
		} catch (IOException e) {
			log.info("Error calling ffMpeg", e);
			return false;
		}
	}

	@Async
	public void processMediaItemForEncoding(MediaItem mediaItem, MediaContentType mediaContentType)
			throws MediaItemEncodeException {

		long mediaItemId = mediaItem.getId();
		MediaItem mostRecentMediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mostRecentMediaItem.isEncodedForWeb()) {
			return;
		}

		String encoderPath = configurationManager.getConfigurationValue(encodeCommands.getEncoderPathKey());
		List<String> processCommands = encodeCommands.getEncodingProcessCommands(encoderPath, mostRecentMediaItem,
				mediaContentType);

		ProcessQueueItem processQueueItem = generateProcessQueueItem(mediaItemId, mediaContentType, processCommands);
		encode(processQueueItem);


	}

	private ProcessQueueItem generateProcessQueueItem(long mediaItemId,
			MediaContentType mediaContentType, List<String> commands) {

		ProcessQueueItem processQueueItem = new ProcessQueueItem(mediaItemId, mediaContentType, commands);
		return processQueueItem;
	}

	private void encode(ProcessQueueItem processQueueItem) {

		if (processQueueItems.contains(processQueueItem)) {
			log.info("Media is already queued for encoding: " + processQueueItem.toString());
		}
		processQueueItems.add(processQueueItem);
		encodeQueue();
	}

	private void encodeQueue() {

		processQueueItems.iterator().forEachRemaining(processQueueItem -> {
			if (processQueueItem.getProcessStartedOn() != null) {
				return;
			}

			try {
				log.info("Starting to encode media file");
				processQueueItem.setProcessStartedOn(new Date());
				ProcessHelper.callProcess(processQueueItem.getCommands());

				MediaItem mediaItem = mediaManager.getMediaItem(processQueueItem.getMediaItemId());
				MediaContentType mediaContentType = processQueueItem.getMediaContentType();

				log.info("Media file decoded to " + mediaContentType.name());
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
				encodeQueue();

			} catch(IOException e) {
				processQueueItems.remove(processQueueItem);
				log.error("Error processing processQueueItem, remove from queues", e);
			}
		});
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
