package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Track;

public class AlbumPage {

	private Album album;
	private List<Track> tracks;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

}
