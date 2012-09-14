package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import it.sauronsoftware.ftp4j.connectors.HTTPTunnelConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.spi.LoadState;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.comparator.FtpFileComparator;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.EncryptionHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConnectionManagerImpl implements ConnectionManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private MediaManager mediaManager;

	protected boolean isProxyEnabled() {
		boolean isProxyEnabled = BooleanUtils.toBoolean(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_ENABLED));
		return isProxyEnabled;

	}

	private FTPClient prepareFtpClient() {
		FTPClient ftpClient = new FTPClient();

		if (!isProxyEnabled()) {
			return ftpClient;
		}

		String proxyUrl = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_URL));
		String proxyPortValue = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_PORT));
		String proxyUsername = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_USERNAME));
		String proxyPassword = StringUtils.trimToEmpty(configurationManager.getConfigurationDecryptedValue(MashUpMediaConstants.PROXY_PASSWORD));
		// String proxyPassword = "l3ST3R";

		int proxyPort = NumberUtils.toInt(proxyPortValue);
		HTTPTunnelConnector connector = new HTTPTunnelConnector(proxyUrl, proxyPort, proxyUsername, proxyPassword);
		ftpClient.setConnector(connector);
		return ftpClient;

	}

	@Override
	public boolean isFtpLocationValid(FtpLocation ftpLocation) {
		FTPClient ftpClient = null;
		try {
			ftpClient = connectToFtp(ftpLocation);
			return true;
		} catch (Exception e) {
			logger.error("Unable to connect to ftp server.", e);
		} finally {
			try {
				ftpClient.disconnect(true);
			} catch (Exception e) {
				logger.error("Unable to disconnect from ftp client", e);
			}
		}

		return false;
	}

	public FTPClient connectToFtp(FtpLocation ftpLocation) throws Exception {

		FTPClient ftpClient = prepareFtpClient();

		String host = ftpLocation.getHost();
		Integer port = ftpLocation.getPort();
		String path = StringUtils.trimToEmpty(ftpLocation.getPath());
		String username = ftpLocation.getUsername();
		String password = ftpLocation.getPassword();

		if (port == null) {
			ftpClient.connect(host);
		} else {
			ftpClient.connect(host, port);
		}

		if (StringUtils.isNotBlank(username)) {
			ftpClient.login(username, password);
		}

		if (StringUtils.isNotEmpty(path)) {
			ftpClient.changeDirectory(path);
		}

		return ftpClient;
	}

	@Override
	public List<Song> getFtpSongs(MusicLibrary musicLibrary) {

		FtpLocation ftpLocation = (FtpLocation) musicLibrary.getLocation();

		List<Song> songs = new ArrayList<Song>();
		FTPClient ftpClient = null;
		try {
			ftpClient = connectToFtp(ftpLocation);
			processFtpSongs(ftpClient, songs, musicLibrary, null, new ArrayList<String>(), 0);

		} catch (Exception e) {
			logger.error("Unable to connect to ftp server.", e);
		} finally {
			try {
				ftpClient.disconnect(true);
			} catch (Exception e) {
				logger.error("Unable to disconnect from ftp client", e);
			}
		}

		return songs;
	}

	protected void processFtpSongs(FTPClient ftpClient, List<Song> songs, MusicLibrary musicLibrary, String artistName, List<String> albumNameParts,
			int trackNumber) {
		try {
			FTPFile[] ftpFiles = ftpClient.list();
			Arrays.sort(ftpFiles, new FtpFileComparator());
			for (FTPFile ftpFile : ftpFiles) {
				String fileName = ftpFile.getName();
				if (ftpFile.getType() == FTPFile.TYPE_DIRECTORY) {
					ftpClient.changeDirectory(fileName);
					artistName = StringUtils.trimToEmpty(artistName);
					if (StringUtils.isEmpty(artistName)) {
						artistName = fileName;
					} else {
						albumNameParts.add(fileName);
					}

					processFtpSongs(ftpClient, songs, musicLibrary, artistName, albumNameParts, 0);
					if (albumNameParts.isEmpty()) {
						artistName = null;
					} else {
						albumNameParts.remove(albumNameParts.size() - 1);
					}
					ftpClient.changeDirectoryUp();
					continue;

				}

				if (FileHelper.isSupportedSong(fileName)) {
					String filePath = ftpClient.currentDirectory() + "/" + fileName;

					trackNumber++;
					Artist artist = new Artist();
					artist.setName(artistName);

					Album album = new Album();
					String albumName = StringHelper.getAlbumName(albumNameParts);
					album.setName(albumName);
					album.setArtist(artist);

					if (trackNumber == 1) {
						AlbumArtImage albumArtImage = getAlbumArtImage(ftpClient, musicLibrary, album);
						album.setAlbumArtImage(albumArtImage);
					}

					Song song = new Song();
					song.setArtist(artist);
					song.setAlbum(album);
					song.setFileName(fileName);
					song.setPath(filePath);
					song.setSizeInBytes(ftpFile.getSize());
					song.setTitle(fileName);
					song.setTrackNumber(trackNumber);
					songs.add(song);
					continue;
				}

			}
		} catch (Exception e) {
			logger.error("Unable to list ftp files", e);
		}
	}

	protected AlbumArtImage getAlbumArtImage(FTPClient ftpClient, MusicLibrary musicLibrary, Album album) throws IllegalStateException, IOException,
			FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {

		String albumArtImagePattern = musicLibrary.getAlbumArtImagePattern();
		FTPFile[] ftpFiles = ftpClient.list();
		for (FTPFile ftpFile : ftpFiles) {
			String fileName = ftpFile.getName();
			if (FileHelper.isSupportedImage(fileName) && FileHelper.isMatchingFileNamePattern(fileName, albumArtImagePattern)) {
				String filePath = ftpClient.currentDirectory() + "/" + fileName;
				AlbumArtImage albumArtImage = new AlbumArtImage();
				albumArtImage.setAlbum(album);
				albumArtImage.setLibrary(musicLibrary);
				albumArtImage.setName(ftpFile.getName());
				albumArtImage.setUrl(filePath);
				return albumArtImage;

			}
		}

		return null;
	}

	@Override
	public byte[] getAlbumArtImageBytes(AlbumArtImage image) throws Exception {
		if (image == null) {
			return null;
		}

		Library library = image.getLibrary();
		if (library == null) {
			return null;
		}

		Location location = library.getLocation();
		if (location == null) {
			return null;
		}

		String imagePath = StringUtils.trimToEmpty(image.getUrl());
		if (StringUtils.isEmpty(imagePath)) {
			return null;
		}

		byte[] bytes = null;
		if (location instanceof FtpLocation) {
			FtpLocation ftpLocation = (FtpLocation) location;
			String password = ftpLocation.getPassword();
			password = EncryptionHelper.decryptText(password);
			ftpLocation.setPassword(password);
			bytes = getFtpImageBytes(ftpLocation, imagePath);
		} else {
			File imageFile = new File(imagePath);
			FileInputStream fileInputStream = new FileInputStream(imageFile);
			bytes = IOUtils.toByteArray(fileInputStream);
			fileInputStream.close();
		}

		return bytes;

	}

	private byte[] getFtpImageBytes(FtpLocation ftpLocation, String path) throws Exception {
		FTPClient ftpClient = connectToFtp(ftpLocation);
		try {
			ftpClient.setType(FTPClient.TYPE_BINARY);
			File imageFile = File.createTempFile("mashupmedia_image", Long.toString(System.nanoTime()));
			ftpClient.download(path, imageFile);
			FileInputStream fileInputStream = new FileInputStream(imageFile);
			byte[] imageBytes = IOUtils.toByteArray(fileInputStream);
			fileInputStream.close();
			return imageBytes;
		} finally {
			ftpClient.disconnect(true);
		}
	}

	@Override
	public File getMediaItemStreamFile(long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		Library library = mediaItem.getLibrary();
		Location location = library.getLocation();
		LocationType locationType = getLocationType(location);

		File file = null;
		if (locationType == LocationType.FTP) {
			
		} else {
			String path = mediaItem.getPath();
			file = new File(path);
		}

		return file;
	}

	private LocationType getLocationType(Location location) {
		if (location instanceof FtpLocation) {
			return LocationType.FTP;
		}

		return LocationType.LOCAL;
	}

	@Override
	public InputStream getMediaItemInputStream(Long mediaItemId) throws Exception {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			return null;
		}

		Library library = mediaItem.getLibrary();
		Location location = library.getLocation();
		// location.getPath();
		String path = mediaItem.getPath();

		// String path = "";
		if (location instanceof FtpLocation) {
			FtpLocation ftpLocation = (FtpLocation) location;
			FTPClient ftpClient = connectToFtp(ftpLocation);
			// ftpLocation.setPath(path);
			ftpClient.setType(FTPClient.TYPE_BINARY);
			//
			// ftpClient.download(path, imageFile);
			// ftpClient.
			// ftpLocation.

		} else {
			FileInputStream fileInputStream = new FileInputStream(path);
			return fileInputStream;
		}

		return null;
	}

	// @Override
	// public String getStreamingMediaItemFilePath(Long mediaItemId) {
	// MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
	// if (mediaItem == null) {
	// return null;
	// }
	//
	// Library library= mediaItem.getLibrary();
	// Location location = library.getLocation();
	// // location.getPath();
	// String path = mediaItem.getPath();
	//
	// // String path = "";
	// if (location instanceof FtpLocation) {
	//
	// } else {
	//
	// }
	//
	//
	// String path = mediaItem.getPath();
	//
	// return null;
	// }

}
