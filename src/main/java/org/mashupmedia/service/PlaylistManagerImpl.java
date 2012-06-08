package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.media.Playlist;
import org.mashupmedia.util.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaylistManagerImpl implements PlaylistManager {

	@Autowired
	private PlaylistDao playlistDao;
	
	@Override
	public List<Playlist> getPlaylists() {
		List<Playlist> playlists = playlistDao.getPlaylists();
		return playlists;
	}

	@Override
	public Playlist getPlaylist(long id) {
		Playlist playlist = playlistDao.getPlaylist(id);
		return playlist;
	}

	@Override
	public Playlist getLastAccessedPlaylistForCurrentUser() {
		User user = SecurityHelper.getLoggedInUser();
		Playlist playlist = playlistDao.getLastAccessedPlaylist(user.getId());
		return playlist;
	}

	@Override
	public Playlist getDefaultPlaylistForCurrentUser() {
		User user = SecurityHelper.getLoggedInUser();
		Playlist playlist = playlistDao.getDefaultPlaylist(user.getId());
		return playlist;
	}
	
	
	@Override
	public void savePlaylist(Playlist playlist) {
		User user = SecurityHelper.getLoggedInUser();
		long playlistId = playlist.getId();
		Date date = new Date();
		if (playlistId == 0) {
			playlist.setCreatedOn(date);
			playlist.setOwner(user);
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

}
