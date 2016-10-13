package org.mashupmedia.service;

import java.io.IOException;
import java.io.InputStream;

import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.util.ImageHelper.ImageType;

public interface ConnectionManager {

	public byte[] getAlbumArtImageBytes(AlbumArtImage albumArtImage, ImageType imageType) throws IOException;

	public InputStream connect(String link);

	public long getMediaItemFileSize(long mediaItemId);

	public String proceessRemoteLibraryConnection(String remoteLibraryUrl);

}
