package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.playlist.MusicPlaylist;
import org.mashupmedia.model.playlist.Playlist;

public interface PlaylistManager {

	public List<Playlist> getPlaylists();

	public Playlist getPlaylist(long id);

	public MusicPlaylist getLastAccessedMusicPlaylistForCurrentUser();
	
	public MusicPlaylist getDefaultMusicPlaylistForCurrentUser();
	
	public void savePlaylist(Playlist playlist);
	
	public void deletePlaylist(long id);
	
	public List<Playlist> getPlaylistsForCurrentUser();

}
