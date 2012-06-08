package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.Playlist;

public interface PlaylistDao {

	public List<Playlist> getPlaylists();

	public Playlist getPlaylist(long id);

	public Playlist getLastAccessedPlaylist(long userId);

	public void savePlaylist(Playlist playlist);

	public List<Playlist> getPlaylists(long userId);

	public void deletePlaylist(Playlist playlist);

	public Playlist getDefaultPlaylist(long userId);

}
