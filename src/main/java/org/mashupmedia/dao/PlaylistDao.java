package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;

public interface PlaylistDao {

	public List<Playlist> getPlaylists();

	public Playlist getPlaylist(long id);

	public Playlist getLastAccessedPlaylist(long userId, PlaylistType playlistType);

	public void savePlaylist(Playlist playlist);

	public List<Playlist> getPlaylists(long userId, PlaylistType playlistType);

	public void deletePlaylist(Playlist playlist);

	public Playlist getDefaultPlaylistForUser(long userId, PlaylistType playlistType);

	public void deletePlaylistMediaItems(List<? extends MediaItem> mediaItems);

	public void deleteLibrary(long libraryId);
	

}
