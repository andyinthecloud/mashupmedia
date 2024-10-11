package org.mashupmedia.model.media;

import java.io.Serializable;

import org.mashupmedia.eums.MediaContentType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "media_resources")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResource implements Serializable {
	private static final long serialVersionUID = -3656367571677496182L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_resources_generator")
	@SequenceGenerator(name = "media_resources_generator", sequenceName = "media_resources_seq", allocationSize = 1)
		private long id;
	@Enumerated(EnumType.STRING)
	private MediaContentType mediaContentType;
	private boolean original;
	private String path;
	private long sizeInBytes;
	private long fileLastModifiedOn;
	@ManyToOne
	private MediaItem mediaItem;

	// public long getId() {
	// 	return id;
	// }

	// public void setId(long id) {
	// 	this.id = id;
	// }

	// public MediaContentType getMediaContentType() {
	// 	return mediaContentType;
	// }

	// public void setMediaContentType(MediaContentType mediaContentType) {
	// 	this.mediaContentType = mediaContentType;
	// }

	// public boolean isOriginal() {
	// 	return original;
	// }

	// public void setOriginal(boolean original) {
	// 	this.original = original;
	// }

	// @Override
	// public int hashCode() {
	// 	final int prime = 31;
	// 	int result = 1;
	// 	result = prime * result + ((mediaContentType == null) ? 0 : mediaContentType.hashCode());
	// 	result = prime * result + (original ? 1231 : 1237);
	// 	return result;
	// }

	// @Override
	// public boolean equals(Object obj) {
	// 	if (this == obj)
	// 		return true;
	// 	if (obj == null)
	// 		return false;
	// 	if (getClass() != obj.getClass())
	// 		return false;
	// 	MediaEncoding other = (MediaEncoding) obj;
	// 	if (mediaContentType != other.mediaContentType)
	// 		return false;
	// 	if (original != other.original)
	// 		return false;
	// 	return true;
	// }

	// @Override
	// public String toString() {
	// 	StringBuilder builder = new StringBuilder();
	// 	builder.append("MediaEncoding [id=");
	// 	builder.append(id);
	// 	builder.append(", mediaContentType=");
	// 	builder.append(mediaContentType);
	// 	builder.append(", original=");
	// 	builder.append(original);
	// 	builder.append("]");
	// 	return builder.toString();
	// }

}
