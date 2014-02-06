package org.mashupmedia.restful;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.metamodel.relational.Loggable;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.util.StringHelper.Encoding;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("rovi")
public class RoviWebServiceImpl implements MediaWebService {
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ConnectionManager connectionManager;


	private final static String ROVI_SEARCH_KEY = "rovi.search.key";
	private final static String ROVI_SEARCH_SECRET = "rovi.search.secret";

	protected String getMd5Hash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		StringBuilder roviBuilder = new StringBuilder();
		roviBuilder.append(MessageHelper.getMessage(ROVI_SEARCH_KEY));
		roviBuilder.append(MessageHelper.getMessage(ROVI_SEARCH_SECRET));
		roviBuilder.append(System.currentTimeMillis());

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] digest = messageDigest.digest(roviBuilder.toString().getBytes(Encoding.UTF8.getEncodingString()));
		String md5Hash = new String(digest);
		return md5Hash;
	}

	@Override
	public RemoteMediaMetaItem getArtistInformation(Artist artist) throws Exception {

		if (artist == null) {
			return null;
		}

		StringBuilder urlBuilder = new StringBuilder(
				"http://api.rovicorp.com/data/v1.1/name/info?country=US&language=en&format=json");

		String remoteId = StringUtils.trimToEmpty(artist.getRemoteId());
		if (StringUtils.isNotEmpty(remoteId)) {
			urlBuilder.append("&nameid=");
			urlBuilder.append(remoteId);
		} else {
			String artistName = artist.getName();
			String artistNameForUrl = StringHelper.formatTextToUrlParameter(artistName);
			urlBuilder.append("&name=");
			urlBuilder.append(artistNameForUrl);
		}

		urlBuilder.append("&apikey=");
		urlBuilder.append(MessageHelper.getMessage(ROVI_SEARCH_KEY));
		urlBuilder.append("&sig=");
		urlBuilder.append(getMd5Hash());

		String url = urlBuilder.toString();
		logger.debug("Searching Rovi for artist information using url: " + url);
		InputStream inputStream = connectionManager.connect(url);
		String jsonArtistText = IOUtils.toString(inputStream, Encoding.UTF8.getEncodingString());
		JSONObject jsonArtist = JSONObject.fromObject(jsonArtistText);
		
		RemoteMediaMetaItem remoteMediaMetaItem = new RemoteMediaMetaItem();

		return remoteMediaMetaItem;
	}

	@Override
	public List<RemoteMediaMetaItem> searchArtist(String artistName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
