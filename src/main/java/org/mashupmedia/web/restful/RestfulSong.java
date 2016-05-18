package org.mashupmedia.web.restful;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jaudiotagger.audio.mp4.atom.Mp4HdlrBox.MediaDataType;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;

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
		this.artistUrl = getContextPath() + "/app/music/artist/" + artist.getId();

		Album album = song.getAlbum();
		this.albumName = album.getName();
		this.albumUrl = getContextPath() + "/app/music/album/" + album.getId();
		this.albumArtUrl = getContextPath() + "/app/music/album-art/" + ImageType.THUMBNAIL.name().toLowerCase() + "/"
				+ album.getId();

	}
	
	@Override
	protected void prepareStreams(MediaItem mediaItem) {
		Set<MediaEncoding> mediaEncodings = mediaItem.getMediaEncodings();
		if (mediaEncodings == null || mediaEncodings.isEmpty()) {
			return;
		}

		long mediaItemId = mediaItem.getId();
		List<RestfulStream> restfulStreamList = new ArrayList<RestfulStream>();
		for (MediaEncoding mediaEncoding : mediaEncodings) {
			MediaContentType mediaContentType = mediaEncoding.getMediaContentType();
			String format = mediaContentType.getjPlayerContentType();
			String url = MediaItemHelper.prepareUrlStream(contextPath, mediaItemId, format);
			RestfulStream restfulStream = new RestfulStream(format, url);
			restfulStreamList.add(restfulStream);
		}
		
		MediaItemHelper.addEssentialStreamUrls(LibraryType.MUSIC, contextPath, mediaItemId, restfulStreamList);
		
		RestfulStream[] streams = new RestfulStream[restfulStreamList.size()];
		streams = restfulStreamList.toArray(streams);
		setStreams(streams);
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
		builder.append("]");
		return builder.toString();
	}
	
	

}
