package org.mashupmedia.controller.configuration.library;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.RemoteShare;
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
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("libraryPage")
public abstract class AbstractLibraryController extends BaseController {

	@Autowired
	protected AdminManager adminManager;

	@Autowired
	protected LibraryManager libraryManager;

	@Autowired
	protected GroupEditor groupEditor;

	@Autowired
	protected LibraryUpdateTaskManager libraryUpdateTaskManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {

		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"),
				"/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb musicConfigurationBreadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.configuration.libraries"), "/app/configuration/list-libraries");
		breadcrumbs.add(musicConfigurationBreadcrumb);

		breadcrumbs.add(prepareFinalBreadcrumb());

	}

	protected abstract Breadcrumb prepareFinalBreadcrumb();

	@ModelAttribute("groups")
	public List<Group> populateGroups() {
		List<Group> groups = adminManager.getGroups();
		return groups;
	}

	protected abstract LibraryPage initialiseLibraryPage(Long libraryId);

	protected abstract String getLibraryPath();

	@RequestMapping(method = RequestMethod.GET)
	public String handleGetLibrary(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@RequestParam(value = "id", required = false) Long libraryId, Model model) {
		LibraryPage libraryPage = initialiseLibraryPage(libraryId);
		model.addAttribute(libraryPage);
		String path = getLibraryPath();
		path = getPath(isFragment, path);
		return path;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handlePostLibrary(@ModelAttribute("libraryPage") LibraryPage libraryPage, Model model,
			BindingResult result, RedirectAttributes redirectAttributes) {

		getValidator().validate(libraryPage, result);
		if (result.hasErrors()) {
			String path = getLibraryPath();
			path = getPath(true, path);
			return path;
		}

		processPostLibrary(libraryPage, model, result, redirectAttributes);
		return getRedirectListLibraryView();
	}

	public void processPostLibrary(LibraryPage libraryPage, Model model, BindingResult result,
			RedirectAttributes redirectAttributes) {
		String action = StringUtils.trimToEmpty(libraryPage.getAction());
		if (action.equalsIgnoreCase(MashUpMediaConstants.ACTION_DELETE)) {
			processDeleteAction(libraryPage);
		} else {
			processSaveAction(libraryPage);

		}
	}

	protected void validateLibraryPage(LibraryPage libraryPage, BindingResult result) {
		new LibraryPageValidator().validate(libraryPage, result);
	}

	protected String getRedirectListLibraryView() {
		return "redirect:/app/configuration/list-libraries?" + PARAM_FRAGMENT + "=true";
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
		
		libraryManager.saveAndReinitialiseLibrary(library);
		libraryUpdateTaskManager.updateLibrary(library);
		
	}

	private void processDeleteAction(LibraryPage libraryPage) {
		Library library = libraryPage.getLibrary();
		libraryManager.deleteLibrary(library);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Group.class, groupEditor);
		initExtraFieldsInBinder(binder);
	}

	protected Validator getValidator() {
		return new LibraryPageValidator();
	}

	protected void initExtraFieldsInBinder(WebDataBinder binder) {
		// override if necessary
	}
}
