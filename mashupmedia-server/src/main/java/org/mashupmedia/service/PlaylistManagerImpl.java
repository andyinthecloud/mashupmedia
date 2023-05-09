package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.exception.UnauthorisedException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.model.playlist.UserPlaylistPosition;
import org.mashupmedia.model.playlist.UserPlaylistPositionId;
import org.mashupmedia.repository.playlist.UserPlaylistPositionRepository;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.MessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class PlaylistManagerImpl implements PlaylistManager {

	private final PlaylistDao playlistDao;
	private final MediaDao mediaDao;
	private final MashupMediaSecurityManager securityManager;
	private final UserPlaylistPositionRepository userPlaylistPositionRepository;

	@Override
	public List<Playlist> getPlaylists(MashupMediaType mashupMediaType) {

		User user = AdminHelper.getLoggedInUser();
		long userId = user.getId();
		boolean isAdministrator = AdminHelper.isAdministrator(user);

		List<Playlist> playlists = playlistDao.getPlaylists(userId, isAdministrator, mashupMediaType);
		return playlists;
	}

	protected void initialisePlaylist(Playlist playlist) {

		if (playlist == null) {
			return;
		}

		Hibernate.initialize(playlist.getPlaylistMediaItems());

		List<PlaylistMediaItem> accessiblePlaylistMediaItems = playlist.getPlaylistMediaItems()
				.stream()
				.filter(pmi -> pmi.getMediaItem().isEnabled())
				.filter(pmi -> securityManager.canAccessMediaItem(pmi.getMediaItem()))
				.sorted((pmi1, pmi2) -> pmi1.getRanking().compareTo(pmi2.getRanking()))
				.collect(Collectors.toList());

		playlist.setAccessiblePlaylistMediaItems(accessiblePlaylistMediaItems);

		if (accessiblePlaylistMediaItems.isEmpty()) {
			return;
		}

		accessiblePlaylistMediaItems.get(0).setFirst(true);
		accessiblePlaylistMediaItems.get(accessiblePlaylistMediaItems.size() - 1).setLast(true);

		User user = AdminHelper.getLoggedInUser();
		Optional<UserPlaylistPosition> userPlaylistPosition = playlist.getUserPlaylistPositions()
				.stream()
				.filter(upp -> upp.getUser().equals(user))
				.findAny();

		if (userPlaylistPosition.isEmpty()) {
			return;
		}

		long userPlaylistMediaId = userPlaylistPosition.get().getPlaylistMediaId();

		accessiblePlaylistMediaItems.forEach(pmi -> {
			pmi.setPlaying(pmi.getId() == userPlaylistMediaId ? true : false);
		});

	}

	@Override
	public Playlist getPlaylist(long id) {
		Playlist playlist = playlistDao.getPlaylist(id);
		if (playlist == null) {
			return playlist;
		}

		initialisePlaylist(playlist);

		Collection<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		Collection<PlaylistMediaItem> playlistMediaItemsToDelete = new ArrayList<PlaylistMediaItem>();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			MediaItem mediaItem = playlistMediaItem.getMediaItem();
			mediaItem = mediaDao.getMediaItem(mediaItem.getId());
			if (mediaItem == null) {
				playlistMediaItemsToDelete.add(playlistMediaItem);
			}
		}

		playlistMediaItems.removeAll(playlistMediaItemsToDelete);
		return playlist;
	}

	@Override
	public Playlist getLastAccessedPlaylistForCurrentUser(MashupMediaType mashupMediaType) {
		User user = AdminHelper.getLoggedInUser();
		Playlist playlist = playlistDao.getLastAccessedPlaylist(user.getId(), mashupMediaType);
		if (playlist == null) {
			playlist = getDefaultPlaylistForCurrentUser(mashupMediaType);
		}

		initialisePlaylist(playlist);

		return playlist;
	}

	@Override
	public Playlist getDefaultPlaylistForCurrentUser(MashupMediaType mashupMediaType) {
		User user = AdminHelper.getLoggedInUser();
		Assert.notNull(user, "User should not be null");

		Playlist playlist = playlistDao.getDefaultPlaylistForUser(user.getId(), mashupMediaType);

		if (playlist != null) {
			initialisePlaylist(playlist);
			return playlist;
		}

		playlist = new Playlist();
		String name = user.getName();
		playlist.setName(name + "'s " + MessageHelper.getMessage("music.playlist.default.name"));
		playlist.setUserDefault(true);
		playlist.setCreatedBy(user);
		playlist.setMashupMediaType(mashupMediaType);
		playlist.setPlaylistMediaItems(new HashSet<PlaylistMediaItem>());
		playlistDao.savePlaylist(playlist);

		return playlist;
	}

	@Override
	public void savePlaylist(Playlist playlist) {
		User createdbyUser = playlist.getCreatedBy();
		User user = AdminHelper.getLoggedInUser();

		if (createdbyUser != null && !user.equals(createdbyUser)) {
			throw new UnauthorisedException("Unable to save user playlist");
		}

		long playlistId = playlist.getId();
		Date date = new Date();
		if (playlistId == 0) {
			playlist.setCreatedOn(date);
			playlist.setCreatedBy(user);
		} 

		playlist.setUpdatedBy(user);
		playlist.setUpdatedOn(date);
		playlistDao.savePlaylist(playlist);

		saveUserPlaylistPosition(playlist);
	}

	private void saveUserPlaylistPosition(Playlist playlist) {
		List<PlaylistMediaItem> accessiblePlaylistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (accessiblePlaylistMediaItems == null || accessiblePlaylistMediaItems.isEmpty()) {
			return;
		}

		Optional<PlaylistMediaItem> playingPlaylistMediaItem = accessiblePlaylistMediaItems
				.stream()
				.filter(pmi -> pmi.isPlaying())
				.findFirst();

		if (playingPlaylistMediaItem.isEmpty()) {
			return;
		}

		User user = AdminHelper.getLoggedInUser();

		UserPlaylistPosition userPlaylistPosition = UserPlaylistPosition
				.builder()
				.playlistId(playlist.getId())
				.userId(user.getId())
				.playlistMediaId(playingPlaylistMediaItem.get().getId())
				.build();

		userPlaylistPositionRepository.save(userPlaylistPosition);
	}

	@Override
	public List<Playlist> getPlaylistsForCurrentUser(MashupMediaType mashupMediaType) {
		User user = AdminHelper.getLoggedInUser();
		if (user == null) {
			return null;
		}

		long userId = user.getId();
		List<Playlist> playlists = playlistDao.getPlaylistsForCurrentUser(userId, mashupMediaType);
		return playlists;
	}

	@Override
	public void deletePlaylist(long id) {
		Playlist playlist = getPlaylist(id);

		User createdbyUser = playlist.getCreatedBy();
		User user = AdminHelper.getLoggedInUser();

		if (!user.equals(createdbyUser)) {
			throw new UnauthorisedException("Unable to delete user playlist");
		}

		playlistDao.deletePlaylist(playlist);
	}

	@Override
	public void deleteLibrary(long libraryId) {
		playlistDao.deleteLibrary(libraryId);
	}

	@Override
	public PlaylistMediaItem playRelativePlaylistMediaItem(Playlist playlist, int relativeOffset) {

		List<PlaylistMediaItem> playlistMediaItems = playlist.getAccessiblePlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return null;
		}

		User user = AdminHelper.getLoggedInUser();

		UserPlaylistPositionId userPlaylistPositionId = new UserPlaylistPositionId(user.getId(), playlist.getId());
		Optional<UserPlaylistPosition> optionalUserPlaylistPosition = userPlaylistPositionRepository
				.findById(userPlaylistPositionId);

		long playlistMediaId = optionalUserPlaylistPosition.isPresent()
				? optionalUserPlaylistPosition.get().getPlaylistMediaId()
				: playlistMediaItems.get(0).getId();

		int playingIndex = getPlayingIndex(playlistMediaItems, playlistMediaId, relativeOffset);

		PlaylistMediaItem playlistMediaItem = playlistMediaItems.get(playingIndex);
		playlist.getPlaylistMediaItems().forEach(pmi -> pmi.setPlaying(pmi.equals(playlistMediaItem)));
		return playlistMediaItem;
	}

	public PlaylistMediaItem playPlaylistMediaItem(Playlist playlist, Long playlistMediaItemId) {
		if (playlistMediaItemId == null || playlistMediaItemId == 0) {
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

		PlaylistMediaItem playlistMediaItem = null;
		for (PlaylistMediaItem pmi : playlistMediaItems) {
			if (pmi.getId() == playlistMediaItemId) {
				pmi.setPlaying(true);
				playlistMediaItem = pmi;
			} else {
				pmi.setPlaying(false);
			}
		}

		return playlistMediaItem;
	}

	private int getPlayingIndex(List<PlaylistMediaItem> playlistMediaItems, long playlistMediaId, int relativeOffset) {

		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return 0;
		}

		Optional<PlaylistMediaItem> playlistMediaItem = playlistMediaItems
				.stream()
				.filter(pmi -> pmi.getId() == playlistMediaId)
				.findAny();

		if (playlistMediaItem.isEmpty()) {
			return 0;
		}

		int index = playlistMediaItems.indexOf(playlistMediaItem.get());

		index += relativeOffset;

		if (index < 0) {
			return 0;
		}

		if (index >= playlistMediaItems.size()) {
			return playlistMediaItems.size() - 1;
		}

		return index;
	}
}
