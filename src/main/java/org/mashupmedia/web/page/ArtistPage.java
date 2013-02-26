package org.mashupmedia.web.page;

import org.mashupmedia.model.media.Artist;
import org.mashupmedia.web.remote.RemoteMediaMeta;

public class ArtistPage {
	private Artist artist;
	private RemoteMediaMeta remoteMediaMeta;

	public RemoteMediaMeta getRemoteMediaMeta() {
		return remoteMediaMeta;
	}

	public void setRemoteMediaMeta(RemoteMediaMeta remoteMediaMeta) {
		this.remoteMediaMeta = remoteMediaMeta;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

}
