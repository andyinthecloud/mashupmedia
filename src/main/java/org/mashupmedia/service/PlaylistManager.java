package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;

public interface PlaylistManager {

	public List<Playlist> getPlaylists();

	public Playlist getPlaylist(long id);

	public Playlist getLastAccessedPlaylistForCurrentUser(PlaylistType playlistType);

	public Playlist getDefaultPlaylistForCurrentUser(PlaylistType playlistType);

	public void savePlaylist(Playlist playlist);

	public void deletePlaylist(long id);

	public List<Playlist> getPlaylistsForCurrentUser(PlaylistType playlistType);

	public void deleteLibrary(long libraryId);

}
