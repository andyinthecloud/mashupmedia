package org.mashupmedia.restful;

import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.util.StringHelper.Encoding;
import org.mashupmedia.web.remote.RemoteImage;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service("discogs")
@Slf4j
public class DiscogsWebServiceImpl extends AbstractCachingMusicWebServiceImpl {
	private static final String PREPEND_CACHE_KEY_ARTIST = "artist_";
	private static final int INTRODUCTION_MAX_LENGTH = 500;


	@Autowired
	private MusicManager musicManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Override
	public RemoteMediaMetaItem getArtistInformation(Artist artist) throws Exception {

		if (artist == null) {
			return null;
		}

		String discogsArtistId = getDiscogArtistId(artist, true);
		RemoteMediaMetaItem remoteMediaMeta = getArtistInformation(discogsArtistId);
		artist.setRemoteId(discogsArtistId);
		musicManager.saveArtist(artist);
		return remoteMediaMeta;
	}

	protected RemoteMediaMetaItem getArtistInformation(String discogsArtistId) throws Exception {
		List<RemoteMediaMetaItem> remoteMediaMetaItems = new ArrayList<RemoteMediaMetaItem>();

		RemoteMediaMetaItem remoteMediaMeta = new RemoteMediaMetaItem();
		remoteMediaMeta.setRemoteId(discogsArtistId);
		remoteMediaMetaItems.add(remoteMediaMeta);

		populateRemoteMediaMetaItems(remoteMediaMetaItems);
		if (remoteMediaMetaItems == null || remoteMediaMetaItems.isEmpty()) {
			return null;
		}

		return remoteMediaMetaItems.get(0);
	}

	protected String prepareRemoteCacheKey(String discogsId) {
		String cacheKey = PREPEND_CACHE_KEY_ARTIST + discogsId;
		return cacheKey;
	}

	protected void populateRemoteMediaMetaItems(List<RemoteMediaMetaItem> remoteMediaMetaItems) throws Exception {

		for (int i = 0; i < remoteMediaMetaItems.size(); i++) {
			RemoteMediaMetaItem remoteMediaMetaItem = remoteMediaMetaItems.get(i);
			String discogsArtistId = remoteMediaMetaItem.getRemoteId();

			if (StringUtils.isBlank(discogsArtistId)) {
				continue;
			}

			String cacheArtistKey = prepareRemoteCacheKey(discogsArtistId);

			RemoteMediaMetaItem cachedRemoteMediaMetaItem = getRemoteMediaItemFromCache(cacheArtistKey);
			if (cachedRemoteMediaMetaItem != null) {
				remoteMediaMetaItems.set(i, cachedRemoteMediaMetaItem);
				continue;
			}

			String artistUrl = "http://api.discogs.com/artists/" + discogsArtistId;
			log.debug("Searching Discogs for artist information using url: " + artistUrl);
			InputStream inputStream = connectionManager.connect(artistUrl);
			String jsonArtistText = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonArtist = objectMapper.readTree(jsonArtistText);

			String nameKey = "name";
			if (!jsonArtist.has(nameKey)) {
				inputStream.close();
				continue;
			}

			if (StringUtils.isBlank(remoteMediaMetaItem.getName())) {
				String name = StringUtils.trimToEmpty(jsonArtist.get("name").asText());
				remoteMediaMetaItem.setName(name);
			}

			String profileKey = "profile";
			String profile = "";
			if (jsonArtist.has(profileKey)) {
				profile = StringUtils.trimToEmpty(jsonArtist.get(profileKey).asText());
				profile = profile.replaceAll("\\[.*?\\]", "");
				profile = profile.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
			}

			remoteMediaMetaItem.setProfile(profile);

			String introduction = profile.replaceAll("\\..*", "");
			introduction += ".";
			if (introduction.length() > INTRODUCTION_MAX_LENGTH) {
				introduction = introduction.substring(0, INTRODUCTION_MAX_LENGTH) + "...";
			}
			remoteMediaMetaItem.setIntroduction(introduction);

			List<RemoteImage> remoteImages = new ArrayList<RemoteImage>();
			String imagesKey = "images";
			if (jsonArtist.has(imagesKey)) {
				JsonNode jsonImages = jsonArtist.get(imagesKey);

				if (jsonImages != null) {
					for (int j = 0; j < jsonImages.size(); j++) {
						JsonNode jsonImage = jsonImages.get(j);
						RemoteImage remoteImage = new RemoteImage();
						String imageUrl = convertImageToProxyUrl(jsonImage.get("resource_url").asText());
						remoteImage.setImageUrl(imageUrl);

						int width = jsonImage.get("width").asInt();
						remoteImage.setWidth(width);

						int height = jsonImage.get("height").asInt();
						remoteImage.setHeight(height);

						String thumbUrl = convertImageToProxyUrl(jsonImage.get("uri150").asText());
						remoteImage.setThumbUrl(thumbUrl);

						remoteImages.add(remoteImage);
					}
				}
			}

			inputStream.close();

			remoteMediaMetaItem.setRemoteImages(remoteImages);
			addRemoteMediaItemToCache(remoteMediaMetaItem);
			throttle(true);
		}

	}

	protected String convertImageToProxyUrl(String url) {
		url = StringUtils.trimToEmpty(url);
		url = "app/proxy/discogs-image/" + url.replaceFirst(".*/", "");
		return url;
	}

	protected String getDiscogArtistId(Artist artist, boolean isThrottle) throws Exception {
		String remoteId = StringUtils.trimToEmpty(artist.getRemoteId());
		if (StringUtils.isNotEmpty(remoteId)) {
			return remoteId;
		}

		String artistName = artist.getName();
		List<RemoteMediaMetaItem> discogsArtistIds = findRemoteMediaMetaItems(artistName, isThrottle, 1);

		if (discogsArtistIds == null || discogsArtistIds.isEmpty()) {
			return "";
		}

		remoteId = discogsArtistIds.get(0).getRemoteId();
		return remoteId;
	}

	protected List<RemoteMediaMetaItem> findRemoteMediaMetaItems(String name, boolean isThrottle, int numberOfArtistIds)
			throws Exception {

		List<RemoteMediaMetaItem> remoteMediaMetaItems = new ArrayList<RemoteMediaMetaItem>();

		name = StringHelper.formatTextToUrlParameter(name);
		if (StringUtils.isEmpty(name)) {
			return remoteMediaMetaItems;
		}

		String searchUrl = "http://api.discogs.com/database/search?q=" + name + "&type=artist";
		log.debug("Searching Discogs for artist id using url: " + searchUrl);
		InputStream inputStream = connectionManager.connect(searchUrl);
		if (inputStream == null) {
			log.error("Could not connect to Discogs web service.");
			throw new ConnectException("Could not connect to Discogs web service.");
		}
		String jsonSearchResults = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonObject = objectMapper.readTree(jsonSearchResults);
		JsonNode jsonArray = jsonObject.get("results");
		if (jsonArray == null) {
			return remoteMediaMetaItems;
		}
		int size = jsonArray.size();
		if (size == 0) {
			return remoteMediaMetaItems;
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonNode jsonArtist = jsonArray.get(i);
			log.debug("Found jsonObject: " + jsonArtist.asText());
			String discogsArtistId = jsonArtist.get("id").asText();
			RemoteMediaMetaItem remoteMediaMeta = new RemoteMediaMetaItem();
			remoteMediaMeta.setRemoteId(discogsArtistId);

			if (!remoteMediaMetaItems.contains(remoteMediaMeta)) {
				String title = jsonArtist.get("title").asText();
				remoteMediaMeta.setName(title);
				remoteMediaMetaItems.add(remoteMediaMeta);

				if (remoteMediaMetaItems.size() >= numberOfArtistIds) {
					break;
				}
			}
		}

		inputStream.close();
		throttle(isThrottle);
		return remoteMediaMetaItems;
	}

	@Override
	public List<RemoteMediaMetaItem> searchArtist(String artistName) throws Exception {
		List<RemoteMediaMetaItem> remoteMediaMetaItems = findRemoteMediaMetaItems(artistName, true, 5);
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

}
