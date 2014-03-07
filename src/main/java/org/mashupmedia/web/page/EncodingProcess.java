package org.mashupmedia.web.page;

import java.util.Date;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.WebHelper.MediaContentType;

public class EncodingProcess {

	private MediaItem mediaItem;
	private MediaContentType mediaContentType;
	private Date startedOn;	

	public Date getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(Date startedOn) {
		this.startedOn = startedOn;
	}

	public MediaItem getMediaItem() {
		return mediaItem;
	}

	public void setMediaItem(MediaItem mediaItem) {
		this.mediaItem = mediaItem;
	}

	public MediaContentType getMediaContentType() {
		return mediaContentType;
	}

	public void setMediaContentType(MediaContentType mediaContentType) {
		this.mediaContentType = mediaContentType;
	}

}
