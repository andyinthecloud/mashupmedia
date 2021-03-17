package org.mashupmedia.controller.configuration;

import org.mashupmedia.service.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/configuration/application")
public class ApplicationController {
	
	@Autowired
	private ConfigurationManager configurationManager;
	
	@RequestMapping(value = "/index-media-items", method = RequestMethod.GET)
	public String handleIndexMediaItems(Model model, BindingResult result) {
		configurationManager.indexMediaItems();
		return "redirect:/";
	}

}
