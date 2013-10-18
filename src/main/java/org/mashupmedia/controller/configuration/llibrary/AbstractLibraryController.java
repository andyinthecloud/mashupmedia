package org.mashupmedia.controller.configuration.llibrary;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.task.LibraryUpdateTaskManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.LibraryPageValidator;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public abstract class AbstractLibraryController extends BaseController {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private GroupEditor groupEditor;

	@Autowired
	private LibraryUpdateTaskManager libraryUpdateTaskManager;
	
	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {

		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb musicConfigurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.libraries"),
				"/app/configuration/list-libraries");
		breadcrumbs.add(musicConfigurationBreadcrumb);
		
		breadcrumbs.add(prepareFinalBreadcrumb());
		
	}
	
	protected abstract Breadcrumb prepareFinalBreadcrumb();
	

	@ModelAttribute("groups")
	public List<Group> populateGroups() {
		List<Group> groups = adminManager.getGroups();
		return groups;
	}

	protected LibraryPage initialiseLibraryPage(Long libraryId) {
		LibraryPage musicLibraryPage = new LibraryPage();
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setEnabled(true);
		Location location = new Location();
		musicLibrary.setLocation(location);

		musicLibraryPage.setLibrary(musicLibrary);

		if (libraryId == null) {
			return musicLibraryPage;
		}

		musicLibrary = (MusicLibrary) libraryManager.getLibrary(libraryId);
		musicLibraryPage.setLibrary(musicLibrary);
		musicLibraryPage.setExists(true);
		return musicLibraryPage;
	}
	
	protected abstract String getPagePath();

	@RequestMapping(method = RequestMethod.GET)
	public String getLibrary(@RequestParam(value = "id", required = false) Long libraryId, Model model) {
		LibraryPage libraryPage = initialiseLibraryPage(libraryId);
		model.addAttribute(libraryPage);
		return getPagePath();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processLibrary(@ModelAttribute("libraryPage") LibraryPage libraryPage, Model model, BindingResult result, RedirectAttributes redirectAttributes) {

		new LibraryPageValidator().validate(libraryPage, result);
		if (result.hasErrors()) {
			return getPagePath();
		}

		String action = StringUtils.trimToEmpty(libraryPage.getAction());
		if (action.equalsIgnoreCase(MashUpMediaConstants.ACTION_DELETE)) {
			processDeleteAction(libraryPage);
		} else {
			processSaveAction(libraryPage);
			libraryUpdateTaskManager.updateLibrary(libraryPage.getLibrary());

		}

		return "redirect:list-libraries";
	}

	private void processSaveAction(LibraryPage libraryPage) {
		Library library = libraryPage.getLibrary();
		
		List<Group> groups = libraryPage.getGroups();
		if (groups != null) {
			library.setGroups(new HashSet<Group>(groups));							
		}
		
		long libraryId = library.getId();
		if (libraryId > 0) {
			// link the remote shares
			Library savedLibrary = libraryManager.getLibrary(libraryId);
			List<RemoteShare> remoteShares = savedLibrary.getRemoteShares();
			library.setRemoteShares(remoteShares);
		}
		
		libraryManager.saveLibrary(library);
	}
	
	private void processDeleteAction(LibraryPage libraryPage) {
		Library library = libraryPage.getLibrary();
		libraryManager.deleteLibrary(library);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Group.class, groupEditor);
	}
}
