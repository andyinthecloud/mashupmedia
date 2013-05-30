package org.mashupmedia.controller.configuration;

import java.util.List;

import org.mashupmedia.controller.BaseController;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.LibraryManager.LibraryType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.ListLibrariesPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ListLibrariesController extends BaseController {

	private final static String PAGE_NAME = "list-libraries";
	private final static String PAGE_PATH = "configuration/" + PAGE_NAME;
	private final static String PAGE_URL = "/" + PAGE_PATH;
	
	@Autowired
	private LibraryManager libraryManager;

	@Override
	public String getPageTitleMessageKey() {
		return "list-libraries.title";
	}
	
	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.libraries"));
		breadcrumbs.add(networkBreadcrumb);
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.GET)
	public String getListLibrariesPage(Model model) {
		
		ListLibrariesPage listLibrariesPage = new ListLibrariesPage();
		model.addAttribute(listLibrariesPage);
		
		@SuppressWarnings("unchecked")
		List<MusicLibrary> musicLibraries = (List<MusicLibrary>) libraryManager.getLibraries(LibraryType.MUSIC);
		listLibrariesPage.setMusicLibraries(musicLibraries);
		
		return PAGE_PATH;
	}

}
