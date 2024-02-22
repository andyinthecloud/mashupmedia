package org.mashupmedia.service;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.music.MusicArtImage;
import org.mashupmedia.model.media.music.Track;

public interface AlbumArtManager {

	public MusicArtImage getAlbumArtImage(MusicLibrary musicLibrary, Track track) throws Exception;
}
