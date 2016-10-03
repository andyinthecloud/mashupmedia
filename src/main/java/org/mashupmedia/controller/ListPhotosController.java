package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.service.PhotoManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/photo/list")
public class ListPhotosController extends BaseController {

	public static int MAXIMUM_PHOTOS = 50;
	private static String ATTRIBUTE_PHOTOS = "photos";

	@Autowired
	private PhotoManager photoManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.photos"));
		breadcrumbs.add(breadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "list-photos.title";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String handleGetPhotoList(
			@RequestParam(value = MashUpMediaConstants.PARAM_IS_APPEND, required = false) Boolean isAppend,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@RequestParam(value = PARAM_PAGE_NUMBER, required = false) Integer pageNumber, Model model) {
		List<Photo> photos = photoManager.getLatestPhotos(pageNumber, MAXIMUM_PHOTOS);
		model.addAttribute(ATTRIBUTE_PHOTOS, photos);
		String pagePath = getPath(isFragment, "photos/list-photos");
		return pagePath;
	}

}
