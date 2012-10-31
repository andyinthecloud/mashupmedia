package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.service.MediaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ajax/search")
public class AjaxSearchController extends BaseAjaxController {

	@Autowired
	private MediaManager mediaManager;
	
	@RequestMapping(value = "/media-items-autocomplete", method = RequestMethod.POST)
	public String handleMediaItemsAutocomplete(@RequestParam("searchWords") String searchWords, Model model) {
		List<String> suggestions = mediaManager.findAutoCompleteMediaItems(searchWords);
		model.addAttribute("suggestions", suggestions);
		return "ajax/search/suggestions";
	}
	
}
