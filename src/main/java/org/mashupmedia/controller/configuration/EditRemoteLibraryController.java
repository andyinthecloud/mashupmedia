/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.controller.configuration;

import java.util.List;

import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.LibraryManager.LibraryType;
import org.mashupmedia.task.LibraryUpdateTaskManager;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.ListRemoteLibrariesValidator;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EditRemoteLibraryPage;
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
public class EditRemoteLibraryController extends BaseController {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private LibraryUpdateTaskManager libraryUpdateTaskManager;

	@Autowired
	private GroupEditor groupEditor;
	
	@Override
	public String getPageTitleMessageKey() {
		return "library.remote.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb listRemoteLibrariesBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.remotelibraries"),
				"/app/configuration/list-remote-libraries");
		breadcrumbs.add(listRemoteLibrariesBreadcrumb);

		Breadcrumb remoteLibraryBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.editremotelibrary"));
		breadcrumbs.add(remoteLibraryBreadcrumb);
	}

	@ModelAttribute("groups")
	public List<Group> populateGroups() {
		List<Group> groups = adminManager.getGroups();
		return groups;
	}

	@RequestMapping(value = "/configuration/edit-remote-library", method = RequestMethod.GET)
	public String handleEditRemoteLibrary(@RequestParam(value = "libraryId", required = true) Long libraryId, Model model) {		
		Library library = libraryManager.getRemoteLibrary(libraryId);
		
		EditRemoteLibraryPage editRemoteLibraryPage = new EditRemoteLibraryPage();
		editRemoteLibraryPage.setLibraryId(library.getId());
		editRemoteLibraryPage.setEnabled(library.isEnabled());
		editRemoteLibraryPage.setGroups(library.getGroups());
		editRemoteLibraryPage.setName(library.getName());
		Location location = library.getLocation();
		editRemoteLibraryPage.setUrl(location.getPath());
		
		if (library instanceof MusicLibrary) {
			editRemoteLibraryPage.setLibraryTypeValue(LibraryType.MUSIC.toString());
		} else {
			editRemoteLibraryPage.setLibraryTypeValue(LibraryType.MUSIC.toString());
		}

		model.addAttribute("editRemoteLibraryPage", editRemoteLibraryPage);
		return "configuration/edit-remote-library";
	}

	@RequestMapping(value = "/configuration/new-remote-library", method = RequestMethod.GET)
	public String handleNewRemoteLibrary(@RequestParam(value = "remoteLibraryUrl", required = true) String remoteLibraryUrl, Model model) {
		EditRemoteLibraryPage editRemoteLibraryPage = new EditRemoteLibraryPage();
		LibraryType libraryType = LibraryHelper.getRemoteLibraryType(remoteLibraryUrl);
		if (libraryType == null) {
			return "configuration/url-error-remote-library";
		}

		editRemoteLibraryPage.setUrl(remoteLibraryUrl);
		editRemoteLibraryPage.setLibraryTypeValue(libraryType.toString());
		editRemoteLibraryPage.setEnabled(true);

		model.addAttribute("editRemoteLibraryPage", editRemoteLibraryPage);
		return "configuration/edit-remote-library";
	}

	@RequestMapping(value = "/configuration/delete-remote-library", method = RequestMethod.GET)
	public String deleteRemoteLibrary(@RequestParam(value = "libraryId", required = true) Long libraryId, Model model) {
		Library library = libraryManager.getLibrary(libraryId);
		libraryManager.deleteLibrary(library);
		return "redirect:/app/configuration/list-remote-libraries";
	}

	@RequestMapping(value = "/configuration/edit-remote-library", method = RequestMethod.POST)
	public String postRemoteLibrary(EditRemoteLibraryPage editRemoteLibraryPage, BindingResult result, RedirectAttributes redirectAttributes) {

		new ListRemoteLibrariesValidator().validate(editRemoteLibraryPage, result);
		if (result.hasErrors()) {
			return "configuration/edit-remote-library";
		}
		
		String libraryType = editRemoteLibraryPage.getLibraryTypeValue();				
		Library remoteLibrary = null;
		if (libraryType.equalsIgnoreCase(LibraryType.MUSIC.toString())) {
			remoteLibrary = new MusicLibrary();
		} else {
			remoteLibrary = new MusicLibrary();
		}
		
		remoteLibrary.setEnabled(editRemoteLibraryPage.isEnabled());
		remoteLibrary.setName(editRemoteLibraryPage.getName());
		remoteLibrary.setGroups(editRemoteLibraryPage.getGroups());		
		Location location = new Location();
		location.setPath(editRemoteLibraryPage.getUrl());
		remoteLibrary.setLocation(location);
		remoteLibrary.setRemote(true);
		libraryManager.saveLibrary(remoteLibrary);
		libraryUpdateTaskManager.updateRemoteLibrary(remoteLibrary);
		return "redirect:/app/configuration/list-remote-libraries";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Group.class, groupEditor);
	}

}
