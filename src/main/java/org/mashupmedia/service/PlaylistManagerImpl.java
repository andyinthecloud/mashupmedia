package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaylistManagerImpl implements PlaylistManager {

	@Autowired
	private PlaylistDao playlistDao;

	@Autowired
	private MediaDao mediaDao;

	@Override
	public List<Playlist> getPlaylists() {
		List<Playlist> playlists = playlistDao.getPlaylists();
		return playlists;
	}

	@Override
	public Playlist getPlaylist(long id) {
		Playlist playlist = playlistDao.getPlaylist(id);
		if (playlist == null) {
			return playlist;
		}
		Hibernate.initialize(playlist.getPlaylistMediaItems());

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
	public Playlist getLastAccessedMusicPlaylistForCurrentUser() {
		User user = SecurityHelper.getLoggedInUser();
		Playlist playlist = playlistDao.getLastAccessedMusicPlaylist(user.getId());
		if (playlist == null) {
			playlist = getDefaultMusicPlaylistForCurrentUser();
		}
		
		Hibernate.initialize(playlist.getPlaylistMediaItems());
		
		return playlist;
	}

	@Override
	public Playlist getDefaultMusicPlaylistForCurrentUser() {
		User user = SecurityHelper.getLoggedInUser();
		Playlist playlist = playlistDao.getDefaultMusicPlaylistForUser(user.getId());
		if (playlist != null) {
			Hibernate.initialize(playlist.getPlaylistMediaItems());
			return playlist;
		}

		playlist = new Playlist();
		playlist.setName(MessageHelper.getMessage("music.playlist.default.name"));
		playlist.setUserDefault(true);
		playlist.setCreatedBy(user);
		playlist.setPlaylistType(PlaylistType.MUSIC.getIdName());
		return playlist;
	}

	@Override
	public void savePlaylist(Playlist playlist) {
		User user = SecurityHelper.getLoggedInUser();
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
	public List<Playlist> getPlaylistsForCurrentUser() {
		User user = SecurityHelper.getLoggedInUser();
		long userId = user.getId();
		List<Playlist> playlists = playlistDao.getPlaylists(userId);
		return playlists;
	}

	@Override
	public void deletePlaylist(long id) {
		Playlist playlist = getPlaylist(id);
		playlistDao.deletePlaylist(playlist);
	}

	@Override
	public void deleteLibrary(long libraryId) {
		playlistDao.deleteLibrary(libraryId);

	}

}
