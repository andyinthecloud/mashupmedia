package org.mashupmedia.controller.configuration;

import org.mashupmedia.model.User;
import org.mashupmedia.util.AdminHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/configuration/administration/my-account")
public class MyAccountController extends EditUserController{
	
	@Override
	public String getPageTitleMessageKey() {
		return "configuration.administration.my-account.title";
	}

	
	@RequestMapping(method = RequestMethod.GET)
	public String editAccount(@RequestParam(value = FRAGMENT_PARAM, required = false) Boolean isFragment, Model model) {
		User user = AdminHelper.getLoggedInUser();
		processUserPage(user, model);
		String path = getPath(isFragment, PAGE_PATH);
		return path;
	}

}
