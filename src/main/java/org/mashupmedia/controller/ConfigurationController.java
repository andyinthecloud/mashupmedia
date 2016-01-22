package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.NetworkPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfigurationController extends BaseController {

	@Autowired
	private ConfigurationManager configurationManager;

	private final static String REDIRECT_PATH = "configuration";
	private final static String PAGE_PATH = "/configuration";

	@Override
	public String getPageTitleMessageKey() {
		return "configuration.title";
	}
	
	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"));
		breadcrumbs.add(breadcrumb);
	}

	@RequestMapping(value = PAGE_PATH, method = RequestMethod.GET)
	public String getConfiguration(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {
		String path = getPath(isFragment, REDIRECT_PATH);
		return path;				
	}


	@RequestMapping(value = PAGE_PATH, method = RequestMethod.POST)
	public String processConfiguration(@ModelAttribute("configurationPage") NetworkPage configurationPage, Model model, BindingResult result) {
		String proxyUrl = configurationPage.getProxyUrl();
		configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_URL, proxyUrl);
		return "redirect:" + REDIRECT_PATH;
	}

}
