package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.ImageHelper.ImageType;

public interface ConnectionManager {
	
	public enum LocationType {
		FTP, LOCAL
	}
	
	public boolean isFtpLocationValid(FtpLocation ftpLocation);

	public List<Song> getFtpSongs(MusicLibrary musicLibrary);

	public FTPClient connectToFtp(FtpLocation ftpLocation) throws Exception;

	public byte[] getAlbumArtImageBytes(AlbumArtImage albumArtImage, ImageType imageType) throws Exception;

	public void startMediaItemStream(long mediaItemId, File file);

	public File getMediaItemStreamFile(long mediaItemId);
	
	public InputStream connect(String link);

	public long getMediaItemFileSize(long mediaItemId);
	
	public LocationType getLocationType(long mediaItemId);

}
