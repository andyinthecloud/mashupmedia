package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

public interface PlaylistManager {

	List<Playlist> getPlaylists(PlaylistType playlistType);

	Playlist getPlaylist(long id);

	Playlist getLastAccessedPlaylistForCurrentUser(PlaylistType playlistType);

	Playlist getDefaultPlaylistForCurrentUser(PlaylistType playlistType);

	void savePlaylist(Playlist playlist);

	void deletePlaylist(long id);

	List<Playlist> getPlaylistsForCurrentUser(PlaylistType playlistType);

	void deleteLibrary(long libraryId);

	PlaylistMediaItem playRelativePlaylistMediaItem(Playlist playlist, int relativeOffset);

	PlaylistMediaItem playPlaylistMediaItem(Playlist playlist, Long playlistMediaItemId);


}
