package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

public class PlaylistHelper {

	public static void replacePlaylist(Playlist playlist, List<? extends MediaItem> tracks) {
		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems != null) {
			playlistMediaItems.clear();
		} else {
			playlistMediaItems = new ArrayList<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (tracks == null || tracks.isEmpty()) {
			return;
		}

		for (int i = 0; i < tracks.size(); i++) {
			PlaylistMediaItem playlistTrack = new PlaylistMediaItem();
			playlistTrack.setMediaItem(tracks.get(i));
			playlistTrack.setRanking(i);
			playlistTrack.setPlaylist(playlist);
			playlistMediaItems.add(playlistTrack);
		}

		if (playlistMediaItems.isEmpty()) {
			return;
		}

		playlistMediaItems.get(0).setPlaying(true);
		playlist.setPlaylistMediaItems(playlistMediaItems);
	}

	public static List<MediaItem> getMediaItems(List<PlaylistMediaItem> playlistMediaItems) {
		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return mediaItems;
		}

		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			mediaItems.add(playlistMediaItem.getMediaItem());
		}

		return mediaItems;
	}

	public static void appendPlaylist(Playlist playlist, List<? extends MediaItem> mediaItems) {

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null) {
			playlistMediaItems = new ArrayList<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (mediaItems == null || mediaItems.isEmpty()) {
			return;
		}

		int totalPlaylistItems = playlistMediaItems.size();

		for (int i = 0; i < mediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = new PlaylistMediaItem();
			playlistMediaItem.setMediaItem(mediaItems.get(i));
			playlistMediaItem.setRanking(totalPlaylistItems + i);
			playlistMediaItem.setPlaylist(playlist);
			playlistMediaItems.add(playlistMediaItem);
		}

		playlist.setPlaylistMediaItems(playlistMediaItems);
	}

	public static void replacePlaylist(Playlist playlist, Track track) {
		if (track == null) {
			return;
		}

		List<Track> tracks = new ArrayList<Track>();
		tracks.add(track);
		replacePlaylist(playlist, tracks);
	}

	public static void appendPlaylist(Playlist playlist, MediaItem mediaItem) {
		if (mediaItem == null) {
			return;
		}

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		mediaItems.add(mediaItem);
		appendPlaylist(playlist, mediaItems);
	}

	public static PlaylistMediaItem navigatePlaylist(Playlist playlist, int relativeOffset,
			boolean isSetPlayingStatus) {

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return null;
		}

		User user = AdminHelper.getLoggedInUser();
		PlaylistMediaItem currentPlaylistMediaItem = user.getPlaylistMediaItem();

		int playingIndex = 0;
		for (int i = 0; i < playlistMediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(i);
			if (playlistMediaItem.equals(currentPlaylistMediaItem)) {
				playingIndex = i;
			}

			if (isSetPlayingStatus) {
				playlistMediaItem.setPlaying(false);
			}
		}

		int newPlayingIndex = playingIndex + relativeOffset;
		if (newPlayingIndex < 0 || newPlayingIndex > (playlistMediaItems.size() - 1)) {
			playlistMediaItems.get(playingIndex).setPlaying(true);
			return currentPlaylistMediaItem;
		}

		PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(newPlayingIndex);
		if (isSetPlayingStatus) {
			playlistMediaItem.setPlaying(true);
		}
		return playlistMediaItem;
	}

	public static PlaylistMediaItem getFirstPlayListMediaItem(Playlist playlist) {
		if (playlist == null) {
			return null;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return null;
		}

		return playlistMediaItems.get(0);
	}

	public static boolean canSavePlaylist(Playlist playlist) {
		if (playlist == null) {
			return false;
		}

		User createdBy = playlist.getCreatedBy();
		if (createdBy == null) {
			return true;
		}
		long createdById = createdBy.getId();

		User user = AdminHelper.getLoggedInUser();
		long userId = user.getId();

		if (createdById == userId) {
			return true;
		}

		return false;
	}

	public static void initialiseCurrentlyPlaying(Playlist playlist) {
		if (playlist == null) {
			return;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return;
		}

		User user = AdminHelper.getLoggedInUser();
		if (user == null) {
			return;
		}

		PlaylistMediaItem userPlaylistMediaItem = user.getPlaylistMediaItem();
		if (userPlaylistMediaItem == null) {
			playlistMediaItems.get(0).setPlaying(true);
			return;
		}

		long userPlaylistMediaItemId = userPlaylistMediaItem.getMediaItem().getId();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			long playlistMediaItemId = playlistMediaItem.getMediaItem().getId();
			if (playlistMediaItemId == userPlaylistMediaItemId) {
				playlistMediaItem.setPlaying(true);
				return;
			}
		}

		playlistMediaItems.get(0).setPlaying(true);

	}

	public static PlaylistType getPlaylistType(String playlistTypeValue) {
		playlistTypeValue = StringUtils.trimToEmpty(playlistTypeValue);
		if (StringUtils.isEmpty(playlistTypeValue)) {
			return PlaylistType.MUSIC;
		}

		PlaylistType[] playlistTypes = PlaylistType.values();
		for (PlaylistType playlistType : playlistTypes) {
			if (playlistType.getValue().equalsIgnoreCase(playlistTypeValue)) {
				return playlistType;
			}
		}
		return PlaylistType.MUSIC;

	}

	public static PlaylistMediaItem getPlaylistMediaItem(Playlist playlist, Long mediaItemId,
			boolean isSetPlayingStatus) {

		if (mediaItemId == null || mediaItemId == 0) {
			return null;
		}

		if (playlist == null) {
			return null;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return null;
		}

		User user = AdminHelper.getLoggedInUser();
		if (user == null) {
			return null;
		}

		if (isSetPlayingStatus) {
			for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
				playlistMediaItem.setPlaying(false);
			}
		}

		Optional<PlaylistMediaItem> optionalPlaylistMediaItem = playlistMediaItems.stream()
				.filter(pmi -> pmi.getMediaItem().getId() == mediaItemId)
				.findAny();

		if (isSetPlayingStatus && optionalPlaylistMediaItem.isPresent()) {
			optionalPlaylistMediaItem.get().setPlaying(true);			
		}

		return optionalPlaylistMediaItem.orElse(null);

	}

}
