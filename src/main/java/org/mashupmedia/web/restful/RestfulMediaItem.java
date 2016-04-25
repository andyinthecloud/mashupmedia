package org.mashupmedia.web.restful;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.WebHelper;

public class RestfulMediaItem {

	private long id;
	private String title;
	private String streamUrl;
	private String streamFormat;
	private String contextPath;

	public RestfulMediaItem(MediaItem mediaItem) {
		this.id = mediaItem.getId();
		this.contextPath = WebHelper.getContextPath();
		this.title = mediaItem.getDisplayTitle();
		this.streamUrl = contextPath + "/app/streaming/media/" + mediaItem.getId();
		this.streamFormat = mediaItem.getBestMediaEncoding().getMediaContentType().getjPlayerContentType();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

	public String getStreamFormat() {
		return streamFormat;
	}

	public void setStreamFormat(String streamFormat) {
		this.streamFormat = streamFormat;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestfulMediaItem [title=");
		builder.append(title);
		builder.append(", streamUrl=");
		builder.append(streamUrl);
		builder.append(", streamFormat=");
		builder.append(streamFormat);
		builder.append("]");
		return builder.toString();
	}

}
