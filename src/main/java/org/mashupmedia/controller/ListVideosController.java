package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.model.media.Video;
import org.mashupmedia.service.VideoManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/videos")
public class ListVideosController extends BaseController {

	@Autowired
	private VideoManager videoManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.video"), "/app/video");
		breadcrumbs.add(breadcrumb);		
	}

	@Override
	public String getPageTitleMessageKey() {
		return "list-videos.title";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String handleGetVideoList(Model model) {
		List<Video> videos = videoManager.getVideos();
		model.addAttribute("videos", videos);
		return "list-videos";
	}
}
