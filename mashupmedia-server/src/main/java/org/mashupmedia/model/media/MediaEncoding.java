package org.mashupmedia.model.media;

import java.io.Serializable;

import jakarta.persistence.*;

import org.mashupmedia.util.MediaItemHelper.MediaContentType;

@Entity
@Table(name = "media_encodings")
@Cacheable
public class MediaEncoding implements Serializable {
	private static final long serialVersionUID = -3656367571677496182L;

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
