package org.mashupmedia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

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
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.FileHelper.FileType;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.StringHelper.Encoding;
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

		String proxyEnabledValue = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_ENABLED));
		boolean isProxyEnabled = BooleanUtils.toBoolean(proxyEnabledValue);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(link);

		BasicHeader header = new BasicHeader("User-Agent", "Mashup Media/1.0 +http://www.mashupmedia.org");
		httpGet.addHeader(header);

		if (!isProxyEnabled) {
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
		} else {
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

		}
		return null;

	}

	@Override
	public byte[] getAlbumArtImageBytes(AlbumArtImage image, ImageType imageType) throws Exception {
		if (image == null) {
			return null;
		}

		File file = null;
		if (imageType == ImageType.THUMBNAIL) {
			file = new File(image.getThumbnailUrl());
		} else {
			file = new File(image.getUrl());
		}
		if (!file.exists()) {
			return null;
		}

		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(fileInputStream);
		IOUtils.closeQuietly(fileInputStream);
		return bytes;
	}

	@Override
	public File getMediaItemStreamFile(long mediaItemId, EncodeType encodeType) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		Library library = mediaItem.getLibrary();
		File file = null;

		EncodeStatusType encodeStatusType = mediaItem.getEncodeStatusType();

		if (encodeType == EncodeType.ENCODED && (encodeStatusType == EncodeStatusType.ENCODED || encodeStatusType == EncodeStatusType.PROCESSING)) {
			file = FileHelper.createMediaFile(library.getId(), mediaItemId, FileType.MEDIA_ITEM_STREAM_ENCODED);
			return file;
		}

		String path = mediaItem.getPath();
		file = new File(path);

		return file;
	}

	@Override
	public long getMediaItemFileSize(long mediaItemId) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			logger.error("Unable to start media stream, no media type found");
			return 0;
		}

		String path = mediaItem.getPath();
		File file = new File(path);
		long size = file.length();
		return size;
	}
	
	@Override
	public String proceessRemoteLibraryConnection(String remoteLibraryUrl) {		
		InputStream inputStream = connect(remoteLibraryUrl);
		StringWriter stringWriter = new StringWriter();
		try {
			IOUtils.copy(inputStream, stringWriter, Encoding.UTF8.getEncodingString());
		} catch (IOException e) {
			logger.error(e);
		} 
		
		IOUtils.closeQuietly(stringWriter);
		IOUtils.closeQuietly(inputStream);
		
		return stringWriter.toString();
	}

}
