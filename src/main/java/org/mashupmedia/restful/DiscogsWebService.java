package org.mashupmedia.restful;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.StringHelper.Encoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

@Service
public class DiscogsWebService {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MusicManager musicManager;

	private static String[] ARTICLES = { "the", "a" };

	@Autowired
	private ConnectionManager connectionManager;

	public String getArtistInformation(Artist artist) throws IOException {

		if (artist == null) {
			return null;
		}

		long discogId = getDiscogArtistId(artist, true);

		if (discogId == 0) {
			return null;
		}

		artist.setDiscogId(discogId);
		musicManager.saveArtist(artist);

		// String artistUrl = "http://api.discogs.com/artists/2517607";
		String artistUrl = "http://api.discogs.com/artists/" + discogId;
		logger.debug("Searching Discogs for artist information using url: " + artistUrl);
		InputStream inputStream = connectionManager.connect(artistUrl);
		String jsonArtist = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		inputStream.close();

		return jsonArtist;
	}

	protected long getDiscogArtistId(Artist artist, boolean isThrottle) throws IOException {
		long discogId = artist.getDiscogId();
		if (discogId > 0) {
			return discogId;
		}

		String name = artist.getName();
		name = prepareSearchParameter(name);
		if (StringUtils.isEmpty(name)) {
			return 0;
		}

		String searchUrl = "http://api.discogs.com/database/search?q=" + name + ",the&type=artist";
		logger.debug("Searching Discogs for artist id using url: " + searchUrl);
		InputStream inputStream = connectionManager.connect(searchUrl);
		String jsonSearchResults = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		inputStream.close();
		
		throttle(isThrottle);

		return discogId;
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
