package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;

import java.util.List;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Song;

public interface ConnectionManager {

	public boolean isFtpLocationValid(FtpLocation ftpLocation);

	public List<Song> getFtpSongs(MusicLibrary musicLibrary);

	public FTPClient connectToFtp(FtpLocation ftpLocation) throws Exception;

	public byte[] getAlbumArtImageBytes(AlbumArtImage albumArtImage) throws Exception;

}
