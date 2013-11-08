package org.mashupmedia.controller.configuration.llibrary;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
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
@RequestMapping("/configuration/library/music")
public class MusicLibraryController extends AbstractLibraryController {

	@Override
	public String getPageTitleMessageKey() {
		return "library.music.title";
	}

	@Override
	protected Breadcrumb prepareFinalBreadcrumb() {
		Breadcrumb musicLibraryBreadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.configuration.library.music"));
		return musicLibraryBreadcrumb;
	}

	@Override
	protected String getPagePath() {
		return "configuration/library/music";
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
	
	@Override
	protected LibraryPage initialiseLibraryPage(Long libraryId) {
		LibraryPage libraryPage = new LibraryPage();
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setEnabled(true);
		Location location = new Location();
		musicLibrary.setLocation(location);

		libraryPage.setLibrary(musicLibrary);

		if (libraryId == null) {
			return libraryPage;
		}

		musicLibrary = (MusicLibrary) libraryManager.getLibrary(libraryId);
		libraryPage.setLibrary(musicLibrary);
		libraryPage.setExists(true);
		return libraryPage;
	}

}
