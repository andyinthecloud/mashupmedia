package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.connectors.HTTPTunnelConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.comparator.FtpFileComparator;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.EncryptionHelper;
import org.mashupmedia.util.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConnectionManagerImpl implements ConnectionManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;
	
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
	public List<Artist> getFtpArtists(FtpLocation location) {

		List<Artist> artists = new ArrayList<Artist>();
		FTPClient ftpClient = null;
		try {
			ftpClient = connectToFtp(location);
			processFtpArtists(ftpClient, artists);

		} catch (Exception e) {
			logger.error("Unable to connect to ftp server.", e);
		} finally {
			try {
				ftpClient.disconnect(true);
			} catch (Exception e) {
				logger.error("Unable to disconnect from ftp client", e);
			}
		}

		return artists;
	}

	private void processFtpArtists(FTPClient ftpClient, List<Artist> artists) {

		try {
			FTPFile[] ftpArtistFiles = ftpClient.list();
			for (FTPFile ftpArtistFile : ftpArtistFiles) {
				Artist artist = new Artist();
				String name = ftpArtistFile.getName();
				artist.setName(name);
				List<Album> albums = new ArrayList<Album>();
				processFtpAlbums(artist, albums, ftpArtistFile, ftpClient, null);
				artist.setAlbums(albums);
				artists.add(artist);

			}
		} catch (Exception e) {
			logger.error("Unable to list ftp files", e);
		}

	}

	private void processFtpAlbums(Artist artist, List<Album> albums, FTPFile ftpArtistFile, FTPClient ftpClient, String prefix) throws IllegalStateException,
			IOException, FTPIllegalReplyException, FTPException {

		if (ftpArtistFile.getType() != FTPFile.TYPE_DIRECTORY) {
			return;
		}

		String name = ftpArtistFile.getName();
		String path = ftpClient.currentDirectory();
		String artistPath = path + "/" + name;

		try {
			ftpClient.changeDirectory(artistPath);
			FTPFile[] ftpAlbumFiles = ftpClient.list();
			for (FTPFile ftpAlbumFile : ftpAlbumFiles) {
				String albumName = ftpAlbumFile.getName();
				if (StringUtils.isNotBlank(prefix)) {
					albumName = prefix + " - " + albumName;
				}					

				if (FileHelper.isAlbum(ftpAlbumFile, ftpClient)) {
					Album album = new Album();
					album.setArtist(artist);
					album.setName(albumName);
					processFtpAlbum(album, ftpAlbumFile, ftpClient);
					albums.add(album);
					continue;
				} 
				
				if (FileHelper.hasFolders(ftpAlbumFile, ftpClient)) {
					processFtpAlbums(artist, albums, ftpAlbumFile, ftpClient, albumName);
				}
			}

		} catch (Exception e) {
			logger.error("Unable to list ftp files", e);
		} finally {
			ftpClient.changeDirectory(path);
		}

	}

	private void processFtpAlbum(Album album, FTPFile ftpAlbumFile, FTPClient ftpClient) throws IllegalStateException, IOException,
			FTPIllegalReplyException, FTPException {
		if (ftpAlbumFile.getType() != FTPFile.TYPE_DIRECTORY) {
			return;
		}

		List<Song> songs = new ArrayList<Song>();

		String name = ftpAlbumFile.getName();
		String path = ftpClient.currentDirectory();
		String albumPath = path + "/" + name;
		try {
			ftpClient.changeDirectory(albumPath);
			FTPFile[] ftpMediaFiles = ftpClient.list();
			Arrays.sort(ftpMediaFiles, new FtpFileComparator());

			for (int i = 0; i < ftpMediaFiles.length; i++) {
				FTPFile ftpMediaFile = ftpMediaFiles[i];
				String fileName = ftpMediaFile.getName();
				String mediaPath = albumPath + "/" + fileName;
				if (FileHelper.isSupportedSong(fileName)) {
					Song song = new Song();
					song.setAlbum(album);
					song.setFileName(fileName);
					song.setPath(mediaPath);
					song.setSizeInBytes(ftpMediaFile.getSize());
					song.setTitle(fileName);
					song.setTrackNumber(i + 1);
					songs.add(song);
				} else if (FileHelper.isSupportedImage(fileName)) {
					AlbumArtImage image = new AlbumArtImage();
					image.setName(fileName);
					image.setUrl(mediaPath);
					album.setAlbumArtImage(image);

				}
			}

			album.setSongs(songs);

		} catch (Exception e) {
			logger.error("Unable to list ftp files", e);
		} finally {
			ftpClient.changeDirectory(path);
		}

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
}
