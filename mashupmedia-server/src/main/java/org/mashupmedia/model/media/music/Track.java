package org.mashupmedia.model.media.music;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.DateHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaContentHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tracks")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Track extends MediaItem {


	@Override
	public MashupMediaType getMashupMediaType() {
		return MashupMediaType.MUSIC;
	}

	private int trackNumber;
	@Column(length = 1000)
	private String title;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Album album;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Genre genre;
	private int trackYear;
	private long trackLength;
	private long bitRate;
	// private boolean readableTag;

	public Artist getArtist() {
		return getAlbum().getArtist();
	}

	// @Override
	// public boolean isEncodedForWeb() {
	// 	Collection<MediaResource> mediaEncodings = getMediaResouces();
	// 	for (MediaResource mediaEncoding : mediaEncodings) {
	// 		if (MediaItemHelper.isWebCompatibleEncoding(
	// 				getMashupMediaType(),
	// 				mediaEncoding.getMediaContentType())) {
	// 			return true;
	// 		}
	// 	}
	// 	return false;
	// }

	// @Override
	// public int hashCode() {
	// 	final int prime = 31;
	// 	int result = super.hashCode();
	// 	result = prime * result + ((album == null) ? 0 : album.hashCode());
	// 	result = prime * result + ((title == null) ? 0 : title.hashCode());
	// 	return result;
	// }

	// @Override
	// public boolean equals(Object obj) {
	// 	if (this == obj)
	// 		return true;
	// 	if (!super.equals(obj))
	// 		return false;
	// 	if (getClass() != obj.getClass())
	// 		return false;
	// 	Track other = (Track) obj;
	// 	if (album == null) {
	// 		if (other.album != null)
	// 			return false;
	// 	} else if (!album.equals(other.album))
	// 		return false;
	// 	if (title == null) {
	// 		if (other.title != null)
	// 			return false;
	// 	} else if (!title.equals(other.title))
	// 		return false;
	// 	return true;
	// }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Track [number=");
		builder.append(trackNumber);
		builder.append(", title=");
		builder.append(title);
		builder.append(", album=");
		builder.append(album.getName());
		builder.append(", genre=");
		builder.append(genre);
		builder.append(", year=");
		builder.append(trackYear);
		builder.append(", artist=");
		builder.append(getArtist());
		builder.append(", length=");
		builder.append(trackLength);
		builder.append(", bitRate=");
		builder.append(bitRate);
		// builder.append(", readableTag=");
		// builder.append(readableTag);
		builder.append("]");
		return builder.toString();
	}

	public String getDisplayTrackLength() {
		if (getTrackLength() == 0) {
			return "";
		}

		String trackLengthDisplay = DateHelper.getDisplayTrackLength(getTrackLength());
		return trackLengthDisplay;

	}

	// public String getMeta() {
	// StringBuilder metaBuilder = new StringBuilder();
	// if (getBitRate() > 0) {
	// metaBuilder.append(getBitRate() + " KBPS");
	// }

	// String displayTrackLength = getDisplayTrackLength();
	// if (StringUtils.isNotBlank(displayTrackLength)) {
	// if (metaBuilder.length() > 0) {
	// metaBuilder.append(" | ");
	// }
	// metaBuilder.append(displayTrackLength);

	// }

	// if (getSizeInBytes() > 0) {
	// if (metaBuilder.length() > 0) {
	// metaBuilder.append(" | ");
	// }
	// long sizeInBytes = getSizeInBytes();
	// String displayBytes = FileHelper.getDisplayBytes(sizeInBytes, true);
	// metaBuilder.append(displayBytes);
	// }

	// metaBuilder.append(" | ");

	// MediaContentType mediaContentType = null;
	// // MediaEncoding mediaEncoding = getBestMediaEncoding();
	// if (mediaEncoding != null) {
	// mediaContentType = mediaEncoding.getMediaContentType();
	// } else {
	// String format = getFormat();
	// mediaContentType = MediaContentHelper.getMediaContentType(format);
	// }

	// metaBuilder.append(mediaContentType.name());
	// return metaBuilder.toString();

	// }

	public String getDisplayTrackNumber() {

		if (getTrackNumber() == 0) {
			return "";
		}

		StringBuilder trackNumberBuilder = new StringBuilder();
		if (getTrackNumber() < 10) {
			trackNumberBuilder.append("0");
		}
		trackNumberBuilder.append(getTrackNumber());
		return trackNumberBuilder.toString();
	}

	@Override
	public String getSummary() {
		StringBuilder summaryBuilder = new StringBuilder();
		summaryBuilder.append(getArtist().getName());
		summaryBuilder.append(TITLE_SEPERATOR);
		summaryBuilder.append(getAlbum().getName());
		summaryBuilder.append(TITLE_SEPERATOR);
		summaryBuilder.append(getTrackNumber());
		summaryBuilder.append(" ");
		summaryBuilder.append(getTitle());
		return summaryBuilder.toString();
	}

}
