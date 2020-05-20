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


import java.util.Set;

import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminHelper {
	
	public static final String ROLE_ADMIN_IDNAME = "role.admin";
	public static final String ROLE_USER_IDNAME = "role.user";

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


}
