package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

public interface PlaylistDao {

	public List<Playlist> getPlaylists(long userId, boolean isAdministrator, MashupMediaType mashupMediaType);

	public Playlist getPlaylist(long id);

	public Playlist getLastAccessedPlaylist(long userId, MashupMediaType mashupMediaType);

	public void savePlaylist(Playlist playlist);

	public List<Playlist> getPlaylistsForCurrentUser(long userId, MashupMediaType mashupMediaType);

	public Playlist getDefaultPlaylistForUser(long userId, MashupMediaType mashupMediaType);

	public void deletePlaylistMediaItem(MediaItem mediaItem);

	public void deleteLibrary(long libraryId);

	public PlaylistMediaItem getPlaylistMediaItem(long playlistMediaItemId);

}
