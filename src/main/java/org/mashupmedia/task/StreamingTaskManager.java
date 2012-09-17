package org.mashupmedia.task;

import java.io.File;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MediaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class StreamingTaskManager {

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private TaskExecutor ftpDownloadTaskExecutor;

	@Autowired
	private TaskExecutor localDownloadTaskExecutor;

	private class DownloadTask implements Runnable {
		private File file;
		private long mediaItemId;

		private DownloadTask(File file, long mediaItemId) {
			this.file = file;
			this.mediaItemId = mediaItemId;
		}

		@Override
		public void run() {
			connectionManager.startMediaItemStream(mediaItemId, file);
		}
	}

//	public StreamingTaskManager(TaskExecutor taskExecutor) {
//		this.ftpDownloadTaskExecutor = taskExecutor;
//	}

	public File startMediaItemDownload(long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		Library library = mediaItem.getLibrary();
		Location location = library.getLocation();
		File file = connectionManager.getMediaItemStreamFile(mediaItemId);
		if (location instanceof FtpLocation) {
			ftpDownloadTaskExecutor.execute(new DownloadTask(file, mediaItemId));
		} else {
			localDownloadTaskExecutor.execute(new DownloadTask(file, mediaItemId));
		}

		return file;
	}

}
