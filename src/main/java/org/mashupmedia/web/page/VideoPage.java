package org.mashupmedia.web.page;

import org.mashupmedia.model.media.Video;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;

public class VideoPage {

	private Video video;
	private String posterUrl;
	private String[] suppliedVideoFormats;
	private RemoteMediaMetaItem remoteMediaMetaItem;

	public RemoteMediaMetaItem getRemoteMediaMetaItem() {
		return remoteMediaMetaItem;
	}

	public void setRemoteMediaMetaItem(RemoteMediaMetaItem remoteMediaMetaItem) {
		this.remoteMediaMetaItem = remoteMediaMetaItem;
	}

	public String[] getSuppliedVideoFormats() {
		return suppliedVideoFormats;
	}

	public void setSuppliedVideoFormats(String[] suppliedVideoFormats) {
		this.suppliedVideoFormats = suppliedVideoFormats;
	}

	public String getSuppliedVideoFormatsValue() {
		if (suppliedVideoFormats == null || suppliedVideoFormats.length == 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (String suppliedVideoFormat : suppliedVideoFormats) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(suppliedVideoFormat);
		}

		return builder.toString();

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
