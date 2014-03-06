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

import org.mashupmedia.encode.EncodeMediaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class EncodeMediaItemTaskManager {
	
	@Autowired
	private ThreadPoolTaskExecutor encodeMediaItemThreadPoolTaskExecutor;

	@Autowired
	private EncodeMediaManager encodeMediaManager;

	public void encodeMediaItem(long mediaItemId) {
		encodeMediaItemThreadPoolTaskExecutor.execute(new EncodeMediaItemTask(mediaItemId));
	}

	private class EncodeMediaItemTask implements Runnable {

		private long mediaItemId;

		public EncodeMediaItemTask(long mediaItemId) {
			this.mediaItemId = mediaItemId;
		}

		public void run() {
			encodeMediaManager.encodeMedia(mediaItemId);
		}
	}

}
