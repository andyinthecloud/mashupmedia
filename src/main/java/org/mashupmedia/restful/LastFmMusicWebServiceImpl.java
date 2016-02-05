package org.mashupmedia.restful;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.ProxyHelper;
import org.mashupmedia.util.StringHelper;
import org.mashupmedia.util.XmlHelper;
import org.mashupmedia.web.remote.RemoteImage;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service("lastFm")
public class LastFmMusicWebServiceImpl extends AbstractCachingMusicWebServiceImpl {
	private final static String LASTFM_API_KEY = "lastfm.api.key";
	private final static String LASTFM_API_ROOT_URL = "http://ws.audioscrobbler.com/2.0/";
	private final static String LASTFM_API_ARTIST_INFO_URL = LASTFM_API_ROOT_URL + "?method=artist.getinfo";
	private final static String LASTFM_API_ARTIST_SEARCH_URL = LASTFM_API_ROOT_URL + "?method=artist.search";

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ConnectionManager connectionManager;


	@Override
	public RemoteMediaMetaItem getArtistInformation(Artist artist) throws Exception {

		if (artist == null) {
			return null;
		}

		String remoteId = StringUtils.trimToEmpty(artist.getRemoteId());
		RemoteMediaMetaItem remoteMediaMetaItem = getRemoteMediaItemFromCache(remoteId);
		if (remoteMediaMetaItem != null) {
			return remoteMediaMetaItem;
		}

		String artistName = artist.getName();

		Document artistInfoDocument = null;
		if (StringUtils.isNotEmpty(remoteId)) {
			artistInfoDocument = getArtistInfoDocumentByRemoteId(remoteId);
			String status = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/@status");
			if (status.equalsIgnoreCase("failed")) {
				artistInfoDocument = getArtistInfoDocumentByArtistName(artistName);
			}
		} else {
			artistInfoDocument = getArtistInfoDocumentByArtistName(artistName);
		}

		remoteMediaMetaItem = new RemoteMediaMetaItem();

		remoteMediaMetaItem.setDate(new Date());

		String remoteArtistName = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/name/text()");
		remoteMediaMetaItem.setName(remoteArtistName);

		remoteId = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/mbid/text()");
		remoteMediaMetaItem.setRemoteId(remoteId);

		String introduction = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/bio/summary/text()");
		introduction = processText(introduction);
		remoteMediaMetaItem.setIntroduction(introduction);

		String profile = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/bio/content/text()");
		profile = processText(profile);
		remoteMediaMetaItem.setProfile(profile);

		List<RemoteImage> remoteImages = getRemoteImages(artistName);
		remoteMediaMetaItem.setRemoteImages(remoteImages);

		addRemoteMediaItemToCache(remoteMediaMetaItem);

		return remoteMediaMetaItem;
	}
	
	protected String processText(String text) {
		String processedText = StringUtils.trimToEmpty(text);
		processedText = processedText.replaceAll("<a\\s", "<a target=\"lastfm\" ");
		processedText = processedText.replaceAll("\\r?\\n|\\r", " ");
		processedText = processedText.replaceAll("\\s{2}?", " ");
		return processedText;
		
	}

	protected Document getArtistInfoDocumentByArtistName(String artistName) throws ParserConfigurationException,
			SAXException, IOException {
		StringBuilder urlBuilder = new StringBuilder(LASTFM_API_ARTIST_INFO_URL);
		String artistNameForUrl = StringHelper.formatTextToUrlParameter(artistName);
		urlBuilder.append("&artist=");
		urlBuilder.append(artistNameForUrl);

		urlBuilder.append("&api_key=");
		urlBuilder.append(MessageHelper.getMessage(LASTFM_API_KEY));

		String artistInfoUrl = urlBuilder.toString();

		InputStream artistInfoInputStream = connectionManager.connect(artistInfoUrl);
		if (artistInfoInputStream == null) {
			throw new ConnectException("Unable to connect to " + urlBuilder.toString());
		}
		
		
		Document artistInfoDocument = XmlHelper.createDocument(artistInfoInputStream);
		IOUtils.closeQuietly(artistInfoInputStream);
		return artistInfoDocument;

	}

	protected Document getArtistInfoDocumentByRemoteId(String remoteId) throws ParserConfigurationException,
			SAXException, IOException {
		StringBuilder urlBuilder = new StringBuilder(LASTFM_API_ARTIST_INFO_URL);
		urlBuilder.append("&mbid=");
		urlBuilder.append(remoteId);
		urlBuilder.append("&api_key=");
		urlBuilder.append(MessageHelper.getMessage(LASTFM_API_KEY));

		String artistInfoUrl = urlBuilder.toString();

		InputStream artistInfoInputStream = connectionManager.connect(artistInfoUrl);
		if (artistInfoInputStream == null) {
			logger.error("Unable to connect to " + urlBuilder.toString());
			return null;
		}
		
		Document artistInfoDocument = XmlHelper.createDocument(artistInfoInputStream);
		IOUtils.closeQuietly(artistInfoInputStream);
		return artistInfoDocument;

	}

	private List<RemoteImage> getRemoteImages(String artistName) throws IOException {
		String artistNameForUrl = StringHelper.formatTextToUrlParameter(artistName);
		String remoteImageUrl = new String("http://www.last.fm/music/" + artistNameForUrl + "/+images");
		
		List<RemoteImage> remoteImages = new ArrayList<RemoteImage>();
		InputStream inputStream = connectionManager.connect(remoteImageUrl);
		if (inputStream == null) {
			throw new ConnectException("Unable to connect to " + remoteImageUrl);
		}
		
		String html = StringHelper.convertToText(inputStream);


		org.jsoup.nodes.Document document = Jsoup.parse(html);
		Elements elements = document.select("ul.image-list li");
		int imageSize = elements.size();
		if (imageSize > MAX_IMAGES) {
			imageSize = MAX_IMAGES;
		}

		for (int i = 0; i < imageSize; i++) {
			Element element = elements.get(i);
			RemoteImage remoteImage = new RemoteImage();
			Elements imageElements = element.select("a img");
			remoteImage.setWidth(IMAGE_MAX_WIDTH);
			String thumbUrl = StringUtils.trimToEmpty(imageElements.attr("src"));
			String proxyThumbUrl = ProxyHelper.formatUrlForProxy(thumbUrl);
			remoteImage.setThumbUrl(proxyThumbUrl);

			String imageUrl = thumbUrl.replaceFirst("/\\d{3}s/", "/_/");
			String proxyImageUrl = ProxyHelper.formatUrlForProxy(imageUrl);
			remoteImage.setImageUrl(proxyImageUrl);
			remoteImages.add(remoteImage);

		}

		return remoteImages;
	}

	@Override
	public List<RemoteMediaMetaItem> searchArtist(String text) throws Exception {

		StringBuilder urlBuilder = new StringBuilder(LASTFM_API_ARTIST_SEARCH_URL);
		urlBuilder.append("&artist=");
		String artistNameForUrl = StringHelper.formatTextToUrlParameter(text);
		urlBuilder.append(artistNameForUrl);
		urlBuilder.append("&api_key=");
		urlBuilder.append(MessageHelper.getMessage(LASTFM_API_KEY));

		String artistInfoUrl = urlBuilder.toString();

		List<RemoteMediaMetaItem> remoteMediaMetaItems = new ArrayList<RemoteMediaMetaItem>();
		InputStream artistSearchInputStream = connectionManager.connect(artistInfoUrl);
		if (artistSearchInputStream == null) {
			throw new ConnectException("Unable to connect to " + urlBuilder.toString());
		}
		
		Document artistSearchDocument = XmlHelper.createDocument(artistSearchInputStream);
		IOUtils.closeQuietly(artistSearchInputStream);
		
		
		NodeList nodeList = XmlHelper.getNodeListFromElement(artistSearchDocument, "/lfm/results/artistmatches/artist");
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NodeList artistNodeList = node.getChildNodes();
			
			
			RemoteMediaMetaItem remoteMediaMetaItem = new RemoteMediaMetaItem();
			String artistName = XmlHelper.getTextContentFromNodeName(artistNodeList, "name");
			remoteMediaMetaItem.setName(artistName);
			String remoteId = XmlHelper.getTextContentFromNodeName(artistNodeList, "mbid");
			remoteMediaMetaItem.setRemoteId(remoteId);
			
			remoteMediaMetaItems.add(remoteMediaMetaItem);
		}
		
		return remoteMediaMetaItems;
	}



}
