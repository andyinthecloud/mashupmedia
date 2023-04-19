package org.mashupmedia.service.playlist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.media.playlist.PlaylistActionStatusType;
import org.mashupmedia.encode.FfMpegManager;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.repository.playlist.PlaylistRepository;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlaylistActionManagerImpl implements PlaylistActionManager {

	private final FfMpegManager ffMpegManager;
	private final PlaylistRepository playlistRepository;

	@Override
	public PlaylistActionStatusType replacePlaylist(long playlistId, List<? extends MediaItem> mediaItems) {
		Playlist playlist = playlistRepository.getReferenceById(playlistId);
		Set<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems != null) {
			playlistMediaItems.clear();
		} else {
			playlistMediaItems = new HashSet<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (mediaItems == null || mediaItems.isEmpty()) {
			return PlaylistActionStatusType.OK;
		}

		for (int i = 0; i < mediaItems.size(); i++) {
			PlaylistMediaItem playlistMediaItem = new PlaylistMediaItem();
			playlistMediaItem.setMediaItem(mediaItems.get(i));
			playlistMediaItem.setRanking(i);
			playlistMediaItem.setPlaying(i == 0);
			playlistMediaItem.setPlaylist(playlist);
			playlistMediaItems.add(playlistMediaItem);
		}

		if (playlistMediaItems.isEmpty()) {
			return PlaylistActionStatusType.OK;
		}

		playlist.setPlaylistMediaItems(playlistMediaItems);
		playlistRepository.save(playlist);

		return sendForEncoding(playlistMediaItems);
	}

	private PlaylistActionStatusType sendForEncoding(Set<PlaylistMediaItem> playlistMediaItems) {


		Set<MediaItem> mediaItemsForEncoding = playlistMediaItems
				.stream()
				.map(pmi -> pmi.getMediaItem())
				.filter(mi -> !mi.isEncodedForWeb())
				.collect(Collectors.toSet());

		if (mediaItemsForEncoding == null || mediaItemsForEncoding.isEmpty()) {
			return PlaylistActionStatusType.OK;
		}

		if (!ffMpegManager.isFfMpegInstalled()) {
			return PlaylistActionStatusType.FFMPEG_NOT_INSTALLED;
		}

		for (MediaItem mediaItem : mediaItemsForEncoding) {
			try {
				ffMpegManager.queueMediaItemForEncoding(mediaItem,
						MediaItemHelper.getDefaultMediaContentType(mediaItem));
			} catch (MediaItemEncodeException e) {
				log.error("Error encoding media", e);
			}
		}

		return PlaylistActionStatusType.ITEMS_SENT_FOR_ENCODING;

	}

	@Override
	public PlaylistActionStatusType appendPlaylist(long playlistId, List<? extends MediaItem> mediaItems) {
		Playlist playlist = playlistRepository.getReferenceById(playlistId);

		Set<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null) {
			playlistMediaItems = new HashSet<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (mediaItems == null || mediaItems.isEmpty()) {
			return PlaylistActionStatusType.OK;
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
		playlistRepository.save(playlist);
		
		return sendForEncoding(playlistMediaItems);
	}

	@Override
	public PlaylistActionStatusType replacePlaylist(long playlistId, MediaItem mediaItem) {
		if (mediaItem == null) {
			return PlaylistActionStatusType.OK;
		}

		List<MediaItem> mediaItems = new ArrayList<>();
		mediaItems.add(mediaItem);
		return replacePlaylist(playlistId, mediaItems);
	}

	@Override
	public PlaylistActionStatusType appendPlaylist(long playlistId, MediaItem mediaItem) {
		if (mediaItem == null) {
			return PlaylistActionStatusType.OK;
		}

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		mediaItems.add(mediaItem);
		return appendPlaylist(playlistId, mediaItems);
	}

	@Override
	public boolean canSavePlaylist(long playlistId) {
		Playlist playlist = playlistRepository.getReferenceById(playlistId);
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

	@Override
	public PlaylistType getPlaylistType(String playlistTypeValue) {
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

	@Override
	public PlaylistMediaItem getPlaylistMediaItemByProgress(long playlistId, long progress) {
		Playlist playlist = playlistRepository.getReferenceById(playlistId);
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
		List<PlaylistMediaItem> remainingPlaylistMediaItems = playlistMediaItems.subList(index,
				playlistMediaItems.size() - 1);
		for (PlaylistMediaItem pmi : remainingPlaylistMediaItems) {
			if (pmi.getMediaItem() instanceof Track track) {
				cumulativeEndSeconds += track.getTrackLength();
				if (progress < cumulativeEndSeconds) {
					return pmi;
				}
			}
		}		

		return optionalPlayingMediaItem.orElse(playlistMediaItems.get(0));
	}



}
