package org.mashupmedia.restful;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.Configuration;
import org.mashupmedia.model.media.Video;
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

@Service("themoviedb")
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

		JSONObject jsonVideo = null;
		if (StringUtils.isNotEmpty(remoteId)) {
			jsonVideo = getVideoInfoDocumentByRemoteId(remoteId);
//			String status = XmlHelper.getTextFromElement(videoInfoDocument, "/lfm/@status");
//			if (status.equalsIgnoreCase("failed")) {
//				videoInfoDocument = getVideoInfoDocumentByArtistName(title);
//			}
			
		} else {
			jsonVideo = getVideoInfoDocumentByTitle(title);
		}

		remoteMediaMetaItem = new RemoteMediaMetaItem();
		
		if (!jsonVideo.has("id")) {
			return remoteMediaMetaItem;
		}
		

		remoteMediaMetaItem.setDate(new Date());

		
		
		String remoteName = jsonVideo.getString("original_title");
		remoteMediaMetaItem.setName(remoteName);

		remoteId = jsonVideo.getString("id");
		remoteMediaMetaItem.setRemoteId(remoteId);

		String introduction = jsonVideo.getString("overview");
		remoteMediaMetaItem.setIntroduction(introduction);

		List<RemoteImage> remoteImages = processRemoteImages(jsonVideo);
		remoteMediaMetaItem.setRemoteImages(remoteImages);

		addRemoteMediaItemToCache(remoteMediaMetaItem);

		return remoteMediaMetaItem;
	}

	private JSONObject getVideoInfoDocumentByTitle(String title) throws IOException {
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
		
		JSONObject jsonResultsObject = JSONObject.fromObject(jsonVideoSearchText);
		JSONArray jsonResultsArray = jsonResultsObject.getJSONArray("results");
		
		IOUtils.closeQuietly(searchInputStream);

		if (jsonResultsArray == null || jsonResultsArray.size() == 0) {
			return null;
		}
		
		JSONObject jsonResultObject = (JSONObject)jsonResultsArray.get(0);
		String remoteId = jsonResultObject.getString("id");
		
		
		JSONObject jsonVideoObject = getVideoInfoDocumentByRemoteId(remoteId);
		return jsonVideoObject;
	}

	protected List<RemoteImage> processRemoteImages(JSONObject jsonVideo) throws IOException {
		
		String baseImageUrl = getBaseImageUrl();
		
		
		List<RemoteImage> remoteImages = new ArrayList<RemoteImage>();
		
		String posterImageUrl = StringUtils.trimToEmpty( jsonVideo.getString("poster_path"));
		if (StringUtils.isNotEmpty(posterImageUrl)) {
			posterImageUrl = baseImageUrl + StringHelper.formatTextToUrlParameter(posterImageUrl);			
			posterImageUrl = PROXY_URL_PREFIX + posterImageUrl;						
			RemoteImage remoteImage = new RemoteImage();
			remoteImage.setImageUrl(posterImageUrl);	
			remoteImage.setRemoteImageType(RemoteImageType.POSTER);
			remoteImages.add(remoteImage);
		}
		
		String backdropImageUrl = StringUtils.trimToEmpty( jsonVideo.getString("backdrop_path"));
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
		
		JSONObject jsonConfiguration = JSONObject.fromObject(jsonConfigurationText);
		JSONObject jsonConfigurationImages = jsonConfiguration.getJSONObject("images");
		String baseImageUrl = jsonConfigurationImages.getString("base_url");
		
		configuration = new Configuration();
		configuration.setKey(THE_MOVIE_DB_BASE_IMAGE_URL);
		configuration.setValue(baseImageUrl + "original/");
		configurationManager.saveConfiguration(configuration);
		
		return baseImageUrl;
	}

	protected JSONObject getVideoInfoDocumentByRemoteId(String remoteId) throws IOException{
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
		
		JSONObject jsonVideo = JSONObject.fromObject(jsonVideoText);
		IOUtils.closeQuietly(inputStream);
		return jsonVideo;
	}

}
