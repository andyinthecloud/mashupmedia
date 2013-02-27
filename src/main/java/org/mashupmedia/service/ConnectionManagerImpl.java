package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.connectors.HTTPTunnelConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
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
import org.mashupmedia.util.FileHelper.FileType;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.LibraryHelper;
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
	
	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;
	
	protected boolean isProxyEnabled() {
		boolean isProxyEnabled = BooleanUtils.toBoolean(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_ENABLED));
		return isProxyEnabled;

	}

	private String getProxyUrl() {
		String proxyUrl = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_URL));
		return proxyUrl;
	}

	private int getProxyPort() {
		String proxyPortValue = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_PORT));
		int proxyPort = NumberUtils.toInt(proxyPortValue);
		return proxyPort;
	}

	private String getProxyUsername() {
		String proxyUsername = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_USERNAME));
		return proxyUsername;
	}

	private String getProxyPassword() {
		String proxyPassword = StringUtils.trimToEmpty(configurationManager.getConfigurationDecryptedValue(MashUpMediaConstants.PROXY_PASSWORD));
		return proxyPassword;
	}

	public InputStream connect(String link) {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(link);
		
		BasicHeader header = new BasicHeader("User-Agent", "Mashup Media/1.0 +http://www.mashupmedia.org");
		httpGet.addHeader(header);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();			
			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				return inputStream;
			}
		} catch (Exception e) {
			logger.error("Unable to connect to host: " + link + ". Trying proxy...");
		}

		httpClient.getCredentialsProvider().setCredentials(new AuthScope(getProxyUrl(), getProxyPort()),
				new UsernamePasswordCredentials(getProxyUsername(), getProxyPassword()));

		HttpHost proxy = new HttpHost(getProxyUrl(), getProxyPort());
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		httpGet = new HttpGet(link);

		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				return inputStream;
			}

		} catch (Exception e) {
			logger.error("Unable to connect to host: " + link + " through proxy.", e);
		}

		return null;

	}

	private FTPClient prepareFtpClient() {
		FTPClient ftpClient = new FTPClient();

		if (!isProxyEnabled()) {
			return ftpClient;
		}

		HTTPTunnelConnector connector = new HTTPTunnelConnector(getProxyUrl(), getProxyPort(), getProxyUsername(), getProxyPassword());
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
		String password = EncryptionHelper.decryptText(ftpLocation.getPassword());

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
	public List<Song> getFtpSongs(MusicLibrary musicLibrary, Date date) {

		FtpLocation ftpLocation = (FtpLocation) musicLibrary.getLocation();

		List<Song> songs = new ArrayList<Song>();
		FTPClient ftpClient = null;
		try {
			ftpClient = connectToFtp(ftpLocation);
			processFtpSongs(date, ftpClient, songs, musicLibrary, null, new ArrayList<String>(), 0);

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

	protected void processFtpSongs(Date date, FTPClient ftpClient, List<Song> songs, MusicLibrary musicLibrary, String artistName, List<String> albumNameParts,
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

					processFtpSongs(date, ftpClient, songs, musicLibrary, artistName, albumNameParts, 0);
					if (albumNameParts.isEmpty()) {
						musicLibraryUpdateManager.saveSongs(musicLibrary, songs);
						songs = new ArrayList<Song>();
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
					artist.setFolderName(artistName);
					
					Album album = new Album();
					String albumName = StringHelper.getAlbumName(albumNameParts);
					album.setName(albumName);
					album.setFolderName(albumName);
					album.setArtist(artist);

//					if (trackNumber == 1) {
//						AlbumArtImage albumArtImage = getAlbumArtImage(ftpClient, musicLibrary, album);
//						album.setAlbumArtImage(albumArtImage);
//					}

					Song song = new Song();
					song.setUpdatedOn(date);
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



	@Override
	public byte[] getAlbumArtImageBytes(AlbumArtImage image, ImageType imageType) throws Exception {
		if (image == null) {
			return null;
		}

		File file = null;
		if (imageType  == ImageType.THUMBNAIL) {
			file = new File(image.getThumbnailUrl());
		} else {
			file = new File(image.getUrl());
		}		
		if (!file.exists()) {
			return null;
		}

		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(fileInputStream);
		return bytes;
	}



	@Override
	public File getMediaItemStreamFile(long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		Library library = mediaItem.getLibrary();
		Location location = library.getLocation();
		LocationType locationType = LibraryHelper.getLocationType(location);

		File file = null;
		if (locationType == LocationType.FTP) {
			long libraryId = library.getId();
			file = FileHelper.createMediaFile(libraryId, mediaItemId, FileType.MEDIA_ITEM_STREAM);
		} else {
			String path = mediaItem.getPath();
			file = new File(path);
		}

		return file;
	}

	@Override
	public LocationType getLocationType(long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		Library library = mediaItem.getLibrary();
		Location location = library.getLocation();
		LocationType locationType = LibraryHelper.getLocationType(location);
		return locationType;
	}



	@Override
	public void startMediaItemStream(long mediaItemId, File file) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			logger.error("Unable to start media stream, no media type found");
			return;
		}

		Library library = mediaItem.getLibrary();
		Location location = library.getLocation();
		LocationType locationType = LibraryHelper.getLocationType(location);
		if (locationType == LocationType.FTP) {
			FtpLocation ftpLocation = (FtpLocation) location;
			try {
				startFtpMediaStream(mediaItem, ftpLocation, file);
			} catch (Exception e) {
				logger.error("Error starting media stream.", e);
			}
		} else {
			return;
		}

	}

	private void startFtpMediaStream(MediaItem mediaItem, FtpLocation ftpLocation, File file) throws Exception {

		if (file.exists()) {
			return;
		}

		FTPClient ftpClient = null;
		try {
			ftpClient = connectToFtp(ftpLocation);
			ftpClient.setType(FTPClient.TYPE_BINARY);
			String mediaPath = mediaItem.getPath();
			ftpClient.download(mediaPath, file);
		} finally {
			ftpClient.disconnect(true);
		}
	}

	@Override
	public long getMediaItemFileSize(long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			logger.error("Unable to start media stream, no media type found");
			return 0;
		}

		Library library = mediaItem.getLibrary();
		Location location = library.getLocation();
		LocationType locationType = LibraryHelper.getLocationType(location);
		long size = 0;
		if (locationType == LocationType.LOCAL) {
			String path = mediaItem.getPath();
			File file = new File(path);
			size = file.length();

		} else if (locationType == LocationType.FTP) {
			FtpLocation ftpLocation = (FtpLocation) location;
			try {
				size = getFtpMediaItemFileSize(mediaItem, ftpLocation);
			} catch (Exception e) {
				logger.error("Unable to get the file size from ftp llibrary", e);
			}
		}

		return size;
	}

	private long getFtpMediaItemFileSize(MediaItem mediaItem, FtpLocation ftpLocation) throws Exception {

		long size = 0;
		FTPClient ftpClient = null;
		try {
			ftpClient = connectToFtp(ftpLocation);
			ftpClient.setType(FTPClient.TYPE_BINARY);
			String mediaPath = mediaItem.getPath();
			size = ftpClient.fileSize(mediaPath);
		} finally {
			ftpClient.disconnect(true);
		}

		return size;
	}

}
