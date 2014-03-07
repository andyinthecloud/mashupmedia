package org.mashupmedia.encode;

import org.mashupmedia.util.WebHelper.MediaContentType;

public class ProcessKey {
	private long mediaItemId;
	private MediaContentType mediaContentType;

	public ProcessKey(long mediaItemId, MediaContentType mediaContentType) {
		this.mediaItemId = mediaItemId;
		this.mediaContentType = mediaContentType;
	}

	public long getMediaItemId() {
		return mediaItemId;
	}

	public void setMediaItemId(long mediaItemId) {
		this.mediaItemId = mediaItemId;
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
		result = prime * result + ((mediaContentType == null) ? 0 : mediaContentType.hashCode());
		result = prime * result + (int) (mediaItemId ^ (mediaItemId >>> 32));
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
		ProcessKey other = (ProcessKey) obj;
		if (mediaContentType != other.mediaContentType)
			return false;
		if (mediaItemId != other.mediaItemId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessKey [mediaId=");
		builder.append(mediaItemId);
		builder.append(", mediaContentType=");
		builder.append(mediaContentType);
		builder.append("]");
		return builder.toString();
	}

}
