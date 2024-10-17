package org.mashupmedia.service.playlist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mashupmedia.component.TranscodeConfigurationComponent;
import org.mashupmedia.dto.media.playlist.TranscodeStatusType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.repository.playlist.PlaylistRepository;
import org.mashupmedia.service.transcode.TranscodeAudioManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlaylistActionManagerImpl implements PlaylistActionManager {

	private final TranscodeAudioManager transcodeAudioManager;
	private final PlaylistRepository playlistRepository;
	private final TranscodeConfigurationComponent transcodeConfigurationComponent;

	@Override
	public TranscodeStatusType replacePlaylist(long playlistId, List<? extends MediaItem> mediaItems) {
		Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
		if (optionalPlaylist.isEmpty()) {
			log.error("Unable to find playlist with id = " + playlistId);
			return TranscodeStatusType.ERROR;
		}

		Playlist playlist = optionalPlaylist.get();
		Set<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems != null) {
			playlistMediaItems.clear();
		} else {
			playlistMediaItems = new HashSet<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (mediaItems == null || mediaItems.isEmpty()) {
			return TranscodeStatusType.TRANSCODED;
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
			return TranscodeStatusType.TRANSCODED;
		}

		playlist.setPlaylistMediaItems(playlistMediaItems);
		playlistRepository.save(playlist);

		return sendForEncoding(playlist.getAccessiblePlaylistMediaItems(AdminHelper.getLoggedInUser()));
	}

	private TranscodeStatusType sendForEncoding(List<PlaylistMediaItem> playlistMediaItems) {

		List<MediaItem> mediaItemsForEncoding = playlistMediaItems
				.stream()
				.map(pmi -> pmi.getMediaItem())
				.filter(mi -> !mi.isTranscoded(
						transcodeConfigurationComponent.getTranscodeAudioMediaContentType()))
				.collect(Collectors.toList());

		if (mediaItemsForEncoding == null || mediaItemsForEncoding.isEmpty()) {
			return TranscodeStatusType.TRANSCODED;
		}

		// if (!transcodeAudioManager.isTranscoderInstalled()) {
		// return EncoderStatusType.TRANSCODER_NOT_INSTALLED;
		// }

		for (MediaItem mediaItem : mediaItemsForEncoding) {
			// try {

			if (mediaItem instanceof Track track) {

				transcodeAudioManager.processTrack(track, track.getOriginalMediaResource().getPath());
			}

			// encodeMediaItemManager.processMediaItemForEncoding(mediaItem,
			// MediaContentHelper.getDefaultMediaContentType(mediaItem));
			// } catch (MediaItemTranscodeException e) {
			// log.error("Error encoding media", e);
			// }
		}

		return TranscodeStatusType.TRANSCODING;

	}

	@Override
	public TranscodeStatusType appendPlaylist(long playlistId, List<? extends MediaItem> mediaItems) {
		Playlist playlist = playlistRepository.getReferenceById(playlistId);

		Set<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null) {
			playlistMediaItems = new HashSet<PlaylistMediaItem>();
			playlist.setPlaylistMediaItems(playlistMediaItems);
		}

		if (mediaItems == null || mediaItems.isEmpty()) {
			return TranscodeStatusType.TRANSCODED;
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

		return sendForEncoding(playlist.getAccessiblePlaylistMediaItems(AdminHelper.getLoggedInUser()));
	}

	@Override
	public TranscodeStatusType replacePlaylist(long playlistId, MediaItem mediaItem) {
		if (mediaItem == null) {
			return TranscodeStatusType.TRANSCODED;
		}

		List<MediaItem> mediaItems = new ArrayList<>();
		mediaItems.add(mediaItem);
		return replacePlaylist(playlistId, mediaItems);
	}

	@Override
	public TranscodeStatusType appendPlaylist(long playlistId, MediaItem mediaItem) {
		if (mediaItem == null) {
			return TranscodeStatusType.TRANSCODED;
		}

		List<MediaItem> mediaItems = new ArrayList<MediaItem>();
		mediaItems.add(mediaItem);
		return appendPlaylist(playlistId, mediaItems);
	}

	@Override
	public boolean canSavePlaylist(long playlistId) {
		Playlist playlist = playlistRepository.getReferenceById(playlistId);
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

}
