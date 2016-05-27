package org.mashupmedia.web.restful;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.WebHelper;

public abstract class RestfulMediaItem {

	private long id;
	private String title;
	protected String contextPath;
	private RestfulStream[] streams;
	private MediaContentType[] suppliedMediaContentTypes;

	public RestfulMediaItem(MediaItem mediaItem, MediaContentType[] suppliedMediaContentTypes) {
		this.id = mediaItem.getId();
		this.contextPath = WebHelper.getContextPath();
		this.title = mediaItem.getDisplayTitle();
		this.suppliedMediaContentTypes = suppliedMediaContentTypes;
		prepareStreams(mediaItem);
	}
	
	
	protected void prepareStreams(MediaItem mediaItem) {
		Set<MediaEncoding> mediaEncodings = mediaItem.getMediaEncodings();
		if (mediaEncodings == null || mediaEncodings.isEmpty()) {
			return;
		}

		long mediaItemId = mediaItem.getId();
		List<RestfulStream> restfulStreamList = new ArrayList<RestfulStream>();
		for (MediaEncoding mediaEncoding : mediaEncodings) {
			MediaContentType mediaContentType = mediaEncoding.getMediaContentType();
			String format = mediaContentType.getjPlayerContentType();
			String url = MediaItemHelper.prepareUrlStream(contextPath, mediaItemId, format);
			RestfulStream restfulStream = new RestfulStream(format, url);
			restfulStreamList.add(restfulStream);
		}
		
		MediaItemHelper.addSuppliedStreamUrls(suppliedMediaContentTypes, contextPath, mediaItemId, restfulStreamList);
		
		RestfulStream[] streams = new RestfulStream[restfulStreamList.size()];
		streams = restfulStreamList.toArray(streams);
		setStreams(streams);
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
