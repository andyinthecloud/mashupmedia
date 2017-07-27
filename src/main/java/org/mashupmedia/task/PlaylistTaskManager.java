package org.mashupmedia.task;

import org.apache.log4j.Logger;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
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

	public void updateNowPlaying(MediaItem mediaItem) {
		PlaylistTask logStreamTask = new PlaylistTask(mediaItem);
		playlistThreadPoolTaskExecutor.execute(logStreamTask);
	}

	private class PlaylistTask implements Runnable {

		private MediaItem mediaItem;

		public PlaylistTask(MediaItem mediaItem) {
			this.mediaItem = mediaItem;
		}

		@Override
		public void run() {

			long mediaItemId = mediaItem.getId();
			Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.ALL);
			PlaylistMediaItem currentPlaylistMediaItem = PlaylistHelper.processRelativePlayingMediaItemFromPlaylist(playlist, 0, true);

			if (mediaItem.equals(currentPlaylistMediaItem.getMediaItem())) {
				return;
			}

			User user = AdminHelper.getLoggedInUser();
			PlaylistMediaItem playlistMediaItem = PlaylistHelper.getPlaylistMediaItem(playlist, mediaItemId);
			playlistManager.saveUserPlaylistMediaItem(user, playlistMediaItem);
			logger.info("Updated playlist to media item: " + mediaItem.getPath());

		}
	}
}
