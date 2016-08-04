package org.mashupmedia.controller.remote;

import java.net.ConnectException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.controller.ajax.AjaxBaseController;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.restful.MusicWebService;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.web.remote.RemoteImage;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/remote/music")
public class RemoteMusicController extends AjaxBaseController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MusicManager musicManager;

	@Autowired
	@Qualifier("lastFm")
	private MusicWebService musicWebService;

	@RequestMapping(value = "/artist/thumb/{artistId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public String getRemoteArtistThumbnail(@PathVariable("artistId") Long artistId, HttpServletRequest request) {

		RemoteMediaMetaItem remoteMediaMetaItem = getRemoteArtistMeta(artistId, request);
		List<RemoteImage> remoteImages = remoteMediaMetaItem.getRemoteImages();
		if (remoteImages == null || remoteImages.isEmpty()) {
			return null;
		}

		RemoteImage remoteImage = remoteImages.get(0);
		String thumbUrl = remoteImage.getThumbUrl();
		return "forward:/" + thumbUrl;
	}

	@RequestMapping(value = "/artist/{artistId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody RemoteMediaMetaItem getArtistInformation(@PathVariable("artistId") Long artistId,
			HttpServletRequest request) {
		RemoteMediaMetaItem remoteMediaMetaItem = getRemoteArtistMeta(artistId, request);
		return remoteMediaMetaItem;
	}

	protected RemoteMediaMetaItem getRemoteArtistMeta(long artistId, HttpServletRequest request) {

		Artist artist = musicManager.getArtist(artistId, false);

		RemoteMediaMetaItem remoteMediaMeta = new RemoteMediaMetaItem();
		try {
			remoteMediaMeta = musicWebService.getArtistInformation(artist);
			String remoteId = remoteMediaMeta.getRemoteId();

			if (StringUtils.isNotBlank(remoteId)) {
				artist.setRemoteId(remoteId);
				musicManager.saveArtist(artist);

			}
		} catch (ConnectException e) {
			logger.error(
					"Error connecting to the remote web service, site may be unavailable or check proxy are incorrect",
					e);
			String contextUrl = WebHelper.getContextUrl(request);
			String introductionMessage = MessageHelper.getRemoteConnectionError(contextUrl);
			remoteMediaMeta.setIntroduction(introductionMessage);
			remoteMediaMeta.setError(true);
		} catch (Exception e) {
			logger.error("Error getting remote artist information", e);
			remoteMediaMeta.setIntroduction(MessageHelper.getMessage("remote.error"));
			remoteMediaMeta.setError(true);
		}

		return remoteMediaMeta;
	}

}
