package org.mashupmedia.web.restful;

import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.ImageHelper.ImageType;

public class RestfulSong extends RestfulMediaItem {

	private String artistName;
	private String artistUrl;
	private String albumName;
	private String albumUrl;
	private String albumArtUrl;

	public RestfulSong(Song song) {
		super(song);
		
		Artist artist = song.getArtist();
		this.artistName = artist.getName();
		this.artistUrl = getContextPath() + "/app/ajax/music/artist/id/" + artist.getId();

		Album album = song.getAlbum();
		this.albumName = album.getName();
		this.albumUrl = getContextPath() + "/app/ajax/music/album/id/" + album.getId();
		this.albumArtUrl = getContextPath() + "/app/music/album-art/" + ImageType.THUMBNAIL.name().toLowerCase() + "/"
				+ album.getId();

	}

	public String getAlbumArtUrl() {
		return albumArtUrl;
	}

	public void setAlbumArtUrl(String albumArtUrl) {
		this.albumArtUrl = albumArtUrl;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistUrl() {
		return artistUrl;
	}

	public void setArtistUrl(String artistUrl) {
		this.artistUrl = artistUrl;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getAlbumUrl() {
		return albumUrl;
	}

	public void setAlbumUrl(String albumUrl) {
		this.albumUrl = albumUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestfulSong [artistName=");
		builder.append(artistName);
		builder.append(", artistUrl=");
		builder.append(artistUrl);
		builder.append(", albumName=");
		builder.append(albumName);
		builder.append(", albumUrl=");
		builder.append(albumUrl);
		builder.append(", albumArtUrl=");
		builder.append(albumArtUrl);
		builder.append(", getTitle()=");
		builder.append(getTitle());
		builder.append(", getStreamUrl()=");
		builder.append(getStreamUrl());
		builder.append(", getStreamFormat()=");
		builder.append(getStreamFormat());
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append(", getClass()=");
		builder.append(getClass());
		builder.append(", hashCode()=");
		builder.append(hashCode());
		builder.append("]");
		return builder.toString();
	}
	
	

}