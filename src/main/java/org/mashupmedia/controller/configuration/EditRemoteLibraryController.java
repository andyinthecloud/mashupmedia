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
import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.ListRemoteLibrariesValidator;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EditRemoteLibraryPage;
import org.mashupmedia.web.page.EditUserPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuration/edit-remote-library")
public class EditRemoteLibraryController extends BaseController {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;

	public enum RemoteLibrariesActionType {
		ADD, DELETE;
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb listRemoteLibrariesBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.remotelibraries",
				"/app/configuration/list-remote-libraries"));
		breadcrumbs.add(listRemoteLibrariesBreadcrumb);

		Breadcrumb remoteLibraryBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.editremotelibraries"));
		breadcrumbs.add(remoteLibraryBreadcrumb);
	}

	@ModelAttribute("groups")
	public List<Group> populateGroups() {
		List<Group> groups = adminManager.getGroups();
		return groups;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String editUser(@RequestParam(value = "libraryId", required = false) Long libraryId, Model model) {
		EditRemoteLibraryPage editRemoteLibraryPage = new EditRemoteLibraryPage();
		
		Library library = new Library();
		if (libraryId != null) {
			library = libraryManager.getRemoteLibrary(libraryId);			
		} else {
			Location location = new Location();
			library.setLocation(location);
		}
		
		editRemoteLibraryPage.setRemoteLibrary(library);
		
		model.addAttribute("editRemoteLibraryPage", editRemoteLibraryPage);
		return "configuration/edit-remote-library";
	}

	
	@RequestMapping(method = RequestMethod.POST)
	public String postRemoteLibrary(EditRemoteLibraryPage listRemoteLibrariesPage, BindingResult result, RedirectAttributes redirectAttributes) {

		new ListRemoteLibrariesValidator().validate(listRemoteLibrariesPage, result);
		if (result.hasErrors()) {
			return "configuration/edit-remote-library";
		}

		Library remoteLibrary = listRemoteLibrariesPage.getRemoteLibrary();
		remoteLibrary.setRemote(true);
		libraryManager.saveLibrary(remoteLibrary);

		return "redirect:/app/configuration/list-remote-libraries";
	}

}
