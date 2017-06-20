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

import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaItem;

public class MediaItemSequenceInputStream {

	private Logger logger = Logger.getLogger(getClass());

	private SequenceInputStream sequenceInputStream;
	private List<MediaItem> mediaItems;
	private long length;

	public MediaItemSequenceInputStream(List<MediaItem> mediaItems) {

		this.mediaItems = mediaItems;

		List<BufferedInputStream> bufferedInputStreams = new ArrayList<BufferedInputStream>();
		for (MediaItem mediaItem : mediaItems) {
			try {
				File file = new File(mediaItem.getPath());
				Path path = file.toPath();
				length += Files.size(path);

				BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(path));
				bufferedInputStreams.add(bufferedInputStream);
			} catch (IOException e) {
				logger.error("File not found error", e);
			}
		}

		this.sequenceInputStream = new SequenceInputStream(Collections.enumeration(bufferedInputStreams));
	}

	public MediaItem getMediaItem(long startBytesPosition) {

		if (mediaItems == null || mediaItems.isEmpty()) {
			return null;
		}

		long totalBytes = 0;
		for (MediaItem mediaItem : mediaItems) {
			File file = new File(mediaItem.getPath());
			Path path = file.toPath();

			try {
				totalBytes += Files.size(path);
				if (totalBytes > startBytesPosition) {
					return mediaItem;
				}
			} catch (IOException e) {
				logger.error("Unable to get file size", e);
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

}
