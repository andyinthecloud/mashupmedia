package org.mashupmedia.controller.configuration.llibrary;

import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
	protected String getLibraryPath() {
		return "configuration/library/music";
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
