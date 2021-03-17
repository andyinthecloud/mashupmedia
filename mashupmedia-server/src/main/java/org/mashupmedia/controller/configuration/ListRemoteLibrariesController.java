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
import org.mashupmedia.model.library.Library;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.ListRemoteLibrariesPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/configuration/list-remote-libraries")
public class ListRemoteLibrariesController extends BaseController {
	

	@Autowired
	private LibraryManager libraryManager;
	
	@Override
	public String getPageTitleMessageKey() {
		return "configuration.list-remote-libraries.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.remotelibraries"));
		breadcrumbs.add(networkBreadcrumb);
	}
	


	@RequestMapping(method = RequestMethod.GET)
	public String getRemoteLibraries(Model model) {
		ListRemoteLibrariesPage listRemoteLibrariesPage = new ListRemoteLibrariesPage();

		List<Library> remoteLibraries = libraryManager.getRemoteLibraries();
		listRemoteLibrariesPage.setRemoteLibraries(remoteLibraries);

		model.addAttribute("listRemoteLibrariesPage", listRemoteLibrariesPage);
		return "configuration.list-remote-libraries";
	}


}
