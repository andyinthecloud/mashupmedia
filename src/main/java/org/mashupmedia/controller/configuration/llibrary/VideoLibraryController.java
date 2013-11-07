package org.mashupmedia.controller.configuration.llibrary;

import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuration/library/video")
public class VideoLibraryController extends AbstractLibraryController {

	@Override
	protected Breadcrumb prepareFinalBreadcrumb() {
		Breadcrumb musicLibraryBreadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.configuration.library.video"));
		return musicLibraryBreadcrumb;
	}

	@Override
	protected String getPagePath() {
		return "configuration/library/video";
	}

	@Override
	public String getPageTitleMessageKey() {
		return "library.video.title";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String handleGetLibrary(@RequestParam(value = "id", required = false) Long libraryId, Model model) {
		processGetLibrary(libraryId, model);
		return getPagePath();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handlePostLibrary(@ModelAttribute("libraryPage") LibraryPage libraryPage, Model model,
			BindingResult result, RedirectAttributes redirectAttributes) {

		validateLibraryPage(libraryPage, result);
		if (result.hasErrors()) {
			return getPagePath();
		}

		processPostLibrary(libraryPage, model, result, redirectAttributes);
		return getRedirectListLibraryView();
	}
	
	
}
