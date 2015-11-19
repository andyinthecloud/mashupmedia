package org.mashupmedia.web.restful;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.WebHelper;

public class RestfulMediaItem {

	private String title;
	private String streamingUrl;

	public RestfulMediaItem(MediaItem mediaItem) {
		this.title = mediaItem.getDisplayTitle();
		String contextUrl = WebHelper.getContextPath();
		this.streamingUrl = contextUrl + "/streaming/media/" + mediaItem.getId();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStreamingUrl() {
		return streamingUrl;
	}

	public void setStreamingUrl(String streamingUrl) {
		this.streamingUrl = streamingUrl;
	}

}
