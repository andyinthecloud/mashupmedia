package org.mashupmedia.restful;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.Configuration;
import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.util.StringHelper.Encoding;
import org.mashupmedia.web.remote.RemoteImage;
import org.mashupmedia.web.remote.RemoteImage.RemoteImageType;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service("themoviedb")
@Slf4j
public class TheMovieDatabaseVideoWebServiceImpl extends AbstractCachingVideoWebServiceImpl{
	
	private final static String THE_MOVIE_DB_API_KEY = "themoviedb.api.key";
	private final static String THE_MOVIE_DB_ROOT_URL = "http://api.themoviedb.org";
	private final static String THE_MOVIE_DB_BASE_IMAGE_URL = "themoviedb.base.image.url";
	private final static String PROXY_URL_PREFIX = "/app/proxy/binary-file?url=";
	
	@Autowired
	private ConnectionManager connectionManager;
	
	@Autowired
	private ConfigurationManager configurationManager;
	

	@Override
	public RemoteMediaMetaItem getVideoInformation(Video video) throws Exception {
		if (video == null) {
			return null;
		}

		String remoteId = StringUtils.trimToEmpty(video.getRemoteId());
		RemoteMediaMetaItem remoteMediaMetaItem = getRemoteMediaItemFromCache(remoteId);
		if (remoteMediaMetaItem != null) {
			return remoteMediaMetaItem;
		}

		String title = video.getSearchText();

		JsonNode jsonVideo = null;
		if (StringUtils.isNotEmpty(remoteId)) {
			jsonVideo = getVideoInfoDocumentByRemoteId(remoteId);			
		} else {
			jsonVideo = getVideoInfoDocumentByTitle(title);
		}

		remoteMediaMetaItem = new RemoteMediaMetaItem();
		
		if (jsonVideo == null || !jsonVideo.has("id")) {
			return remoteMediaMetaItem;
		}
		
		remoteMediaMetaItem.setDate(new Date());

		String remoteName = jsonVideo.get("original_title").asText();
		remoteMediaMetaItem.setName(remoteName);

		remoteId = jsonVideo.get("id").asText();
		remoteMediaMetaItem.setRemoteId(remoteId);

		String introduction = jsonVideo.get("overview").asText();
		remoteMediaMetaItem.setIntroduction(introduction);

		List<RemoteImage> remoteImages = processRemoteImages(jsonVideo);
		remoteMediaMetaItem.setRemoteImages(remoteImages);

		addRemoteMediaItemToCache(remoteMediaMetaItem);

		return remoteMediaMetaItem;
	}

	private JsonNode getVideoInfoDocumentByTitle(String title) throws IOException {
		StringBuilder urlBuilder = new StringBuilder(THE_MOVIE_DB_ROOT_URL);
		urlBuilder.append("/3/search/movie");
		urlBuilder.append("?api_key=");
		urlBuilder.append(MessageHelper.getMessage(THE_MOVIE_DB_API_KEY));
		urlBuilder.append("&query=");
		String formattedTitle = StringHelper.formatTextToUrlParameter(title);
		urlBuilder.append(formattedTitle);

		InputStream searchInputStream = connectionManager.connect(urlBuilder.toString());
		if (searchInputStream == null) {
			throw new ConnectException("Unable to connect to " + urlBuilder.toString());
		}
		
		String jsonVideoSearchText = IOUtils.toString(searchInputStream, Encoding.UTF8.getEncodingString());
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonResultsObject = objectMapper.readTree(jsonVideoSearchText);
		JsonNode jsonResultsArray = jsonResultsObject.get("results");
		
		IOUtils.closeQuietly(searchInputStream);

		if (jsonResultsArray == null || jsonResultsArray.size() == 0) {
			return null;
		}
		
		JsonNode jsonResultObject = jsonResultsArray.get(0);
		String remoteId = jsonResultObject.get("id").asText();
		
		
		JsonNode jsonVideoObject = getVideoInfoDocumentByRemoteId(remoteId);
		return jsonVideoObject;
	}

	protected List<RemoteImage> processRemoteImages(JsonNode jsonVideo) throws IOException {
		
		String baseImageUrl = getBaseImageUrl();
		
		
		
		List<RemoteImage> remoteImages = new ArrayList<RemoteImage>();
		
		String posterImageUrl = StringUtils.trimToEmpty( jsonVideo.get("poster_path").asText());
		if (StringUtils.isNotEmpty(posterImageUrl)) {
			posterImageUrl = baseImageUrl + StringHelper.formatTextToUrlParameter(posterImageUrl);			
			posterImageUrl = PROXY_URL_PREFIX + posterImageUrl;						
			RemoteImage remoteImage = new RemoteImage();
			remoteImage.setImageUrl(posterImageUrl);	
			remoteImage.setRemoteImageType(RemoteImageType.POSTER);
			remoteImages.add(remoteImage);
		}
		
		String backdropImageUrl = StringUtils.trimToEmpty( jsonVideo.get("backdrop_path").asText());
		if (StringUtils.isNotEmpty(backdropImageUrl)) {
			backdropImageUrl = baseImageUrl + StringHelper.formatTextToUrlParameter(backdropImageUrl);			
			backdropImageUrl = PROXY_URL_PREFIX + backdropImageUrl;						
			RemoteImage remoteImage = new RemoteImage();
			remoteImage.setImageUrl(backdropImageUrl);			
			remoteImage.setRemoteImageType(RemoteImageType.BACKDROP);
			remoteImages.add(remoteImage);
		}
				
		return remoteImages;
	}

	protected String getBaseImageUrl() throws IOException {
		Configuration configuration = configurationManager.getConfiguration(THE_MOVIE_DB_BASE_IMAGE_URL);
		if (configuration != null) {
			Date createdOn = configuration.getCreatedOn();
			
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			Date cutOffDate = calendar.getTime();
			
			
			if (createdOn.after(cutOffDate)) {
				String baseImageUrl = configuration.getValue();
				return baseImageUrl;
			}
		}
		
		StringBuilder urlBuilder = new StringBuilder(THE_MOVIE_DB_ROOT_URL);
		urlBuilder.append("/3/configuration");
		
		urlBuilder.append("?api_key=");
		urlBuilder.append(MessageHelper.getMessage(THE_MOVIE_DB_API_KEY));

		String url = urlBuilder.toString();

		InputStream inputStream = connectionManager.connect(url);
		if (inputStream == null) {
			throw new ConnectException("Unable to connect to " + urlBuilder.toString());
		}
		
		String jsonConfigurationText = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		IOUtils.closeQuietly(inputStream);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonConfiguration = objectMapper.readTree(jsonConfigurationText);
		JsonNode jsonConfigurationImages = jsonConfiguration.get("images");
		String baseImageUrl = jsonConfigurationImages.get("base_url").asText();
		
		configuration = new Configuration();
		configuration.setKey(THE_MOVIE_DB_BASE_IMAGE_URL);
		configuration.setValue(baseImageUrl + "original/");
		configurationManager.saveConfiguration(configuration);
		
		baseImageUrl = configuration.getValue();		
		return baseImageUrl;
	}

	protected JsonNode getVideoInfoDocumentByRemoteId(String remoteId) throws IOException{
		StringBuilder urlBuilder = new StringBuilder(THE_MOVIE_DB_ROOT_URL);
		urlBuilder.append("/3/movie/");
		urlBuilder.append(remoteId);
		
		urlBuilder.append("?api_key=");
		urlBuilder.append(MessageHelper.getMessage(THE_MOVIE_DB_API_KEY));

		String videoInfoUrl = urlBuilder.toString();

		InputStream inputStream = connectionManager.connect(videoInfoUrl);
		if (inputStream == null) {
			throw new ConnectException("Unable to connect to " + urlBuilder.toString());
		}
		
		String jsonVideoText = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonVideo = objectMapper.readTree(jsonVideoText);
		IOUtils.closeQuietly(inputStream);
		return jsonVideo;
	}

}
