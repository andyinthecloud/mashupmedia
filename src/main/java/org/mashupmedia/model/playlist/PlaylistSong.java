package org.mashupmedia.model.playlist;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;

@Entity
@Cacheable
public class PlaylistSong extends PlaylistMediaItem {

	@ManyToOne
	private Song song;
	
	@ManyToOne
	private Playlist playlist;

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	@Override
	public MediaItem getMediaItem() {
		return getSong();
	}

}
