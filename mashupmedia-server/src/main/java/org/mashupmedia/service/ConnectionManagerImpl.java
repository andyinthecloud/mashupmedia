package org.mashupmedia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
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
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.StringHelper.Encoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConnectionManagerImpl implements ConnectionManager {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private MediaManager mediaManager;

	protected boolean isProxyEnabled() {
		boolean isProxyEnabled = BooleanUtils
				.toBoolean(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_ENABLED));
		return isProxyEnabled;

	}

	private String getProxyUrl() {
		String proxyUrl = StringUtils
				.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_URL));
		return proxyUrl;
	}

	private int getProxyPort() {
		String proxyPortValue = StringUtils
				.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_PORT));
		int proxyPort = NumberUtils.toInt(proxyPortValue);
		return proxyPort;
	}

	private String getProxyUsername() {
		String proxyUsername = StringUtils
				.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_USERNAME));
		return proxyUsername;
	}

	private String getProxyPassword() {
		String proxyPassword = StringUtils
				.trimToEmpty(configurationManager.getConfigurationDecryptedValue(MashUpMediaConstants.PROXY_PASSWORD));
		return proxyPassword;
	}

	public InputStream connect(String link) {

		String proxyEnabledValue = StringUtils
				.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_ENABLED));
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
	public byte[] getAlbumArtImageBytes(AlbumArtImage image, ImageType imageType) throws IOException {
		if (image == null) {
			return null;
		}

		String filePath = null;
		if (imageType == ImageType.THUMBNAIL) {
			filePath = image.getThumbnailUrl();
		} else {
			filePath = image.getUrl();
		}

		byte[] bytes = getFileBytes(filePath);
		return bytes;
	}

	private byte[] getFileBytes(String filePath) throws IOException {
		if (StringUtils.isBlank(filePath)) {
			return null;
		}

		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(fileInputStream);
		IOUtils.closeQuietly(fileInputStream);
		return bytes;
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

		File file = new File(remoteLibraryUrl);
		if (file.exists()) {
			try {
				String xml = FileUtils.readFileToString(file);
				return xml;
			} catch (IOException e) {
				logger.error("Error reading remoteLibraryFile at: " + file.getAbsolutePath(), e);
			}
		}

		InputStream inputStream = connect(remoteLibraryUrl);
		StringWriter stringWriter = new StringWriter();
		try {
			IOUtils.copy(inputStream, stringWriter, Encoding.UTF8.getEncodingString());
		} catch (IOException e) {
			logger.error("Error processing remote library", e);
		}

		IOUtils.closeQuietly(stringWriter);
		IOUtils.closeQuietly(inputStream);

		return stringWriter.toString();
	}

}
