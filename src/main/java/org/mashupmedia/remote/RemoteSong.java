/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.remote;

import java.io.Serializable;

public class RemoteSong extends RemoteMediaItem implements Serializable{

	private static final long serialVersionUID = 1094752010515988971L;
	private int trackNumber;
	private String title;
	private String album;
	private String genre;
	private int year;
	private String artist;
	private long trackLength;
	private long bitRate;
	private boolean readableTag;
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
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
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
	public boolean isReadableTag() {
		return readableTag;
	}
	public void setReadableTag(boolean readableTag) {
		this.readableTag = readableTag;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemoteSong [trackNumber=");
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
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}	
	

}
