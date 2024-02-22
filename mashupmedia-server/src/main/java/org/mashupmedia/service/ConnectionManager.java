package org.mashupmedia.service;

import java.io.IOException;

import org.mashupmedia.model.media.music.MusicArtImage;
import org.mashupmedia.util.ImageHelper.ImageType;

public interface ConnectionManager {

	public byte[] getAlbumArtImageBytes(MusicArtImage albumArtImage, ImageType imageType) throws IOException;

	public long getMediaItemFileSize(long mediaItemId);

}
