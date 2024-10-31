package org.mashupmedia.model.media.music;

import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.DateHelper;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

	public Artist getArtist() {
		return getAlbum().getArtist();
	}

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
