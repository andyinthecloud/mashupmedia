package org.mashupmedia.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InitialisationManagerImpl implements InitialisationManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private AdminManager adminManager;

	@Override
	public void initialiseApplication() {
		User user = adminManager.getUser(DEFAULT_USERNAME);
		if (user != null) {
			logger.info("Database has already been initialised. Exiting....");
			return;
		}

		initialiseGroups();
		initialiseAdminUserAndRoles();
	}

	protected void initialiseGroups() {
		saveGroup("Friends");
		saveGroup("Family");
	}

	protected void saveGroup(String name) {
		Group group = new Group();
		group.setName(name);
		adminManager.saveGroup(group);

	}

	protected void initialiseAdminUserAndRoles() {
		Set<Role> roles = initialiseFirstRoles();
		User user = new User();
		user.setName(DEFAULT_NAME);
		user.setUsername(DEFAULT_USERNAME);
		user.setPassword(DEFAULT_PASSWORD);
		
		user.setEnabled(true);
		user.setEditable(false);
		user.setRoles(roles);
		
		List<Group> groups = adminManager.getGroups();
		user.setGroups(new HashSet<Group>(groups));		
		adminManager.saveUser(user);
//		adminManager.updatePassword(DEFAULT_USERNAME, DEFAULT_PASSWORD);
	}

	protected Set<Role> initialiseFirstRoles() {
		Set<Role> roles = new HashSet<Role>();
		Role adminRole = saveRole(AdminHelper.ROLE_ADMIN_IDNAME, "Administrator", "ROLE_ADMINISTRATOR");
		roles.add(adminRole);
		Role userRole = saveRole(AdminHelper.ROLE_USER_IDNAME, "User", "ROLE_USER");
		roles.add(userRole);
		return roles;

	}

	protected Role saveRole(String idName, String name, String authority) {
		Role role = new Role();
		role.setIdName(idName);
		role.setName(name);
		role.setAuthority(authority);
		adminManager.saveRole(role);
		return role;
	}

}
