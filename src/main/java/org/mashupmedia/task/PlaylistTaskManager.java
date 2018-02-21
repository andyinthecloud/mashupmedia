package org.mashupmedia.task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.mashupmedia.encode.FfMpegManager;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class PlaylistTaskManager {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ThreadPoolTaskExecutor playlistThreadPoolTaskExecutor;

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private FfMpegManager ffMpegManager;

	@Autowired
	private ProcessManager processManager;

	public void updateNowPlaying(MediaItem mediaItem) {
		NowPlayingTask logStreamTask = new NowPlayingTask(mediaItem);
		playlistThreadPoolTaskExecutor.execute(logStreamTask);
	}

	private class NowPlayingTask implements Runnable {

		private MediaItem mediaItem;

		public NowPlayingTask(MediaItem mediaItem) {
			this.mediaItem = mediaItem;
		}

		@Override
		public void run() {

			long mediaItemId = mediaItem.getId();
			Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.ALL);
			PlaylistMediaItem currentPlaylistMediaItem = PlaylistHelper
					.processRelativePlayingMediaItemFromPlaylist(playlist, 0, true);

			if (mediaItem.equals(currentPlaylistMediaItem.getMediaItem())) {
				return;
			}

			User user = AdminHelper.getLoggedInUser();
			PlaylistMediaItem playlistMediaItem = PlaylistHelper.getPlaylistMediaItem(playlist, mediaItemId);
			playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);
			logger.info("Updated playlist to media item: " + mediaItem.getPath());

		}
	}

	private class CreateTemporaryPlaylistFileTask implements Runnable {

		private File mediaFile;
		private File playlistTemporaryFile;

		public CreateTemporaryPlaylistFileTask(long playlistId, File mediaFile) {
			this.mediaFile = mediaFile;
			this.playlistTemporaryFile = FileHelper.createTemporaryPlaylistFile(playlistId);
		}

		@Override
		public void run() {
			try {
				List<String> commands = ffMpegManager.queueCopyAudioWithoutTags(mediaFile, playlistTemporaryFile);
				processManager.callProcess(commands);
			} catch (MediaItemEncodeException e) {
				logger.error("Unable to copy file", e);
			} catch (IOException e) {
				logger.error("Unable to copy file", e);
			}

		}

	}

	public File getTemporaryPlaylistFile(long playlistId, File mediaFile) {
		CreateTemporaryPlaylistFileTask createTemporaryPlaylistFileTask = new CreateTemporaryPlaylistFileTask(
				playlistId, mediaFile);
		playlistThreadPoolTaskExecutor.execute(createTemporaryPlaylistFileTask);
		return createTemporaryPlaylistFileTask.playlistTemporaryFile;
	}
}
