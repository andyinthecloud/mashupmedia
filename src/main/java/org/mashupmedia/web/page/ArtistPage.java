package org.mashupmedia.web.page;

import org.mashupmedia.model.media.Artist;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;

public class ArtistPage {
	private Artist artist;
	private RemoteMediaMetaItem remoteMediaMeta;

	public RemoteMediaMetaItem getRemoteMediaMeta() {
		return remoteMediaMeta;
	}

	public void setRemoteMediaMeta(RemoteMediaMetaItem remoteMediaMeta) {
		this.remoteMediaMeta = remoteMediaMeta;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

}
