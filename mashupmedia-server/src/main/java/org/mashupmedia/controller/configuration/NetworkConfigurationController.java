package org.mashupmedia.controller.configuration;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.NetworkPageValidator;
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
public class NetworkConfigurationController extends BaseController {

	private final static String PAGE_NAME = "network";
	private final static String PAGE_PATH = "configuration." + PAGE_NAME;
	private final static String PAGE_URL = "/configuration/" + PAGE_NAME;
	private final static String PAGE_FRAGMENT_PATH = PAGE_PATH  + FRAGMENT_APPEND_PATH;

	@Autowired
	private ConfigurationManager configurationManager;

	@Override
	public String getPageTitleMessageKey() {
		return "network.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.network"));
		breadcrumbs.add(networkBreadcrumb);
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.GET)
	public String getNetwork(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {
		NetworkPage networkPage = new NetworkPage();

		String proxyEnabled = StringUtils.trimToEmpty(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_ENABLED));
		networkPage.setProxyEnabled(proxyEnabled);
		if (StringUtils.isEmpty(proxyEnabled)) {
			networkPage.setProxyEnabled("false");
		}

		String proxyUrl = configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_URL);
		networkPage.setProxyUrl(proxyUrl);

		String proxyPort = configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_PORT);
		networkPage.setProxyPort(proxyPort);

		String proxyUsername = configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_USERNAME);
		networkPage.setProxyUsername(proxyUsername);

		model.addAttribute(networkPage);
		
		String path = getPath(isFragment, PAGE_PATH);		
		return path;
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.POST)
	public String processNetwork(@ModelAttribute("networkPage") NetworkPage networkPage, Model model, BindingResult result) {

		new NetworkPageValidator().validate(networkPage, result);
		if (result.hasErrors()) {
			return PAGE_FRAGMENT_PATH;
		}

		String proxyEnabled = networkPage.getProxyEnabled();
		configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_ENABLED, proxyEnabled);

		String proxyUrl = networkPage.getProxyUrl();
		configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_URL, proxyUrl);

		String proxyPort = networkPage.getProxyPort();
		configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_PORT, String.valueOf(proxyPort));

		String proxyUsername = networkPage.getProxyUsername();
		configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_USERNAME, proxyUsername);

		String proxyPassword = networkPage.getProxyPassword();
		configurationManager.saveEncrypteConfiguration(MashUpMediaConstants.PROXY_PASSWORD, proxyPassword);

		return "redirect:/configuration?" +PARAM_FRAGMENT + "=true";
	}

}
