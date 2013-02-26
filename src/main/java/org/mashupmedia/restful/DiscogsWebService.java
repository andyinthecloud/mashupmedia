package org.mashupmedia.restful;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.StringHelper.Encoding;
import org.mashupmedia.web.remote.RemoteImage;
import org.mashupmedia.web.remote.RemoteMediaMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

@Service
public class DiscogsWebService {
	private static final String PREPEND_CACHE_KEY_ARTIST = "artist_";
	
	private Logger logger = Logger.getLogger(getClass());
	
	
	
	private Map<String, RemoteMediaMeta> remoteMediaCache = new HashMap<String, RemoteMediaMeta>();
	
	
	
	public static int DEFAULT_CACHE_SECONDS = 86400;

	

	@Autowired
	private MusicManager musicManager;

	private static String[] ARTICLES = { "the", "a" };

	@Autowired
	private ConnectionManager connectionManager;

	public RemoteMediaMeta getArtistInformation(Artist artist) throws IOException {

		if (artist == null) {
			return null;
		}

		String discogId = getDiscogArtistId(artist, true);
		if (StringUtils.isEmpty(discogId)) {
			return null;
		}
		
		String cacheArtistKey = PREPEND_CACHE_KEY_ARTIST + discogId;
		Date date = new Date();
		RemoteMediaMeta remoteMediaMeta = remoteMediaCache.get(cacheArtistKey);
		if (remoteMediaMeta != null) {
			long seconds = (date.getTime() - remoteMediaMeta.getDate().getTime()) / 1000;
			if (DEFAULT_CACHE_SECONDS > seconds) {
				return remoteMediaMeta;
			}

			remoteMediaCache.remove(remoteMediaMeta);

		}
		

		artist.setRemoteId(discogId);
		musicManager.saveArtist(artist);

		// String artistUrl = "http://api.discogs.com/artists/2517607";
		String artistUrl = "http://api.discogs.com/artists/" + discogId;
		logger.debug("Searching Discogs for artist information using url: " + artistUrl);
		InputStream inputStream = connectionManager.connect(artistUrl);
		String jsonArtistText = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		JSONObject jsonArtist = JSONObject.fromObject(jsonArtistText);
		remoteMediaMeta = new RemoteMediaMeta();
		
		String profile = StringUtils.trimToEmpty(jsonArtist.getString("profile"));
		profile = profile.replaceAll("\\[.=", "");
		profile = profile.replaceAll("\\]", "");		
		profile = profile.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
		remoteMediaMeta.setProfile(profile);

		JSONArray jsonImages = jsonArtist.getJSONArray("images");
		List<RemoteImage> remoteImages = new ArrayList<RemoteImage>();

		if (jsonImages != null) {
			for (int i = 0; i < jsonImages.size(); i++) {
				JSONObject jsonImage = jsonImages.getJSONObject(i);
				RemoteImage remoteImage = new RemoteImage();
				String imageUrl = StringUtils.trimToEmpty(jsonImage.getString("resource_url"));
				imageUrl = "http://s.pixogs.com/image/" + imageUrl.replaceFirst(".*/", "");				
				remoteImage.setImageUrl(imageUrl);

				int width = jsonImage.getInt("width");
				remoteImage.setWidth(width);
				
				int height = jsonImage.getInt("height");
				remoteImage.setHeight(height);
				
				String thumbUrl = jsonImage.getString("uri150");
				remoteImage.setThumbUrl(thumbUrl);
				
				remoteImages.add(remoteImage);
			}
		}

		inputStream.close();

		remoteMediaMeta.setRemoteImages(remoteImages);		
		remoteMediaCache.put(cacheArtistKey, remoteMediaMeta);

		return remoteMediaMeta;
	}


	protected String getDiscogArtistId(Artist artist, boolean isThrottle) throws IOException {
		String remoteId = StringUtils.trimToEmpty(artist.getRemoteId());
		if (StringUtils.isNotEmpty(remoteId)) {
			return remoteId;
		}

		String name = artist.getName();
		name = prepareSearchParameter(name);
		if (StringUtils.isEmpty(name)) {
			return "";
		}

		String searchUrl = "http://api.discogs.com/database/search?q=" + name + "&type=artist";
		logger.debug("Searching Discogs for artist id using url: " + searchUrl);
		InputStream inputStream = connectionManager.connect(searchUrl);
		String jsonSearchResults = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		JSONObject jsonObject = JSONObject.fromObject(jsonSearchResults);
		JSONArray jsonArray = jsonObject.getJSONArray("results");
		if (jsonArray == null) {
			return "";
		}
		int size = jsonArray.size();
		if (size == 0) {
			return "";
		}

		JSONObject jsonArtist = jsonArray.getJSONObject(0);
		logger.debug("Found jsonObject: " + jsonArtist.toString(2));
		remoteId = jsonArtist.getString("id");

		inputStream.close();
		throttle(isThrottle);

		return remoteId;
	}

	protected void throttle(boolean isThrottle) {
		if (!isThrottle) {
			return;
		}

		// Wait for one second to get the artist information
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

	}

	protected String prepareSearchParameter(String artistName) throws UnsupportedEncodingException {
		artistName = StringUtils.trimToEmpty(artistName).toLowerCase();
		if (StringUtils.isEmpty(artistName)) {
			return artistName;
		}

		for (String article : ARTICLES) {
			article = article + " ";
			if (artistName.startsWith(article)) {
				artistName.replaceFirst(article, "");
				artistName += ", " + article;
				break;
			}
		}

		artistName = UriUtils.encodeQueryParam(artistName, Encoding.UTF8.getEncodingString());
		return artistName;
	}

}
