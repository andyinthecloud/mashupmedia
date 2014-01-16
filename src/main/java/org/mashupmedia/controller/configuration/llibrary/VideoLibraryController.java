package org.mashupmedia.controller.configuration.llibrary;

import java.util.List;

import org.mashupmedia.editor.VideoResolutionEditor;
import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.VideoResolution;
import org.mashupmedia.service.VideoManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/configuration/library/video")
public class VideoLibraryController extends AbstractLibraryController {
	
	@Autowired
	private VideoManager videoManager;
	
	@Autowired
	private VideoResolutionEditor videoResolutionEditor;

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

	@ModelAttribute("videoResolutions")
	public List<VideoResolution> getVideoResolutions() {
		List<VideoResolution> videoResolutions = videoManager.getVideoResolutions();
		return videoResolutions;
	}
	
	@Override
	protected void initExtraFieldsInBinder(WebDataBinder binder) {
		binder.registerCustomEditor(VideoResolution.class, videoResolutionEditor);
	}
	
	@Override
	protected LibraryPage initialiseLibraryPage(Long libraryId) {
		LibraryPage libraryPage = new LibraryPage();
		VideoLibrary videoLibrary = new VideoLibrary();
		videoLibrary.setEnabled(true);
		Location location = new Location();
		videoLibrary.setLocation(location);

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
