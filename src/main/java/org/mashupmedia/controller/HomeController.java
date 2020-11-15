package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController extends BaseController {

	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment) {		
		String path = getPath(isFragment, "home");
		return path;
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
