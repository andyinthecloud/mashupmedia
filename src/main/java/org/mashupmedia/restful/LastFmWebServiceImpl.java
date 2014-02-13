package org.mashupmedia.restful;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mashupmedia.model.media.Artist;
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

@Service("lastFm")
public class LastFmWebServiceImpl extends AbstractMediaWebServiceImpl {

	@Autowired
	private ConnectionManager connectionManager;

	private final static String LASTFM_API_KEY = "lastfm.api.key";

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

		StringBuilder urlBuilder = new StringBuilder("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo");
		String artistName = artist.getName();
		String artistNameForUrl = StringHelper.formatTextToUrlParameter(artistName);

		if (StringUtils.isNotEmpty(remoteId)) {
			urlBuilder.append("&mbid=");
			urlBuilder.append(remoteId);
		} else {
			urlBuilder.append("&artist=");
			urlBuilder.append(artistNameForUrl);
		}

		urlBuilder.append("&api_key=");
		urlBuilder.append(MessageHelper.getMessage(LASTFM_API_KEY));

		String artistInfoUrl = urlBuilder.toString();

		InputStream artistInfoInputStream = connectionManager.connect(artistInfoUrl);
		Document artistInfoDocument = XmlHelper.createDocument(artistInfoInputStream);
		artistInfoInputStream.close();

		remoteMediaMetaItem = new RemoteMediaMetaItem();

		remoteMediaMetaItem.setDate(new Date());

		String remoteArtistName = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/name/text()");
		remoteMediaMetaItem.setName(remoteArtistName);

		remoteId = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/mbid/text()");
		remoteMediaMetaItem.setRemoteId(remoteId);

		String introduction = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/bio/summary/text()");
		remoteMediaMetaItem.setIntroduction(introduction);

		String profile = XmlHelper.getTextFromElement(artistInfoDocument, "/lfm/artist/bio/content/text()");
		remoteMediaMetaItem.setProfile(profile);

		List<RemoteImage> remoteImages = getRemoteImages(artistNameForUrl);
		remoteMediaMetaItem.setRemoteImages(remoteImages);

		addRemoteMediaItemToCache(remoteMediaMetaItem);

		return remoteMediaMetaItem;
	}

	private List<RemoteImage> getRemoteImages(String artistNameForUrl) throws IOException {
		String remoteImageUrl = new String("http://www.last.fm/music/" + artistNameForUrl + "/+images");
		InputStream inputStream = connectionManager.connect(remoteImageUrl);
		String html = StringHelper.convertToText(inputStream);

		List<RemoteImage> remoteImages = new ArrayList<RemoteImage>();

		org.jsoup.nodes.Document document = Jsoup.parse(html);
		Elements elements = document.select("#pictures li");
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
	public List<RemoteMediaMetaItem> searchArtist(String artistName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
