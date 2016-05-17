package org.mashupmedia.web.restful;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;

public class RestfulMediaItem {

	private long id;
	private String title;
	private String contextPath;
	private RestfulStream[] streams;

	public RestfulMediaItem(MediaItem mediaItem) {
		this.id = mediaItem.getId();
		this.contextPath = WebHelper.getContextPath();
		this.title = mediaItem.getDisplayTitle();

		prepareStreams(mediaItem);

	}

	private void prepareStreams(MediaItem mediaItem) {
		Set<MediaEncoding> mediaEncodings = mediaItem.getMediaEncodings();
		if (mediaEncodings == null || mediaEncodings.isEmpty()) {
			return;
		}

		List<RestfulStream> restfulStreamList = new ArrayList<RestfulStream>();
		for (MediaEncoding mediaEncoding : mediaEncodings) {
			MediaContentType mediaContentType = mediaEncoding.getMediaContentType();
			String format = mediaContentType.getjPlayerContentType();
			String url = contextPath + "/app/streaming/media/" + mediaItem.getId() + "?mediaContentType=" + format;
			RestfulStream restfulStream = new RestfulStream(format, url);
			restfulStreamList.add(restfulStream);
		}

		streams = new RestfulStream[restfulStreamList.size()];
		streams = restfulStreamList.toArray(streams);

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
