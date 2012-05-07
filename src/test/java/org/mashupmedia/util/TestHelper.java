package org.mashupmedia.util;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;

public class TestHelper {
	
	public static Song prepareSong(MusicLibrary musicLibrary, String songTitle) {
		Song song = new Song();
		Album album = new Album();
		album.setName("album");
		Artist artist = new Artist();
		artist.setName("artist");
		album.setArtist(artist);
		song.setAlbum(album);
		song.setArtist(artist);
		song.setLibrary(musicLibrary);
		song.setPath("path");
		song.setSizeInBytes(100);
		song.setTitle(songTitle);
		song.setTrackNumber(1);
		Year year = new Year();
		year.setYear(2012);
		song.setYear(year);
		return song;
	}

}
