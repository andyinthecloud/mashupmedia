package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.service.PhotoManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/photo/list")
public class ListPhotosController extends BaseController {

	@Autowired
	private PhotoManager photoManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.photos"));
		breadcrumbs.add(breadcrumb);
	}


	@Override
	public String getPageTitleMessageKey() {
		return "list-photos.title";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String handleGetPhotoList(Model model) {
		return "photos-home";
	}

}
