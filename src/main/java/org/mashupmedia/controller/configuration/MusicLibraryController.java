package org.mashupmedia.controller.configuration;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.task.LibraryUpdateTaskManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.MusicLibraryPageValidator;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.MusicLibraryPage;
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
public class MusicLibraryController extends BaseController {

	private final static String PAGE_NAME = "music-library";
	private final static String PAGE_PATH = "configuration/" + PAGE_NAME;
	private final static String PAGE_URL = "/" + PAGE_PATH;

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private GroupEditor groupEditor;

	@Autowired
	private LibraryUpdateTaskManager libraryUpdateTaskManager;

	@Override
	public String getPageTitleMessageKey() {
		return "musiclibrary.title";
	}
	
	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {

		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb musicConfigurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.libraries"),
				"/app/configuration/list-libraries");
		breadcrumbs.add(musicConfigurationBreadcrumb);

		Breadcrumb musicLibraryBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.musiclibrary"));
		breadcrumbs.add(musicLibraryBreadcrumb);
	}

	@ModelAttribute("groups")
	public List<Group> populateGroups() {
		List<Group> groups = adminManager.getGroups();
		return groups;
	}

	protected MusicLibraryPage initialiseMusicLibraryPage(Long libraryId) {
		MusicLibraryPage musicLibraryPage = new MusicLibraryPage();
		MusicLibrary musicLibrary = new MusicLibrary();
		musicLibrary.setEnabled(true);
		Location location = new Location();
		musicLibrary.setLocation(location);

		musicLibraryPage.setMusicLibrary(musicLibrary);

		if (libraryId == null) {
			return musicLibraryPage;
		}

		musicLibrary = (MusicLibrary) libraryManager.getLibrary(libraryId);
		musicLibraryPage.setMusicLibrary(musicLibrary);
		musicLibraryPage.setExists(true);
		return musicLibraryPage;
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.GET)
	public String getMusicLibrary(@RequestParam(value = "id", required = false) Long libraryId, Model model) {
		MusicLibraryPage musicLibraryPage = initialiseMusicLibraryPage(libraryId);
		model.addAttribute(musicLibraryPage);
		return PAGE_PATH;
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.POST)
	public String processMusicLibrary(@ModelAttribute("musicLibraryPage") MusicLibraryPage musicLibraryPage, Model model, BindingResult result, RedirectAttributes redirectAttributes) {

		new MusicLibraryPageValidator().validate(musicLibraryPage, result);
		if (result.hasErrors()) {
			return PAGE_PATH;
		}

		String action = StringUtils.trimToEmpty(musicLibraryPage.getAction());
		if (action.equalsIgnoreCase(MashUpMediaConstants.ACTION_DELETE)) {
			processDeleteAction(musicLibraryPage);
		} else {
			processSaveAction(musicLibraryPage);
			libraryUpdateTaskManager.updateLibrary(musicLibraryPage.getMusicLibrary());

		}

		return "redirect:list-libraries";
	}

	private void processSaveAction(MusicLibraryPage musicLibraryPage) {
		MusicLibrary musicLibrary = musicLibraryPage.getMusicLibrary();
		
		List<Group> groups = musicLibraryPage.getGroups();
		if (groups != null) {
			musicLibrary.setGroups(new HashSet<Group>(groups));							
		}
		
		libraryManager.saveLibrary(musicLibrary);
	}
	
	private void processDeleteAction(MusicLibraryPage musicLibraryPage) {
		MusicLibrary musicLibrary = musicLibraryPage.getMusicLibrary();
		libraryManager.deleteLibrary(musicLibrary);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Group.class, groupEditor);
	}

}
