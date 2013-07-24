package org.mashupmedia.model.media;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.mashupmedia.util.DateHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.MediaContentType;

@Entity
@Indexed
@Cacheable
@XmlRootElement
public class Song extends MediaItem {

	private static final long serialVersionUID = -8767965461920368852L;


	private int trackNumber;
	@Field(analyze = Analyze.NO)
	@Column(length = 1000)
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

	public Song() {
		setMediaType(MediaType.SONG);
	}
	
	public Song(Song song) {
		
	}
	

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
		builder.append(album.getName());
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
		metaBuilder.append(getMediaContentType().getDisplayText());

		return metaBuilder.toString();

	}

	
	public MediaContentType getMediaContentType() {
		MediaContentType mediaContentType = WebHelper.getMediaContentType(getFormat(), MediaContentType.MP3);
		return mediaContentType;
	}
	
	public String getJPlayerContentType() {
		MediaContentType mediaContentType = getMediaContentType();
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

	@Override
	public String getSummary() {
		StringBuilder summaryBuilder = new StringBuilder();
		summaryBuilder.append(getArtist().getName());
		summaryBuilder.append(TITLE_SEPERATOR);
		summaryBuilder.append(getAlbum().getName());
		summaryBuilder.append(TITLE_SEPERATOR);
		summaryBuilder.append(getDisplayTitle());
		return summaryBuilder.toString();
	}

}
