package org.mashupmedia.service;

import java.io.File;
import java.io.InputStream;

import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.util.ImageHelper.ImageType;

public interface ConnectionManager {
	
	
	public enum EncodeType {
		UNPROCESSED, ENCODED, BEST;
	}
	

	public byte[] getAlbumArtImageBytes(AlbumArtImage albumArtImage, ImageType imageType) throws Exception;

	public File getMediaItemStreamFile(long mediaItemId, EncodeType encodeType);
	
	public InputStream connect(String link);

	public long getMediaItemFileSize(long mediaItemId);
	
}
