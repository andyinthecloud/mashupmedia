package org.mashupmedia.controller;

import java.net.ConnectException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.model.media.Video;
import org.mashupmedia.restful.VideoWebService;
import org.mashupmedia.service.VideoManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.WebHelper.MediaContentType;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.VideoPage;
import org.mashupmedia.web.remote.RemoteImage;
import org.mashupmedia.web.remote.RemoteImage.RemoteImageType;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/video")
public class VideoController extends BaseController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private VideoManager videoManager;

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@Autowired
	@Qualifier("themoviedb")
	private VideoWebService videoWebService;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.videos"), "/app/videos");
		breadcrumbs.add(breadcrumb);

	}

	@Override
	public String getPageTitleMessageKey() {
		return "video.title";
	}

	@RequestMapping(value = "/show/{videoId}", method = RequestMethod.GET)
	public String handleGetVideo(@PathVariable("videoId") Long videoId, @RequestParam(value = "reencode", required = false) Boolean isForceReencode, Model model) {

		Video video = videoManager.getVideo(videoId);
		
		if (isForceReencode == null) {
			isForceReencode = false;
		}

		if (isForceReencode) {
			video.setEncodeStatusType(EncodeStatusType.OVERRIDE);
			videoManager.saveVideo(video);
			encodeMediaItemTaskManager.encodeMediaItem(videoId);
		}

		List<Breadcrumb> breadcrumbs = populateBreadcrumbs();
		Breadcrumb breadcrumb = new Breadcrumb(video.getDisplayTitle());
		breadcrumbs.add(breadcrumb);
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);
		
		String headPageTitle = populateHeadPageTitle() + " " + video.getDisplayTitle();
		model.addAttribute(MODEL_KEY_HEAD_PAGE_TITLE, headPageTitle);
		
		VideoPage videoPage = new VideoPage();
		videoPage.setSuppliedVideoFormats(new String[] { MediaContentType.WEBM.getjPlayerContentType() });
		videoPage.setVideo(video);

		RemoteMediaMetaItem remoteMediaMetaItem = getRemoteMediaMetaItem(videoId);
		if (remoteMediaMetaItem != null && !video.isIgnoreRemoteContent()) {
			video.setSummary(remoteMediaMetaItem.getIntroduction());
		}		
		videoPage.setRemoteMediaMetaItem(remoteMediaMetaItem);

		String posterUrl = "/images/no-video-poster.png";
		RemoteImage remoteImage = remoteMediaMetaItem.getRemoteImage(RemoteImageType.BACKDROP);
		if (remoteImage != null) {
			posterUrl = remoteImage.getImageUrl();
		}
		videoPage.setPosterUrl(posterUrl);

		model.addAttribute("videoPage", videoPage);
		return "videos/show";
	}

	protected RemoteMediaMetaItem getRemoteMediaMetaItem(long videoId) {
		Video video = videoManager.getVideo(videoId);

		RemoteMediaMetaItem remoteMediaMetaItem = new RemoteMediaMetaItem();
		try {
			remoteMediaMetaItem = videoWebService.getVideoInformation(video);
			String remoteId = remoteMediaMetaItem.getRemoteId();

			if (StringUtils.isNotBlank(remoteId)) {
				video.setRemoteId(remoteId);
				videoManager.saveVideo(video);

			}
		} catch (ConnectException e) {
			logger.error(
					"Error connecting to the remote web service, site may be unavailable or check proxy are incorrect",
					e);
			remoteMediaMetaItem.setIntroduction(MessageHelper.getMessage("remote.connection.error"));
			remoteMediaMetaItem.setError(true);
		} catch (Exception e) {
			logger.error("Error getting remote video information", e);
			remoteMediaMetaItem.setIntroduction(MessageHelper.getMessage("remote.error"));
			remoteMediaMetaItem.setError(true);
		}

		return remoteMediaMetaItem;

	}

	@RequestMapping(value = "/play/{videoId}", method = RequestMethod.GET)
	public String handlePlayVideo(@PathVariable("videoId") Long videoId,
			 Model model) {
		Video video = videoManager.getVideo(videoId);

		EncodeStatusType encodeStatusType = video.getEncodeStatusType();
		if (encodeStatusType != EncodeStatusType.ENCODED) {
			encodeMediaItemTaskManager.encodeMediaItem(videoId);
		}

		return "redirect:/app/streaming/media/encoded/" + videoId;
	}

}
