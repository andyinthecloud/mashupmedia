package org.mashupmedia.web.restful;

import java.util.Arrays;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.WebHelper;

public abstract class RestfulMediaItem {

	private long id;
	private String title;
	protected String contextPath;
	private RestfulStream[] streams;

	public RestfulMediaItem(MediaItem mediaItem) {
		this.id = mediaItem.getId();
		this.contextPath = WebHelper.getContextPath();
		this.title = mediaItem.getDisplayTitle();

		prepareStreams(mediaItem);

	}

	protected abstract void prepareStreams(MediaItem mediaItem);
	
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


	public RestfulStream[] getStreams() {
		return streams;
	}

	public void setStreams(RestfulStream[] streams) {
		this.streams = streams;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestfulMediaItem [id=");
		builder.append(id);
		builder.append(", title=");
		builder.append(title);
		builder.append(", contextPath=");
		builder.append(contextPath);
		builder.append(", streams=");
		builder.append(Arrays.toString(streams));
		builder.append("]");
		return builder.toString();
	}

}
