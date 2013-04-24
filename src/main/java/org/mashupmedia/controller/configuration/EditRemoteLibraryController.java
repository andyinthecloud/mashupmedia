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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.controller.remote.RemoteLibraryController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.StringHelper.Encoding;
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
	
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private GroupEditor groupEditor;

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
		

		Library remoteLibrary = editRemoteLibraryPage.getRemoteLibrary();
		String songsXml = proceessRemoteLibraryConnection(remoteLibrary);
		if (StringUtils.isBlank(songsXml)) {
			result.rejectValue("remoteLibrary.location.path", "configuration.edit-remote-library.errors.invalid.url");
			return "configuration/edit-remote-library";
		}
		
		
		
		remoteLibrary.setRemote(true);
		libraryManager.saveLibrary(remoteLibrary);

		return "redirect:/app/configuration/list-remote-libraries";
	}
	
	protected String proceessRemoteLibraryConnection(Library remoteLibrary) {
		// Connect to the remote library
		Location location = remoteLibrary.getLocation();
		InputStream inputStream = connectionManager.connect(location.getPath());
		StringWriter stringWriter = new StringWriter();
		try {
			IOUtils.copy(inputStream, stringWriter, Encoding.UTF8.getEncodingString());
		} catch (IOException e) {
			logger.error(e);
		} 
		
		IOUtils.closeQuietly(stringWriter);
		IOUtils.closeQuietly(inputStream);
		
		return stringWriter.toString();
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Group.class, groupEditor);
	}

}
