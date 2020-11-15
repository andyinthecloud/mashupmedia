package org.mashupmedia.controller.configuration;

import java.util.List;

import org.mashupmedia.controller.BaseController;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.ListLibrariesPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ListLibrariesController extends BaseController {

	private final static String LIST_LIBRARIES_PAGE_PATH = "configuration.list-libraries";
	private final static String CHOOSE_LIBRARY_PAGE_PATH = "configuration.choose-library-type";

	@Autowired
	private LibraryManager libraryManager;

	@Override
	public String getPageTitleMessageKey() {
		return "list-libraries.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"),
				"/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.libraries"));
		breadcrumbs.add(networkBreadcrumb);
	}

	@RequestMapping(value = "/configuration/list-libraries", method = RequestMethod.GET)
	public String getListLibrariesPage(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			Model model) {

		ListLibrariesPage listLibrariesPage = new ListLibrariesPage();
		model.addAttribute(listLibrariesPage);

		List<Library> libraries = (List<Library>) libraryManager.getLocalLibraries(LibraryType.ALL);
		listLibrariesPage.setLibraries(libraries);

		String path = getPath(isFragment, LIST_LIBRARIES_PAGE_PATH);
		return path;
	}

	@RequestMapping(value = "/configuration/choose-library-type", method = RequestMethod.GET)
	public String handleChooseLibraryType(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			Model model) {
		String path = getPath(isFragment, CHOOSE_LIBRARY_PAGE_PATH);
		return path;
	}

}
