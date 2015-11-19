package org.mashupmedia.web.restful;

import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.WebHelper;

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
		this.artistUrl = WebHelper.getContextPath() + "/app/ajax/music/artist/id/" + artist.getId();

		Album album = song.getAlbum();
		this.albumName = song.getDisplayTitle();
		this.albumUrl = WebHelper.getContextPath() + "/app/ajax/music/album/id/" + album.getId();

		AlbumArtImage albumArtImage = album.getAlbumArtImage();
		this.albumArtUrl = WebHelper.getContextPath() + "/app/music/album-art/" + ImageType.THUMBNAIL.name().toLowerCase() + "/"
				+ albumArtImage.getId();

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

}
