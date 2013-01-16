package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.Group;
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

	public static List<Long> getLoggedInUserGroupIds() {
		User user = getLoggedInUser();
		if (user == null) {
			return null;
		}

		Set<Group> groups = user.getGroups();
		if (groups == null || groups.isEmpty()) {
			return null;
		}

		List<Long> groupIds = new ArrayList<Long>();
		for (Group group : groups) {
			groupIds.add(group.getId());
		}

		return groupIds;
	}

}
