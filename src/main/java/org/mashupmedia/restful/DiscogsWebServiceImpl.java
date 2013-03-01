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
public class DiscogsWebServiceImpl implements DiscogsWebService {
	private static final String PREPEND_CACHE_KEY_ARTIST = "artist_";

	private Logger logger = Logger.getLogger(getClass());

	private Map<String, RemoteMediaMeta> remoteMediaCache = new HashMap<String, RemoteMediaMeta>();
	public static int DEFAULT_CACHE_SECONDS = 86400;

	@Autowired
	private MusicManager musicManager;

	private static String[] ARTICLES = { "the", "a" };

	@Autowired
	private ConnectionManager connectionManager;

	@Override
	public RemoteMediaMeta getArtistInformation(Artist artist) throws IOException {

		if (artist == null) {
			return null;
		}

		String discogsArtistId = getDiscogArtistId(artist, true);
		RemoteMediaMeta remoteMediaMeta = getDiscogsArtistMeta(discogsArtistId);
		artist.setRemoteId(discogsArtistId);
		musicManager.saveArtist(artist);
		return remoteMediaMeta;
	}

	@Override
	public RemoteMediaMeta getDiscogsArtistMeta(String discogsArtistId) throws IOException {
		List<String> discogsArtistIds = new ArrayList<String>();
		discogsArtistIds.add(discogsArtistId);
		List<RemoteMediaMeta> remoteMediaMetaItems = getDiscogsArtistMetaItems(discogsArtistIds);
		if (remoteMediaMetaItems == null || remoteMediaMetaItems.isEmpty()) {
			return null;
		}

		return remoteMediaMetaItems.get(0);
	}

	protected List<RemoteMediaMeta> getDiscogsArtistMetaItems(List<String> discogsArtistIds) throws IOException {

		List<RemoteMediaMeta> remoteMediaMetaItems = new ArrayList<RemoteMediaMeta>();

		for (String discogsArtistId : discogsArtistIds) {
			if (StringUtils.isBlank(discogsArtistId)) {
				continue;
			}

			String cacheArtistKey = PREPEND_CACHE_KEY_ARTIST + discogsArtistId;
			Date date = new Date();
			RemoteMediaMeta remoteMediaMeta = remoteMediaCache.get(cacheArtistKey);
			if (remoteMediaMeta != null) {
				long seconds = (date.getTime() - remoteMediaMeta.getDate().getTime()) / 1000;
				if (DEFAULT_CACHE_SECONDS > seconds) {
					remoteMediaMetaItems.add(remoteMediaMeta);
					continue;
				}

				remoteMediaCache.remove(remoteMediaMeta);

			}

			String artistUrl = "http://api.discogs.com/artists/" + discogsArtistId;
			logger.debug("Searching Discogs for artist information using url: " + artistUrl);
			InputStream inputStream = connectionManager.connect(artistUrl);
			String jsonArtistText = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
			JSONObject jsonArtist = JSONObject.fromObject(jsonArtistText);

			String profileKey = "profile";
			if (!jsonArtist.containsKey(profileKey)) {
				continue;
			}

			remoteMediaMeta = new RemoteMediaMeta();
			String profile = StringUtils.trimToEmpty(jsonArtist.getString(profileKey));
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
					String imageUrl = convertImageToProxyUrl(jsonImage.getString("resource_url"));
					remoteImage.setImageUrl(imageUrl);

					int width = jsonImage.getInt("width");
					remoteImage.setWidth(width);

					int height = jsonImage.getInt("height");
					remoteImage.setHeight(height);

					String thumbUrl = convertImageToProxyUrl(jsonImage.getString("uri150"));
					remoteImage.setThumbUrl(thumbUrl);

					remoteImages.add(remoteImage);
				}
			}

			inputStream.close();

			remoteMediaMeta.setRemoteImages(remoteImages);
			remoteMediaCache.put(cacheArtistKey, remoteMediaMeta);
			remoteMediaMetaItems.add(remoteMediaMeta);
			throttle(true);
		}

		return remoteMediaMetaItems;
	}

	protected String convertImageToProxyUrl(String url) {
		url = StringUtils.trimToEmpty(url);
		url = "/app/proxy/discogs-image/" + url.replaceFirst(".*/", "");
		return url;
	}

	protected String getDiscogArtistId(Artist artist, boolean isThrottle) throws IOException {
		String remoteId = StringUtils.trimToEmpty(artist.getRemoteId());
		if (StringUtils.isNotEmpty(remoteId)) {
			return remoteId;
		}

		String artistName = artist.getName();
		List<String> discogsArtistIds = getDiscogArtistId(artistName, isThrottle, 1);

		if (discogsArtistIds == null || discogsArtistIds.isEmpty()) {
			return "";
		}

		remoteId = discogsArtistIds.get(0);
		return remoteId;
	}

	protected List<String> getDiscogArtistId(String name, boolean isThrottle, int numberOfArtistIds) throws IOException {

		List<String> discogsArtistIds = new ArrayList<String>();

		name = prepareSearchParameter(name);
		if (StringUtils.isEmpty(name)) {
			return discogsArtistIds;
		}

		String searchUrl = "http://api.discogs.com/database/search?q=" + name + "&type=artist";
		logger.debug("Searching Discogs for artist id using url: " + searchUrl);
		InputStream inputStream = connectionManager.connect(searchUrl);
		String jsonSearchResults = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		JSONObject jsonObject = JSONObject.fromObject(jsonSearchResults);
		JSONArray jsonArray = jsonObject.getJSONArray("results");
		if (jsonArray == null) {
			return discogsArtistIds;
		}
		int size = jsonArray.size();
		if (size == 0) {
			return discogsArtistIds;
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonArtist = jsonArray.getJSONObject(i);
			logger.debug("Found jsonObject: " + jsonArtist.toString(2));
			String discogsArtistId = jsonArtist.getString("id");
			if (!discogsArtistId.contains(discogsArtistId)) {
				discogsArtistIds.add(discogsArtistId);
				if (discogsArtistIds.size() >= numberOfArtistIds) {
					break;
				}
			}
		}

		inputStream.close();
		throttle(isThrottle);
		return discogsArtistIds;
	}

	@Override
	public List<RemoteMediaMeta> searchArtist(String artistName) throws IOException {
		List<String> discogsArtistIds = getDiscogArtistId(artistName, true, 5);
		List<RemoteMediaMeta> remoteMediaMetaItems = getDiscogsArtistMetaItems(discogsArtistIds);
		return remoteMediaMetaItems;
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
