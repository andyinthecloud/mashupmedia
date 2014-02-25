package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.model.media.Video;
import org.mashupmedia.service.VideoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/video")
public class VideoController {

	@Autowired
	private VideoManager videoManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public String getMusic(Model model) {
		List<Video> videos = videoManager.getVideos();
		model.addAttribute("videos", videos);
		return "videos";
	}
}
