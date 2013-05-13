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
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.ListGroupsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/configuration/administration")
public class ListGroupController extends BaseController {
	@Autowired
	private AdminManager adminManager;

	@Override
	public String getPageTitleMessageKey() {
		return "configuration.administration.groups.title";
	}
	
	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.groups"));
		breadcrumbs.add(networkBreadcrumb);
	}

	@RequestMapping(value = "/list-groups", method = RequestMethod.GET)
	public String getGroups(Model model) {
		ListGroupsPage listGroupsPage = new ListGroupsPage();
		List<Group> groups = adminManager.getGroups();
		listGroupsPage.setGroups(groups);
		model.addAttribute("listGroupsPage", listGroupsPage);
		return "configuration/administration/list-groups";
	}

}
