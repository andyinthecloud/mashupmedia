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

import java.util.Iterator;

import org.mashupmedia.encode.EncodeMediaManager;
import org.mashupmedia.encode.FfMpegManager;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.encode.ProcessQueueItem;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class EncodeMediaItemTaskManager {

	@Autowired
	private ThreadPoolTaskExecutor encodeMediaItemThreadPoolTaskExecutor;

	@Autowired
	private EncodeMediaManager encodeMediaManager;

	@Autowired
	private FfMpegManager ffMpegManager;

	@Autowired
	private ProcessManager processManager;

	public void processMediaItemForEncodingDuringAutomaticUpdate(MediaItem mediaItem, MediaContentType mediaContentType) {
		MediaContentType savedMediaContentType = MediaContentType.UNSUPPORTED;
		MediaEncoding mediaEncoding = mediaItem.getBestMediaEncoding();
		if (mediaEncoding != null) {
			savedMediaContentType = mediaEncoding.getMediaContentType();
		}

		if (savedMediaContentType == MediaContentType.UNSUPPORTED) {
			processMediaItemForEncoding(mediaItem, mediaContentType);
		}
	}

	public void processMediaItemForEncoding(MediaItem mediaItem, MediaContentType mediaContentType) {
		ffMpegManager.prepareMediaItemBeforeEncoding(mediaItem, mediaContentType);
		processQueue();
	}
	
		

	public void processQueue() {
 		Iterator<ProcessQueueItem> iterator = processManager.getProcessQueueItemsIterator();

		if (iterator == null) {
			return;
		}

		int totalRunningProcesses = 0;

		while (iterator.hasNext()) {
			ProcessQueueItem processQueueItem = iterator.next();
			if (processQueueItem.getProcess() != null) {
				totalRunningProcesses++;
			}
		}

		int maximumConcurrentProcesses = processManager.getMaximumConcurrentProcesses();

		int availableProcesses = maximumConcurrentProcesses - totalRunningProcesses;
		if (availableProcesses <= 0) {
			return;
		}

		iterator = processManager.getProcessQueueItemsIterator();

		for (int i = 0; i < availableProcesses; i++) {

			while (iterator.hasNext()) {
				ProcessQueueItem processQueueItem = iterator.next();
				if (processQueueItem.getProcess() != null) {
					continue;
				}
 				encodeMediaItemThreadPoolTaskExecutor.execute(new EncodeMediaQueueTask(processQueueItem));
				break;
			}

		}

	}

	private class EncodeMediaQueueTask implements Runnable {
		private ProcessQueueItem processQueueItem;

		public EncodeMediaQueueTask(ProcessQueueItem processQueueItem) {
			this.processQueueItem = processQueueItem;
		}

		public void run() {
			encodeMediaManager.encodeMedia(processQueueItem);

		}
	}

}
