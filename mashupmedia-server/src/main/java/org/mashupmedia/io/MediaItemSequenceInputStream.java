package org.mashupmedia.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MediaItemSequenceInputStream {


	private SequenceInputStream sequenceInputStream;
	private List<MediaItem> mediaItems;
	private long length;

	public MediaItemSequenceInputStream(List<MediaItem> mediaItems, MediaContentType mediaContentType) {

		this.mediaItems = mediaItems;

		List<BufferedInputStream> bufferedInputStreams = new ArrayList<BufferedInputStream>();
		for (MediaItem mediaItem : mediaItems) {
			try {
				MediaResource mediaResource = mediaItem.getMediaResource(mediaContentType);
				
				// Path path = mediaResource.getPath()  file.toPath();
				// length += Files.size(path);
				length += mediaResource.getSizeInBytes();

				// BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(path));
				BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(null));
				bufferedInputStreams.add(bufferedInputStream);
			} catch (IOException e) {
				log.error("File not found error", e);
			}
		}

		this.sequenceInputStream = new SequenceInputStream(Collections.enumeration(bufferedInputStreams));
	}

	public MediaItem getMediaItem_delete(long startBytesPosition) {

		if (mediaItems == null || mediaItems.isEmpty()) {
			return null;
		}

		long totalBytes = 0;
		for (MediaItem mediaItem : mediaItems) {
			// File file = new File(mediaItem.getPath());

			File file = new File("");
			Path path = file.toPath();

			try {
				totalBytes += Files.size(path);
				if (totalBytes > startBytesPosition) {
					return mediaItem;
				}
			} catch (IOException e) {
				log.error("Unable to get file size", e);
			}

		}
		return mediaItems.get(mediaItems.size() - 1);
	}

	public SequenceInputStream getSequenceInputStream() {
		return sequenceInputStream;
	}

	public long getLength() {
		return length;
	}
	
	public boolean isPlaylist() {
		if (mediaItems == null || mediaItems.isEmpty()) {
			return false;
		}
		
		if (mediaItems.size() == 1) {
			return false;
		}
		
		return true;
		
	}

}
