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

package org.mashupmedia.service.transcode.local;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.shadow.org.terracotta.utilities.io.Files;
import org.mashupmedia.encode.ProcessHelper;
import org.mashupmedia.encode.ProcessQueueItem;
import org.mashupmedia.encode.command.EncodeCommands;
import org.mashupmedia.encode.command.FfMpegCommands;
import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MediaItemTranscodeException;
import org.mashupmedia.exception.MediaItemTranscodeException.EncodeExceptionType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.storage.StorageManager;
import org.mashupmedia.service.transcode.TranscodeAudioManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocalTranscodeAudioManagerImpl implements TranscodeAudioManager{

	private final EncodeCommands encodeCommands = new FfMpegCommands();
	private final ConfigurationManager configurationManager;
	private final MediaManager mediaManager;
	private final StorageManager storageManager;


	@Value("${mashupmedia.transcode.audio.total-threads}")
	private int totalThreads;
	private ThreadPoolExecutor threadPoolExecutor;

	@Value("${mashupmedia.transcode.audio.ffmpeg-path}")
	private String ffMpegPath;
	private boolean isFFMpegInstalled;


    @Value("${mashupmedia.transcode.audio.format}")
    private String transcodeAudioFormat;

    private MediaContentType audioTranscodeContentType;




	
	// @Value("${mashupmedia.encode.audio}")
	// private final String encodeAudio; 


	// private List<ProcessQueueItem> processQueueItems = new CopyOnWriteArrayList<ProcessQueueItem>();
	// private List<ProcessQueueItem> processQueueItems = new ArrayList<ProcessQueueItem>();

	@PostConstruct
	private void postConstruct() {

		isFFMpegInstalled = isTranscoderInstalled();
		if (!isFFMpegInstalled) {
			log.info("ffmpeg installation not found using variable: mashupmedia.encode.audio.ffmpeg-path = " + ffMpegPath);
		}
		
		threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(totalThreads);
		this.audioTranscodeContentType = MediaContentType.getMediaContentType(transcodeAudioFormat);
		
	}

	@Override
	public void processTrack(Track track, String resourceId){

		if (!isFFMpegInstalled) {
			log.info("Unable to transcode track, ffMpeg not installed");
			return;
		}

		if (StringUtils.isBlank(resourceId)) {
			return;
		}

		User user = AdminHelper.getLoggedInUser();

		threadPoolExecutor.submit(() -> {
			if (track.isTranscoded(audioTranscodeContentType)) {
				return;
			}

			Path inputPath = Path.of(resourceId);
			Path outputPath = user.createTempResourcePath();
			try {
				processMediaItemForEncoding(track, inputPath, outputPath);


			} catch (MediaItemTranscodeException e) {
				log.error("Error transcoding track", e);
			}

		});

		
	}

	// public void processMediaItemForEncodingDuringAutomaticUpdate(MediaItem mediaItem) {

	// 	// MediaContentType mediaContentType = Arrays.stream(MediaContentType.values())
	// 	// .filter(mct -> mct.name().equalsIgnoreCase(encodeAudio))
	// 	// .findAny().orElse(MediaContentType.AUDIO_AAC);



	// 	MediaContentType encodeMediaContentType = mediaItem.getEncodeMediaContentType();
	// 	Assert.notNull(encodeMediaContentType, "encodeMediaContentType should not be null");



	// 	// MediaContentType savedMediaContentType = MediaContentType.MEDIA_UNSUPPORTED;
	// 	// MediaEncoding mediaEncoding = mediaItem.getBestMediaEncoding();
	// 	// if (mediaEncoding != null) {
	// 	// 	savedMediaContentType = mediaEncoding.getMediaContentType();
	// 	// }





	// 	// if (savedMediaContentType == MediaContentType.MEDIA_UNSUPPORTED) {
	// 		try {
				
	// 			processMediaItemForEncoding(mediaItem);
	// 		} catch (MediaItemEncodeException exception) {
	// 			log.error("Error while encoding media item", exception);
	// 		}
	// 	// }
	// }

	public String getEncoderPath() {
		return configurationManager.getConfigurationValue(encodeCommands.getEncoderPathKey());
	}

	public void saveEncoderPath(String path) {
		configurationManager.saveConfiguration(encodeCommands.getEncoderPathKey(), path);
	}

	public boolean isTranscoderInstalled() {
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

	private  void processMediaItemForEncoding(MediaItem mediaItem, Path inputPath, Path outputPath)
			throws MediaItemTranscodeException {

				if (mediaItem.isTranscoded(audioTranscodeContentType)) {
					return;
				}

		long mediaItemId = mediaItem.getId();
		// MediaItem mostRecentMediaItem = mediaManager.getMediaItem(mediaItemId);
		// if (mostRecentMediaItem.isEncodedForWeb()) {
		// 	return;
		// }

		String encoderPath = configurationManager.getConfigurationValue(encodeCommands.getEncoderPathKey());

		// MediaContentType mediaContentType = mediaItem.getTranscodeMediaContentType();
		MediaContentType mediaContentType = getMediaContentType(mediaItem.getMashupMediaType()); 
		List<String> processCommands = encodeCommands.getEncodingProcessCommands(encoderPath,
				mediaContentType, inputPath, outputPath);
			
		// ProcessQueueItem processQueueItem = generateProcessQueueItem(mediaItemId, mediaContentType, processCommands);
		ProcessQueueItem processQueueItem = ProcessQueueItem.builder()
		.mediaItemId(mediaItemId)
		.mediaContentType(mediaContentType)
		.commands(processCommands)
		.inputPath(inputPath)
		.outputPath(outputPath)
		.build();
		
		
		transcode(processQueueItem);

		// Check the input is uploaded through library
		String libraryPath = mediaItem.getLibrary().getPath();
		if (StringUtils.isBlank(libraryPath)) {
			try {
				Files.delete(inputPath);
			} catch (IOException e) {
				throw new MediaItemTranscodeException(EncodeExceptionType.UNABLE_TO_DELETE_TEMPORARY_FILE, "Unable to delete input file", e);
			}
		} 


	}

	// private ProcessQueueItem generateProcessQueueItem(long mediaItemId,
	// 		MediaContentType mediaContentType, List<String> commands) {

	// 	ProcessQueueItem processQueueItem = new ProcessQueueItem(mediaItemId, mediaContentType, commands);
	// 	// return processQueueItem;

	// 	return ProcessQueueItem.builder()
	// 	.mediaItemId(mediaItemId)
	// 	.mediaContentType(mediaContentType)
	// 	.commands(commands)
	// 	.inputPath(null)
	// 	.outputPath(null)
	// 	.build();
	// }

	// private void encode(ProcessQueueItem processQueueItem) {

	// 	if (processQueueItems.contains(processQueueItem)) {
	// 		log.info("Media is already queued for encoding: " + processQueueItem.toString());
	// 	}
	// 	processQueueItems.add(processQueueItem);
	// 	encodeQueue();
	// }

	private MediaContentType getMediaContentType(MashupMediaType mashupMediaType) {

		MediaContentType mediaContentType = null;
		if (mashupMediaType == MashupMediaType.MUSIC) {
			mediaContentType = audioTranscodeContentType;
		}
		return mediaContentType;
	}

	private void transcode(ProcessQueueItem processQueueItem) {

		// processQueueItems.iterator().forEachRemaining(processQueueItem -> {
		// 	if (processQueueItem.getProcessStartedOn() != null) {
		// 		return;
		// 	}

			try {
				log.info("Starting to encode media file");
				processQueueItem.setProcessStartedOn(new Date());
				ProcessHelper.callProcess(processQueueItem.getCommands());

				MediaItem mediaItem = mediaManager.getMediaItem(processQueueItem.getMediaItemId());
				MediaContentType mediaContentType = processQueueItem.getMediaContentType();

				log.info("Media file decoded to " + mediaContentType.name());
				MediaResource mediaResource = new MediaResource();
				mediaResource.setMediaContentType(mediaContentType);
				mediaResource.setOriginal(false);
				mediaItem.getMediaResources().add(mediaResource);
				// Set<MediaResource> mediaEncodings = mediaItem.getMediaResources();
				// if (mediaEncodings == null) {
				// 	mediaEncodings = new HashSet<MediaResource>();
				// 	mediaItem.setMediaResources(mediaEncodings);
				// }
				// mediaEncodings.add(mediaResource);
				
				// long sizeInBytes = getSizeInBytes(mediaItem, mediaResource);
				// mediaItem.setSizeInBytes(sizeInBytes);
				mediaManager.saveMediaItem(mediaItem);
				storageManager.store(processQueueItem.getOutputPath());
				Files.delete(processQueueItem.getOutputPath());
				// encodeQueue();

			} catch(IOException e) {
				// processQueueItems.remove(processQueueItem);
				log.error("Error processing processQueueItem, remove from queues", e);
			}
		// });
	}


	// private long getSizeInBytes(MediaItem mediaItem, MediaResource mediaEncoding) {
	// 	File originalMediaFile =  new File(mediaItem.getPath());		
	// 	File encodedMediaFile = FileHelper.getMediaFile(mediaItem, mediaEncoding);
	// 	if (!encodedMediaFile.exists()) {
	// 		return originalMediaFile.length();			
	// 	}
		
	// 	if (encodedMediaFile.length() == 0) {
	// 		return originalMediaFile.length();
	// 	}
		
	// 	return encodedMediaFile.length();
	// }

}
