package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController extends BaseController {

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String getHome() {
		return "home";
	}
	
	@Override
	public String getPageTitleMessageKey() {
		return "home.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		// Nothing to do - this is the home page
	}

}
