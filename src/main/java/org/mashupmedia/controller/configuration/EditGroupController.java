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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.LibraryEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.EditGroupPageValidator;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EditGroupPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/configuration/administration")
public class EditGroupController extends BaseController {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private LibraryEditor libraryEditor;

	@Override
	public String getPageTitleMessageKey() {
		return "configuration.administration.edit-group.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration"));
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.groups"), "/app/configuration/administration/list-groups"));
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.edit-group")));
	}

	@ModelAttribute("libraries")
	public List<Library> getLibraries() {
		List<Library> libraries = (List<Library>) libraryManager.getLibraries(LibraryType.ALL);
		return libraries;
	}

	@RequestMapping(value = "/edit-group/{groupId}", method = RequestMethod.GET)
	public String editUser(@PathVariable("groupId") Long groupId, Model model) {
		Group group = adminManager.getGroup(groupId);
		EditGroupPage editGroupPage = prepareEditGroupPage(group);
		model.addAttribute("editGroupPage", editGroupPage);
		return "configuration/administration/edit-group";
	}

	protected EditGroupPage prepareEditGroupPage(Group group) {
		EditGroupPage editGroupPage = new EditGroupPage();
		editGroupPage.setGroup(group);
		List<Library> selectedLibraries = libraryManager.getLibrariesForGroup(group.getId());
		editGroupPage.setSelectedLibraries(selectedLibraries);
		return editGroupPage;
	}

	@RequestMapping(value = "/add-group", method = RequestMethod.GET)
	public String addUser(Model model) {
		Group group = new Group();
		EditGroupPage editGroupPage = prepareEditGroupPage(group);
		model.addAttribute("editGroupPage", editGroupPage);
		return "configuration/administration/edit-group";
	}

	@RequestMapping(value = "/submit-group", method = RequestMethod.POST)
	public String processSubmitUser(@ModelAttribute("editGroupPage") EditGroupPage editGroupPage, BindingResult bindingResult, Model model) {

		new EditGroupPageValidator().validate(editGroupPage, bindingResult);
		if (bindingResult.hasErrors()) {
			return "configuration/administration/edit-group";
		}

		Group group = editGroupPage.getGroup();

		String action = StringUtils.trimToEmpty(editGroupPage.getAction());

		if (action.equalsIgnoreCase("delete")) {
			adminManager.deleteGroup(group.getId());
		} else {

			List<Library> selectedLibraries = editGroupPage.getSelectedLibraries();
			processSaveLibraries(selectedLibraries, group);

			adminManager.saveGroup(group);
		}

		return "redirect:/app/configuration/administration/list-groups";
	}

	protected void processSaveLibraries(List<Library> selectedLibraries, Group group) {

		if (selectedLibraries == null) {
			selectedLibraries = new ArrayList<Library>();
		}

		List<Library> libraries = (List<Library>) libraryManager.getLibraries(LibraryType.ALL);

		for (Library selectedLibrary : selectedLibraries) {

			long selectedLibraryId = selectedLibrary.getId();
			if (selectedLibraryId == 0) {
				continue;
			}
			selectedLibrary = libraryManager.getLibrary(selectedLibraryId);
			Set<Group> groups = selectedLibrary.getGroups();
			if (groups == null) {
				groups = new HashSet<Group>();
			}
			groups.add(group);
			selectedLibrary.setGroups(groups);
			adminManager.saveGroup(group);
			libraryManager.saveLibrary(selectedLibrary);
			removeLibrary(libraries, selectedLibraryId);

		}

		for (Library library : libraries) {
			Set<Group> groups = library.getGroups();
			if (groups == null || groups.isEmpty()) {
				continue;
			}
			long libraryId = library.getId();
			if (libraryId > 0) {
				library = libraryManager.getLibrary(libraryId);
			}

			groups.remove(group);
			library.setGroups(groups);
			libraryManager.saveLibrary(library);
		}
	}

	protected void removeLibrary(List<Library> libraries, long libraryId) {

		Library libraryToRemove = null;
		for (Library library : libraries) {
			if (library.getId() == libraryId) {
				libraryToRemove = library;
				break;
			}
		}

		if (libraryToRemove == null) {
			return;
		}

		libraries.remove(libraryToRemove);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Library.class, libraryEditor);
	}

}
