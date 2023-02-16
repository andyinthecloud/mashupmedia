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

	public static PlaylistMediaItem getPlaylistMediaItem(Playlist playlist, Long mediaItemId) {

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

		Optional<PlaylistMediaItem> optionalPlaylistMediaItem = playlistMediaItems.stream()
				.filter(pmi -> pmi.getMediaItem().getId() == mediaItemId)
				.findAny();

		if (optionalPlaylistMediaItem.isPresent()) {
			optionalPlaylistMediaItem.get().setPlaying(true);
		}

		return optionalPlaylistMediaItem.orElse(null);

	}

	public static PlaylistMediaItem getPlaylistMediaItemByProgress(Playlist playlist, long progress) {
		if (playlist == null) {
			return null;
		}

		
		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		Optional<PlaylistMediaItem> optionalPlayingMediaItem = playlistMediaItems
			.stream()
			.filter(pmi -> pmi.isPlaying())
			.findFirst();

		int index = 0;
		if (optionalPlayingMediaItem.isPresent()) {
			index = playlistMediaItems.indexOf(optionalPlayingMediaItem.get());
		}

		long cumulativeEndSeconds = 0;
		List<PlaylistMediaItem> remainingPlaylistMediaItems = playlistMediaItems.subList(index, playlistMediaItems.size() - 1);
		for(PlaylistMediaItem pmi : remainingPlaylistMediaItems) {
			if (pmi.getMediaItem() instanceof Track track) {
				cumulativeEndSeconds += track.getTrackLength();
				if (progress < cumulativeEndSeconds) {
					return pmi;
				}
			} 
		};

		return optionalPlayingMediaItem.orElse(playlistMediaItems.get(0));

	}

    public static void processPlayingMediaItem(Playlist playlist, PlaylistMediaItem playlistMediaItem) {
		if (playlist == null) {
			return;
		}

		if (playlistMediaItem == null) {
			return;
		}

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		for (PlaylistMediaItem pmi : playlistMediaItems) {
			pmi.setPlaying(pmi.equals(playlistMediaItem));
		}
    }

	// public static long getCumulativeEndSeconds(Playlist playlist, PlaylistMediaItem playlistMediaItem) {
	// 	if (playlist == null) {
	// 		return 0;
	// 	}

	// 	long cumulativeEndSeconds = 0;
	// 	List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
	// 	for (PlaylistMediaItem pmi : playlistMediaItems) {
	// 		if (!(pmi.getMediaItem() instanceof Track)) {
	// 			continue;
	// 		}

	// 		Track track = (Track) pmi.getMediaItem();
	// 		cumulativeEndSeconds += track.getTrackLength();

			
	// 		if (playlistMediaItem.equals(pmi)) {
	// 			return cumulativeEndSeconds;
	// 		}
			
	// 	}

	// 	return 0;

	// }

}
