package org.mashupmedia.web.page;

import org.mashupmedia.model.media.Video;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;

public class VideoPage {

	private Video video;
	private String posterUrl;
	private RemoteMediaMetaItem remoteMediaMetaItem;

	public RemoteMediaMetaItem getRemoteMediaMetaItem() {
		return remoteMediaMetaItem;
	}

	public void setRemoteMediaMetaItem(RemoteMediaMetaItem remoteMediaMetaItem) {
		this.remoteMediaMetaItem = remoteMediaMetaItem;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

}
