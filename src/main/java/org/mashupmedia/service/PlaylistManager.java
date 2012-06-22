package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.playlist.Playlist;

public interface PlaylistManager {

	public enum PlaylistType {
		MUSIC("music");
		PlaylistType(String idName) {
			this.idName = idName;
		}

		String idName;

		public String getIdName() {
			return idName;
		}

	}

	public List<Playlist> getPlaylists();

	public Playlist getPlaylist(long id);

	public Playlist getLastAccessedMusicPlaylistForCurrentUser();

	public Playlist getDefaultMusicPlaylistForCurrentUser();

	public void savePlaylist(Playlist playlist);

	public void deletePlaylist(long id);

	public List<Playlist> getPlaylistsForCurrentUser();


}
