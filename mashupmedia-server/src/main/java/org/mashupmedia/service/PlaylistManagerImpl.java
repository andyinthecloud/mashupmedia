package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.exception.UnauthorisedException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaylistManagerImpl implements PlaylistManager {

	@Autowired
	private PlaylistDao playlistDao;

	@Autowired
	private MediaDao mediaDao;

	@Autowired
	private SecurityManager securityManager;

	@Autowired
	private AdminManager adminManager;

	@Override
	public List<Playlist> getPlaylists(PlaylistType playlistType) {

		User user = AdminHelper.getLoggedInUser();
		long userId = user.getId();
		boolean isAdministrator = AdminHelper.isAdministrator(user);

		List<Playlist> playlists = playlistDao.getPlaylists(userId, isAdministrator, playlistType);
		return playlists;
	}

	protected synchronized void initialisePlaylist(Playlist playlist) {

		if (playlist == null) {
			return;
		}

		Hibernate.initialize(playlist.getPlaylistMediaItems());

		List<PlaylistMediaItem> accessiblePlaylistMediaItems = new ArrayList<PlaylistMediaItem>();
		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			MediaItem mediaItem = playlistMediaItem.getMediaItem();
			if (!mediaItem.isEnabled()) {
				continue;
			}

			if (securityManager.canAccessPlaylistMediaItem(playlistMediaItem)) {
				accessiblePlaylistMediaItems.add(playlistMediaItem);
			}
		}
		playlist.setAccessiblePlaylistMediaItems(accessiblePlaylistMediaItems);

	}

	@Override
	public Playlist getPlaylist(long id) {
		Playlist playlist = playlistDao.getPlaylist(id);
		if (playlist == null) {
			return playlist;
		}

		initialisePlaylist(playlist);

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		List<PlaylistMediaItem> playlistMediaItemsToDelete = new ArrayList<PlaylistMediaItem>();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			MediaItem mediaItem = playlistMediaItem.getMediaItem();
			mediaItem = mediaDao.getMediaItem(mediaItem.getId());
			if (mediaItem == null) {
				playlistMediaItemsToDelete.add(playlistMediaItem);
			}
		}

		playlistMediaItems.removeAll(playlistMediaItemsToDelete);

		// playlist.setPlaylistMediaItems(playlistMediaItems);

		return playlist;
	}

	@Override
	public Playlist getLastAccessedPlaylistForCurrentUser(PlaylistType playlistType) {
		User user = AdminHelper.getLoggedInUser();
		Playlist playlist = playlistDao.getLastAccessedPlaylist(user.getId(), playlistType);
		if (playlist == null) {
			playlist = getDefaultPlaylistForCurrentUser(playlistType);
		}

		initialisePlaylist(playlist);

		return playlist;
	}

	@Override
	public Playlist getDefaultPlaylistForCurrentUser(PlaylistType playlistType) {
		User user = AdminHelper.getLoggedInUser();
		Playlist playlist = playlistDao.getDefaultPlaylistForUser(user.getId(), playlistType);
		if (playlist != null) {
			initialisePlaylist(playlist);
			return playlist;
		}

		playlist = new Playlist();
		String name = user.getName();
		playlist.setName(name + "'s " + MessageHelper.getMessage("music.playlist.default.name"));
		playlist.setUserDefault(true);
		playlist.setCreatedBy(user);
		playlist.setPlaylistType(PlaylistType.MUSIC);
		playlist.setPlaylistMediaItems(new ArrayList<PlaylistMediaItem>());

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
	}

	@Override
	public List<Playlist> getPlaylistsForCurrentUser(PlaylistType playlistType) {
		User user = AdminHelper.getLoggedInUser();
		if (user == null) {
			return null;
		}

		long userId = user.getId();
		List<Playlist> playlists = playlistDao.getPlaylistsForCurrentUser(userId, playlistType);
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
	public void saveUserPlaylistMediaItem(User user, PlaylistMediaItem playlistMediaItem) {
		if (user == null || playlistMediaItem == null) {
			return;
		}

		if (user.getId() == 0) {
			throw new MashupMediaRuntimeException("Can only update the playlistMediaItem for an existing user.");
		}

		user.setPlaylistMediaItem(playlistMediaItem);
		savePlaylist(playlistMediaItem.getPlaylist());

		adminManager.updateUser(user);

	}

}
