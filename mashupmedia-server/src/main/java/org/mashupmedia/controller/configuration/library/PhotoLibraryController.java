package org.mashupmedia.controller.configuration.library;

import org.mashupmedia.model.library.PhotoLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/configuration/library/photo")
public class PhotoLibraryController extends AbstractLibraryController {

	@Override
	protected Breadcrumb prepareFinalBreadcrumb() {
		Breadcrumb musicLibraryBreadcrumb = new Breadcrumb(
				MessageHelper
						.getMessage("breadcrumb.configuration.library.photo"));
		return musicLibraryBreadcrumb;
	}

	@Override
	protected String getLibraryPath() {
		return "configuration.library.photo";
	}

	@Override
	public String getPageTitleMessageKey() {
		return "library.photo.title";
	}

	@Override
	protected LibraryPage initialiseLibraryPage(Long libraryId) {
		LibraryPage libraryPage = new LibraryPage();
		PhotoLibrary photoLibrary = new PhotoLibrary();
		photoLibrary.setEnabled(true);
		Location location = new Location();
		photoLibrary.setLocation(location);

		libraryPage.setLibrary(photoLibrary);

		if (libraryId == null) {
			return libraryPage;
		}

		photoLibrary = (PhotoLibrary) libraryManager.getLibrary(libraryId);
		libraryPage.setLibrary(photoLibrary);
		libraryPage.setExists(true);

		return libraryPage;
	}

}
