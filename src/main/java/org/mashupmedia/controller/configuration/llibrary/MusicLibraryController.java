package org.mashupmedia.controller.configuration.llibrary;

import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/configuration/music-library")
public class MusicLibraryController extends AbstractLibraryController {

	@Override
	public String getPageTitleMessageKey() {
		return "musiclibrary.title";
	}

	@Override
	protected Breadcrumb prepareFinalBreadcrumb() {
		Breadcrumb musicLibraryBreadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.configuration.musiclibrary"));
		return musicLibraryBreadcrumb;
	}

	@Override
	protected String getPagePath() {
		return "/configuration/music-library";
	}
	
//	@RequestMapping(method = RequestMethod.GET)
//	public String getLibrary(@RequestParam(value = "id", required = false) Long libraryId, Model model) {
//		LibraryPage libraryPage = initialiseLibraryPage(libraryId);
//		model.addAttribute(libraryPage);
//		return getPagePath();
//	}

}
