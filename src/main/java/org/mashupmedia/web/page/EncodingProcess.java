package org.mashupmedia.web.page;

import java.util.Date;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;

public class EncodingProcess {

	private MediaItem mediaItem;
	private MediaContentType mediaContentType;
	private Date processStartedOn;
	private Date createdOn;

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getProcessStartedOn() {
		return processStartedOn;
	}

	public void setProcessStartedOn(Date processStartedOn) {
		this.processStartedOn = processStartedOn;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((mediaContentType == null) ? 0 : mediaContentType.hashCode());
		result = prime * result + ((mediaItem == null) ? 0 : mediaItem.hashCode());
		result = prime * result + ((processStartedOn == null) ? 0 : processStartedOn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EncodingProcess other = (EncodingProcess) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (mediaContentType != other.mediaContentType)
			return false;
		if (mediaItem == null) {
			if (other.mediaItem != null)
				return false;
		} else if (!mediaItem.equals(other.mediaItem))
			return false;
		if (processStartedOn == null) {
			if (other.processStartedOn != null)
				return false;
		} else if (!processStartedOn.equals(other.processStartedOn))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EncodingProcess [mediaItem=");
		builder.append(mediaItem);
		builder.append(", mediaContentType=");
		builder.append(mediaContentType);
		builder.append(", startedOn=");
		builder.append(processStartedOn);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
