package org.mashupmedia.controller.configuration.llibrary;

import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.model.library.VideoLibrary.VideoDeriveTitleType;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.VideoManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/configuration/library/video")
public class VideoLibraryController extends AbstractLibraryController {

	@Autowired
	private VideoManager videoManager;

	@Override
	protected Breadcrumb prepareFinalBreadcrumb() {
		Breadcrumb musicLibraryBreadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.configuration.library.video"));
		return musicLibraryBreadcrumb;
	}

	@Override
	protected String getLibraryPath() {
		return "configuration/library/video";
	}

	@Override
	public String getPageTitleMessageKey() {
		return "library.video.title";
	}

	@Override
	protected LibraryPage initialiseLibraryPage(Long libraryId) {
		LibraryPage libraryPage = new LibraryPage();
		VideoLibrary videoLibrary = new VideoLibrary();
		videoLibrary.setEnabled(true);
		Location location = new Location();
		videoLibrary.setLocation(location);
		videoLibrary.setVideoDeriveTitle(VideoDeriveTitleType.USE_FOLDER_AND_FILE_NAME.name());

		libraryPage.setLibrary(videoLibrary);

		if (libraryId == null) {
			return libraryPage;
		}

		videoLibrary = (VideoLibrary) libraryManager.getLibrary(libraryId);
		libraryPage.setLibrary(videoLibrary);
		libraryPage.setExists(true);

		return libraryPage;
	}
}
