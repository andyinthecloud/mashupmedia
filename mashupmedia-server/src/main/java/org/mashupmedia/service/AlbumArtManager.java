package org.mashupmedia.service;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Track;

public interface AlbumArtManager {

	public MetaImage getMetaImage(MusicLibrary musicLibrary, Track track) throws Exception;
}
