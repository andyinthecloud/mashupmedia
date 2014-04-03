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

import org.mashupmedia.encode.EncodeMediaManager;
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

	public void processMediaItemForEncoding(MediaItem mediaItem, long fileLastModified, long savedMediaItemFileLastModified, MediaContentType mediaContentType) {
		
		
//		MediaContentType mediaContentType = MediaContentType.UNSUPPORTED;
		MediaEncoding mediaEncoding = mediaItem.getBestMediaEncoding();
		if (mediaEncoding != null) {
			return;
//			mediaContentType = mediaEncoding.getMediaContentType();
		}

//		long mediaItemFileLastModifiedOn = mediaItem.getFileLastModifiedOn();
		
		if (fileLastModified > savedMediaItemFileLastModified && mediaContentType == MediaContentType.UNSUPPORTED) {
			
			encodeMediaItemThreadPoolTaskExecutor.execute(new EncodeMediaItemTask(mediaItem.getId(), mediaContentType));
			
//			encodeMediaItemTaskManager.queueMediaItemForEncoding(video.getId(), mediaContentType);
		}
		
		
		
		
//		encodeMediaItemThreadPoolTaskExecutor.execute(new EncodeMediaItemTask(mediaItemId, mediaContentType));
	}

	private class EncodeMediaItemTask implements Runnable {

		private long mediaItemId;
		private MediaContentType mediaContentType;

		public EncodeMediaItemTask(long mediaItemId, MediaContentType mediaContentType) {
			this.mediaItemId = mediaItemId;
			this.mediaContentType = mediaContentType;
		}

		public void run() {
			encodeMediaManager.encodeMedia(mediaItemId, mediaContentType);
		}
	}

}
