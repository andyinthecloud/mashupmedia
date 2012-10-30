package org.mashupmedia.model.media;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.mashupmedia.util.DateHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.MediaContentType;

@Entity
@Indexed
@Cacheable
public class Song extends MediaItem {

	private static final long serialVersionUID = -8767965461920368852L;

	public final static String TITLE_SEPERATOR = " - ";

	private int trackNumber;
	@Field(index = Index.YES, analyze = Analyze.YES)
	private String title;
	@IndexedEmbedded
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Album album;
	@ManyToOne
	@IndexedEmbedded
	private Genre genre;
	@ManyToOne
	@IndexedEmbedded
	private Year year;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@IndexedEmbedded
	private Artist artist;
	private long trackLength;
	private long bitRate;
	private boolean readableTag;

	public boolean isReadableTag() {
		return readableTag;
	}

	public void setReadableTag(boolean readableTag) {
		this.readableTag = readableTag;
	}

	public long getTrackLength() {
		return trackLength;
	}

	public void setTrackLength(long trackLength) {
		this.trackLength = trackLength;
	}

	public long getBitRate() {
		return bitRate;
	}

	public void setBitRate(long bitRate) {
		this.bitRate = bitRate;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Song other = (Song) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Song [trackNumber=");
		builder.append(trackNumber);
		builder.append(", title=");
		builder.append(title);
		builder.append(", album=");
		builder.append(album);
		builder.append(", genre=");
		builder.append(genre);
		builder.append(", year=");
		builder.append(year);
		builder.append(", artist=");
		builder.append(artist);
		builder.append(", trackLength=");
		builder.append(trackLength);
		builder.append(", bitRate=");
		builder.append(bitRate);
		builder.append(", readableTag=");
		builder.append(readableTag);
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

	public String getMeta() {
		StringBuilder metaBuilder = new StringBuilder();
		if (getBitRate() > 0) {
			metaBuilder.append(getBitRate() + " KBPS");
		}

		String displayTrackLength = getDisplayTrackLength();
		if (StringUtils.isNotBlank(displayTrackLength)) {
			if (metaBuilder.length() > 0) {
				metaBuilder.append(" | ");
			}
			metaBuilder.append(displayTrackLength);

		}

		if (getSizeInBytes() > 0) {
			if (metaBuilder.length() > 0) {
				metaBuilder.append(" | ");
			}
			long sizeInBytes = getSizeInBytes();
			String displayBytes = FileHelper.getDisplayBytes(sizeInBytes, true);
			metaBuilder.append(displayBytes);
		}

		metaBuilder.append(" | ");
		metaBuilder.append(getMediaContentType());

		return metaBuilder.toString();

	}

	public String getMediaContentType() {
		MediaContentType mediaContentType = WebHelper.getMediaContentType(getFormat(), MediaContentType.MP3);
		return mediaContentType.getDisplayText();
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

	public String getDisplayTitle() {
		StringBuilder titleBuilder = new StringBuilder();
		if (isReadableTag()) {
			titleBuilder.append(getDisplayTrackNumber());
			titleBuilder.append(TITLE_SEPERATOR);
			titleBuilder.append(getTitle());
			return titleBuilder.toString();
		}

		String title = StringUtils.trimToEmpty(getTitle());
		int dotIndex = title.lastIndexOf(".");
		if (dotIndex < 0) {
			return title;
		}

		title = title.substring(0, dotIndex);
		return title;
	}

}
