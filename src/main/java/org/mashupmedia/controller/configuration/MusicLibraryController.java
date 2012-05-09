package org.mashupmedia.controller.configuration;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.task.LibraryUpdateTaskManager;
import org.mashupmedia.util.EncryptionHelper;
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

@Controller
public class MusicLibraryController extends BaseController {

	private final static String PAGE_NAME = "music-library";
	private final static String PAGE_PATH = "configuration/" + PAGE_NAME;
	private final static String PAGE_URL = "/" + PAGE_PATH;

	private final static String LOCATION_TYPE_FOLDER = "folder";
	private final static String LOCATION_TYPE_FTP = "ftp";

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

		Breadcrumb musicConfigurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.music"),
				"/app/configuration/music-configuration");
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
		musicLibraryPage.setMusicLibrary(musicLibrary);
		musicLibraryPage.setLocationType(LOCATION_TYPE_FOLDER);

		Location location = new Location();
		musicLibraryPage.setFolderLocation(location);

		FtpLocation ftpLocation = new FtpLocation();
		musicLibraryPage.setFtpLocation(ftpLocation);

		if (libraryId == null) {
			return musicLibraryPage;
		}

		musicLibrary = libraryManager.getMusicLibrary(libraryId);
		musicLibraryPage.setMusicLibrary(musicLibrary);
		location = musicLibrary.getLocation();
		String locationType = LOCATION_TYPE_FOLDER;
		if (location instanceof FtpLocation) {
			musicLibraryPage.setFtpLocation((FtpLocation) location);
			locationType = LOCATION_TYPE_FTP;
		} else {
			musicLibraryPage.setFolderLocation((Location) location);
			locationType = LOCATION_TYPE_FOLDER;
		}

		musicLibraryPage.setLocationType(locationType);

		return musicLibraryPage;
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.GET)
	public String getMusicLibrary(@RequestParam(value = "id", required = false) Long libraryId, Model model) {
		MusicLibraryPage musicLibraryPage = initialiseMusicLibraryPage(libraryId);
		model.addAttribute(musicLibraryPage);
		return PAGE_PATH;
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.POST)
	public String processMusicLibrary(@ModelAttribute("musicLibraryPage") MusicLibraryPage musicLibraryPage, Model model, BindingResult result) {

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

		return "redirect:music-configuration";
	}

	private void processSaveAction(MusicLibraryPage musicLibraryPage) {
		String locationType = StringUtils.trimToEmpty(musicLibraryPage.getLocationType());
		Location location = null;
		if (locationType.equalsIgnoreCase(LOCATION_TYPE_FTP)) {
			FtpLocation ftpLocation = musicLibraryPage.getFtpLocation();
			String ftpPassword = ftpLocation.getPassword();
			String encryptedProxyPassword = EncryptionHelper.encryptText(ftpPassword);
			ftpLocation.setPassword(encryptedProxyPassword);
			location = ftpLocation;

		} else {
			location = musicLibraryPage.getFolderLocation();
		}

		MusicLibrary musicLibrary = musicLibraryPage.getMusicLibrary();
		musicLibrary.setLocation(location);
		libraryManager.saveMusicLibrary(musicLibrary);

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
