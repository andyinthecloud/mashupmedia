package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;

public class AlbumPage {

	private Album album;
	private RemoteMediaMetaItem remoteMediaMetaItem;
	private List<Song> songs;

	public RemoteMediaMetaItem getRemoteMediaMetaItem() {
		return remoteMediaMetaItem;
	}

	public void setRemoteMediaMetaItem(RemoteMediaMetaItem remoteMediaMetaItem) {
		this.remoteMediaMetaItem = remoteMediaMetaItem;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}

}
