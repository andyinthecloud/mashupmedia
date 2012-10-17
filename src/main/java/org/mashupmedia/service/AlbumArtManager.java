package org.mashupmedia.service;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Song;

public interface AlbumArtManager {

	public AlbumArtImage getAlbumArtImage(MusicLibrary musicLibrary, Song song) throws Exception;
}
