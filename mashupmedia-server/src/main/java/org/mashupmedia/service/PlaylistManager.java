package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

public interface PlaylistManager {

	List<Playlist> getPlaylists(MashupMediaType mashupMediaType);

	Playlist getPlaylist(long id);

	Playlist getLastAccessedPlaylistForCurrentUser(MashupMediaType mashupMediaType);

	Playlist getDefaultPlaylistForCurrentUser(MashupMediaType mashupMediaType);

	void savePlaylist(Playlist playlist);

	void deletePlaylist(long id);

	List<Playlist> getPlaylistsForCurrentUser(MashupMediaType mashupMediaType);

	void deleteLibrary(long libraryId);

	PlaylistMediaItem playRelativePlaylistMediaItem(Playlist playlist, int relativeOffset);

	PlaylistMediaItem playPlaylistMediaItem(Playlist playlist, Long playlistMediaItemId);


}
