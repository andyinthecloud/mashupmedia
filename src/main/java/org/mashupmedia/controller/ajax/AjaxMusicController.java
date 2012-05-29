package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.service.MusicManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/ajax/music")
public class AjaxMusicController {
	
	@Autowired
	private MusicManager musicManager;
	
	@RequestMapping(value = "/random-albums", method = RequestMethod.GET)
	public String getMusic(Model model) {
		List<Album> albums = musicManager.getRandomAlbums(30);
		model.addAttribute("albums", albums);
		return "ajax/music/random-albums";
		
	}

}
