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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.editor.GroupEditor;
import org.mashupmedia.editor.RoleEditor;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.util.AdminHelper;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/configuration/administration")
public class EditUserController extends BaseController {

	protected final static String PAGE_PATH = "configuration.administration.edit-user";

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private RoleEditor roleEditor;

	@Autowired
	private GroupEditor groupEditor;

	@Override
	public String getPageTitleMessageKey() {
		return "configuration.administration.edit-user.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/configuration"));
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.users"),
				"/configuration/administration/list-users"));
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.edit-user")));
	}

	@ModelAttribute("groups")
	public List<Group> getGroups() {
		List<Group> groups = adminManager.getGroups();
		return groups;
	}

	@ModelAttribute("roles")
	public List<Role> getRoles() {
		List<Role> roles = adminManager.getRoles();
		return roles;
	}

	@RequestMapping(value = "/edit-user/{userId}", method = RequestMethod.GET)
	public String editUser(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@PathVariable("userId") Long userId, Model model) {
		User user = adminManager.getUser(userId);

		processUserPage(user, model);
		String path = getPath(isFragment, PAGE_PATH);
		return path;
	}

	protected void processUserPage(User user, Model model) {
		EditUserPage editUserPage = prepareEditUserPage(user);
		model.addAttribute("editUserPage", editUserPage);
	}

	@RequestMapping(value = "/add-user", method = RequestMethod.GET)
	public String addUser(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {
		User user = new User();
		user.setEnabled(true);
		user.setEditable(true);

		processUserPage(user, model);
		String path = getPath(isFragment, PAGE_PATH);
		return path;
	}

	@RequestMapping(value = "/submit-user", method = RequestMethod.POST)
	public String processSubmitUser(@ModelAttribute("editUserPage") EditUserPage editUserPage,
			BindingResult bindingResult, Model model) {

		new EditUserPageValidator().validate(editUserPage, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute(MODEL_KEY_HAS_ERRORS, Boolean.TRUE.toString());			
			return PAGE_PATH + FRAGMENT_APPEND_PATH;
		}

		processAdministratorRole(editUserPage);

		User user = editUserPage.getUser();

		String action = StringUtils.trimToEmpty(editUserPage.getAction());

		if (action.equalsIgnoreCase("delete")) {
			long userId = user.getId();
			adminManager.deleteUser(userId);

			long currentUserId = AdminHelper.getLoggedInUser().getId();
			if (userId == currentUserId) {
				return "/j_spring_security_logout";
			}

		} else {
			adminManager.saveUser(user);
		}

		return "redirect:/configuration/administration/list-users?" + PARAM_FRAGMENT + "=true";
	}

	protected void processAdministratorRole(EditUserPage editUserPage) {
		boolean isAdministrator = editUserPage.isAdministrator();
		User user = editUserPage.getUser();

		Role administrationRole = adminManager.getRole(AdminHelper.ROLE_ADMIN_IDNAME);
		Set<Role> roles = user.getRoles();
		if (roles == null) {
			roles = new HashSet<Role>();
		}

		if (isAdministrator) {
			roles.add(administrationRole);
		} else {
			roles.remove(administrationRole);
		}

		user.setRoles(roles);
	}

	protected EditUserPage prepareEditUserPage(User user) {
		EditUserPage editUserPage = new EditUserPage();
		editUserPage.setUser(user);
		boolean isUserAdministrator = AdminHelper.isAdministrator(user);
		editUserPage.setAdministrator(isUserAdministrator);

		User currentUser = AdminHelper.getLoggedInUser();
		boolean isShowAdministratorRights = AdminHelper.isAdministrator(currentUser);
		editUserPage.setShowAdministrator(isShowAdministratorRights);

		return editUserPage;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Role.class, roleEditor);
		binder.registerCustomEditor(Group.class, groupEditor);
	}

}
