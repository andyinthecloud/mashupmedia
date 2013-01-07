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

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.editor.RoleEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.EditUserPageValidator;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EditUserPage;
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
public class EditUserController extends BaseController {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private RoleEditor roleEditor;

	@Autowired
	private GroupEditor groupEditor;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration"));
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.users"), "/app/configuration/administration/list-users"));
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.edit-user")));
	}

	@RequestMapping(value = "/edit-user/{userId}", method = RequestMethod.GET)
	public String editUser(@PathVariable("userId") Long userId, Model model) {
		User user = adminManager.getUser(userId);
		EditUserPage editUserPage = prepareEditUserPage(user);
		model.addAttribute("editUserPage", editUserPage);
		return "configuration/administration/edit-user";
	}

	@RequestMapping(value = "/add-user", method = RequestMethod.GET)
	public String addUser(Model model) {
		User user = new User();
		user.setEnabled(true);
		user.setEditable(true);
		EditUserPage editUserPage = prepareEditUserPage(user);
		model.addAttribute("editUserPage", editUserPage);
		return "configuration/administration/edit-user";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmitUser(@ModelAttribute("editUserPage") EditUserPage editUserPage, BindingResult bindingResult, Model model) {
		
		new EditUserPageValidator().validate(editUserPage, bindingResult);
		if (bindingResult.hasErrors()) {
			return "configuration/administration/edit-user";			
		}
		
		User user = editUserPage.getUser();
		String action = StringUtils.trimToEmpty(editUserPage.getAction());

		if (action.equalsIgnoreCase("delete")) {
			adminManager.deleteUser(user);
		}  else {
			adminManager.saveUser(user);			
		}
		
		return "redirect:/configuration/administration/list-users";
	}

	protected EditUserPage prepareEditUserPage(User user) {
		EditUserPage editUserPage = new EditUserPage();
		editUserPage.setUser(user);

		List<Role> roles = adminManager.getRoles();
		editUserPage.setRoles(roles);

		List<Group> groups = adminManager.getGroups();
		editUserPage.setGroups(groups);

		return editUserPage;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Role.class, roleEditor);
		binder.registerCustomEditor(Group.class, groupEditor);
	}

}