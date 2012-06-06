package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.Playlist;

public interface PlaylistManager {

	public List<Playlist> getPlaylists();

	public Playlist getPlaylist(long id);

	public Playlist getDefaultPlaylistForCurrentUser();
	
	public void savePlaylist(Playlist playlist);
	
	public void deletePlaylist(long id);
	
	public List<Playlist> getPlaylistsForCurrentUser();

}
