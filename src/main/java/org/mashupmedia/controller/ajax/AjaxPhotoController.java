package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.service.PhotoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ajax/photo")
public class AjaxPhotoController extends AjaxBaseController {

	private final static int TOTAL_PHOTOS = 60;

	@Autowired
	private PhotoManager photoManager;

	@RequestMapping(value = "/load-latest-photos", method = RequestMethod.GET)
	public String handleAjaxGetPhotoList(@RequestParam(value = "pageNumber", required = false) Integer pageNumber, Model model) {
		if (pageNumber == null) {
			pageNumber = 0;
		}
		
		List<Photo> photos = photoManager.getLatestPhotos(pageNumber, TOTAL_PHOTOS);
		model.addAttribute("photos", photos);
				
		if (pageNumber == 0 && photos.isEmpty()) {
			return "ajax/photo/no-photos-found";			
		}
		
		return "ajax/photo/list-photos";
	}
}
