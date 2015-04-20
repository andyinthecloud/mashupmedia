package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LogOutSuccessController extends BaseController {

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		// Nothing to do, login page
	}

	@Override
	public String getPageTitleMessageKey() {
		return "log-out.title";
	}
	
	@RequestMapping(value = "/log-out-success", method = RequestMethod.GET)
	public String getLogOut() {
		return "log-out-success";
	}

}
