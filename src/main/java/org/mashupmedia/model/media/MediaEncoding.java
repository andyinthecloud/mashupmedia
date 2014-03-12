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
	private int ranking;

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

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((mediaContentType == null) ? 0 : mediaContentType.hashCode());
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
		if (id != other.id)
			return false;
		if (mediaContentType != other.mediaContentType)
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
		builder.append(", ranking=");
		builder.append(ranking);
		builder.append("]");
		return builder.toString();
	}

}
