package org.mashupmedia.model.media;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.util.DateHelper;
import org.mashupmedia.util.FileHelper;

@Entity
@Cacheable
public class Song extends MediaItem {
	
	public final static String TITLE_SEPERATOR = " - "; 
	
	private int trackNumber;
	private String title;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Album album;
	@ManyToOne
	private Genre genre;
	@ManyToOne
	private Year year;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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

	public String getMeta() {
		StringBuilder metaBuilder = new StringBuilder();
		if (getBitRate() > 0) {
			metaBuilder.append(getBitRate() + " KBPS");
		}

		if (getTrackLength() > 0) {
			if (metaBuilder.length() > 0) {
				metaBuilder.append(" | ");
			}

			String trackLengthDisplay = DateHelper.getDisplayTrackLength(getTrackLength());
			metaBuilder.append(trackLengthDisplay);
		}

		if (getSizeInBytes() > 0) {
			if (metaBuilder.length() > 0) {
				metaBuilder.append(" | ");
			}
			long sizeInBytes = getSizeInBytes();
			String displayBytes = FileHelper.getDisplayBytes(sizeInBytes, true);
			metaBuilder.append(displayBytes);
		}

		if (StringUtils.isNotBlank(getFormat())) {
			if (metaBuilder.length() > 0) {
				metaBuilder.append(" | ");
			}
			metaBuilder.append(getFormat());
		}

		return metaBuilder.toString();

	}
	
	public String getDisplayTitle() {
		StringBuilder titleBuilder = new StringBuilder();
		if (isReadableTag()) {
			titleBuilder.append(getTrackNumber());
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
