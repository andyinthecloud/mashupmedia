package org.mashupmedia.model.media;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.mashupmedia.util.MediaItemHelper.MediaContentType;

@Entity
@Cacheable
public class MediaEncoding {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Enumerated(EnumType.STRING)
	private MediaContentType mediaContentType;
	private boolean original;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MediaContentType getMediaContentType() {
		return mediaContentType;
	}

	public void setMediaContentType(MediaContentType mediaContentType) {
		this.mediaContentType = mediaContentType;
	}

	public boolean isOriginal() {
		return original;
	}

	public void setOriginal(boolean original) {
		this.original = original;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mediaContentType == null) ? 0 : mediaContentType.hashCode());
		result = prime * result + (original ? 1231 : 1237);
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
		MediaEncoding other = (MediaEncoding) obj;
		if (mediaContentType != other.mediaContentType)
			return false;
		if (original != other.original)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MediaEncoding [id=");
		builder.append(id);
		builder.append(", mediaContentType=");
		builder.append(mediaContentType);
		builder.append(", original=");
		builder.append(original);
		builder.append("]");
		return builder.toString();
	}

}
