package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/videos")
public class ListPhotosController extends BaseController {

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

}
