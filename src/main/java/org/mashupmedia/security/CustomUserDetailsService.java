package org.mashupmedia.security;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.InitialisationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {


	@Autowired
	private AdminManager adminManager;
	
	@Autowired
	private InitialisationManager initialisationManager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		int totalUsers = adminManager.getTotalUsers();
		username = StringUtils.trimToEmpty(username);
		if (totalUsers == 0 && InitialisationManager.DEFAULT_USERNAME.equals(username)) {
			initialisationManager.initialiseApplication();
		}
		User user = adminManager.getUser(username);
		return user;
	}

}
