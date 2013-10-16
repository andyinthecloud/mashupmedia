package org.mashupmedia.controller.configuration.llibrary;

import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping ("/configuration/music-library")
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

}
