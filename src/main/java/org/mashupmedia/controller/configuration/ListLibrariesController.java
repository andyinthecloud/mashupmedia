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

@Controller
public class ListLibrariesController extends BaseController {

	@Autowired
	private LibraryManager libraryManager;

	@Override
	public String getPageTitleMessageKey() {
		return "list-libraries.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"),
				"/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.libraries"));
		breadcrumbs.add(networkBreadcrumb);
	}

	@RequestMapping(value = "/configuration/list-libraries", method = RequestMethod.GET)
	public String getListLibrariesPage(Model model) {

		ListLibrariesPage listLibrariesPage = new ListLibrariesPage();
		model.addAttribute(listLibrariesPage);

		List<Library> libraries = (List<Library>) libraryManager.getLocalLibraries(LibraryType.ALL);
		listLibrariesPage.setLibraries(libraries);

		return "configuration/list-libraries";
	}

	@RequestMapping(value = "/configuration/choose-library-type", method = RequestMethod.GET)
	public String handleChooseLibraryType(Model model) {
		return "configuration/choose-library-type";
	}

}
