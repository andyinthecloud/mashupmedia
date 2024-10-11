package org.mashupmedia.service;

import java.io.IOException;

import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.util.ImageHelper.ImageType;

public interface ConnectionManager {

	public byte[] getAlbumArtImageBytes(MetaImage albumArtImage, ImageType imageType) throws IOException;

	// public long getMediaItemFileSize(long mediaItemId);

}
