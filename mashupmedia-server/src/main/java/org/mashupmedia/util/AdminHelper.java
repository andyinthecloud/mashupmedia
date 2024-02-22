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

package org.mashupmedia.util;

import java.util.List;
import java.util.Set;

import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.security.MediaAuthentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminHelper {

	public static final String ROLE_ADMIN_IDNAME = "role.admin";

	public static boolean isAdministrator(User user) {
		if (user == null) {
			return false;
		}

		Set<Role> roles = user.getRoles();
		if (roles == null || roles.isEmpty()) {
			return false;
		}

		for (Role role : roles) {
			if (role.getIdName().equalsIgnoreCase(ROLE_ADMIN_IDNAME)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAdministrator() {
		User user = getLoggedInUser();
		if (user == null) {
			return false;
		}

		return isAdministrator(user);
	}

	public static void setLoggedInUser(User user) {
		MediaAuthentication authentication = MediaAuthentication
				.builder()
				.name(user.getName())
				.authenticated(true)
				.authorities(user.getRoles())
				.details(user)
				.principal(user)
				.build();

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public static User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}

		Object principal = authentication.getPrincipal();
		if (principal == null) {
			return null;
		}

		if (principal instanceof User) {
			User user = (User) principal;
			return user;
		}

		return null;
	}

	// public static boolean isAllowedGroup(List<Group> groups) {
	// User user = getLoggedInUser();
	// Set<Group> userGroups = user.getGroups();
	// return userGroups.stream().anyMatch(groups::contains);
	// }

	public static void checkUserPermission(User user) {
		User loggedInUser = AdminHelper.getLoggedInUser();
		if (loggedInUser.isAdministrator() || loggedInUser.isSystem()) {
			return;
		}

		if (loggedInUser.equals(user)) {
			return;
		}

		throw new AccessDeniedException("Unauthorised access to library");
	}
}
