package org.mashupmedia.controller;

import java.net.ConnectException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.restful.VideoWebService;
import org.mashupmedia.service.VideoManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.VideoPage;
import org.mashupmedia.web.remote.RemoteImage;
import org.mashupmedia.web.remote.RemoteImage.RemoteImageType;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/video")
@Slf4j
public class VideoController extends BaseController {

	@Autowired
	private VideoManager videoManager;

	@Autowired
	@Lazy
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@Autowired
	@Qualifier("themoviedb")
	private VideoWebService videoWebService;


	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.videos"), "/video/videos");
		breadcrumbs.add(breadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "video.title";
	}

	@RequestMapping(value = "/show/{videoId}", method = RequestMethod.GET)
	public String handleGetVideo(
			@PathVariable("videoId") Long videoId,
			@RequestParam(value = "reencode", required = false) Boolean isReencode, @RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			Model model, HttpServletRequest request) {

		Video video = videoManager.getVideo(videoId);

		try {
			processReencodeRequest(isReencode, video);
		} catch (MediaItemEncodeException e) {
			log.error("Error encoding video", e);
		}

		List<Breadcrumb> breadcrumbs = populateBreadcrumbs();
		Breadcrumb breadcrumb = new Breadcrumb(video.getDisplayTitle());
		breadcrumbs.add(breadcrumb);
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);

		String headPageTitle = populateHeadPageTitle() + " "
				+ video.getDisplayTitle();
		model.addAttribute(MODEL_KEY_HEAD_PAGE_TITLE, headPageTitle);

		VideoPage videoPage = new VideoPage();

		videoPage.setVideo(video);

		RemoteMediaMetaItem remoteMediaMetaItem = getRemoteMediaMetaItem(
				videoId, request);
		if (remoteMediaMetaItem != null && !video.isIgnoreRemoteContent()) {
			video.setSummary(remoteMediaMetaItem.getIntroduction());
		}
		videoPage.setRemoteMediaMetaItem(remoteMediaMetaItem);

		String posterUrl = "/images/no-video-poster.png";
		RemoteImage remoteImage = remoteMediaMetaItem
				.getRemoteImage(RemoteImageType.BACKDROP);
		if (remoteImage != null) {
			posterUrl = remoteImage.getImageUrl();
		}
		videoPage.setPosterUrl(posterUrl);

		model.addAttribute("videoPage", videoPage);
		
		String pagePath = getPath(isFragment, "video.show");
		return pagePath;
	}

	protected void processReencodeRequest(Boolean isReencode, Video video) throws MediaItemEncodeException {
		if (isReencode == null || video == null) {
			return;
		}

		if (isReencode == false) {
			return;
		}

		encodeMediaItemTaskManager.processMediaItemForEncoding(video,
				MediaContentType.MP4);
	}

	protected RemoteMediaMetaItem getRemoteMediaMetaItem(long videoId,
			HttpServletRequest request) {
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
			log.error(
					"Error connecting to the remote web service, site may be unavailable or check proxy are incorrect",
					e);

			String contextUrl = WebHelper.getContextUrl(request);
			String introductionMessage = MessageHelper
					.getRemoteConnectionError(contextUrl);
			remoteMediaMetaItem.setIntroduction(introductionMessage);
			remoteMediaMetaItem.setError(true);
		} catch (Exception e) {
			log.error("Error getting remote video information", e);
			remoteMediaMetaItem.setIntroduction(MessageHelper
					.getMessage("remote.error"));
			remoteMediaMetaItem.setError(true);
		}

		return remoteMediaMetaItem;

	}

	@Override
	public String populateMediaType() {
		return "video";
	}

	@RequestMapping(value = "/videos", method = RequestMethod.GET)
	public String handleGetVideoList(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			Model model) {
		List<Video> videos = videoManager.getVideos();
		model.addAttribute("videos", videos);

		String pagePath = getPath(isFragment, "video.videos");
		return pagePath;
	}
	

}
