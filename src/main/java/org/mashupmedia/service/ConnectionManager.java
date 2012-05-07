package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;

import java.util.List;

import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.AlbumArtImage;

public interface ConnectionManager {

	public boolean isFtpLocationValid(FtpLocation ftpLocation);

	public List<Artist> getFtpArtists(FtpLocation location);
	
	public FTPClient connectToFtp(FtpLocation ftpLocation) throws Exception;

	public byte[] getAlbumArtImageBytes(AlbumArtImage albumArtImage) throws Exception;
	

}
