package org.mashupmedia.util;

import org.mashupmedia.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityHelper {

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
