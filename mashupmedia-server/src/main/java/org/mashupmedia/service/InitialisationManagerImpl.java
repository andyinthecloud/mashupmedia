package org.mashupmedia.service;

import java.util.Date;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InitialisationManagerImpl implements InitialisationManager {

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private ConfigurationManager configurationManager;

	@Override
	public void initialiseApplication() {
		User user = adminManager.getUser(MashUpMediaConstants.ADMIN_USER_DEFAULT_USERNAME);
		if (user != null) {
			log.info("Database has already been initialised. Exiting....");
			return;
		}

		initialiseUniqueInstallationName();
		initialiseFirstRoles();
		adminManager.initialiseAdminUser();
		adminManager.initialiseSystemUser();
	}

	
	private void initialiseUniqueInstallationName() {

		StringBuilder uniqueNameBuilder = new StringBuilder();
		uniqueNameBuilder.append(System.getenv("os.arch"));
		uniqueNameBuilder.append(System.getenv("os.name"));
		uniqueNameBuilder.append(System.getenv("os.version"));
		uniqueNameBuilder.append(System.getenv("user.country"));
		uniqueNameBuilder.append(System.getenv("user.dir"));
		uniqueNameBuilder.append(System.getenv("user.home"));
		uniqueNameBuilder.append(System.getenv("user.name"));

		Date date = new Date();
		String uniqueInstallationName = String.valueOf(date.getTime())
				+ String.valueOf(uniqueNameBuilder.toString().hashCode());
		configurationManager.saveConfiguration(MashUpMediaConstants.UNIQUE_INSTALLATION_NAME, uniqueInstallationName);
	}

	protected void initialiseFirstRoles() {
		saveRole(AdminHelper.ROLE_ADMIN_IDNAME, "Administrator", User.ROLE_ADMINISTRATOR);
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
