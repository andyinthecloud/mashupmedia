package org.mashupmedia.security;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.dao.UserDao;
import org.mashupmedia.model.User;
import org.mashupmedia.service.InitialisationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserDao userDao;
	
	
	@Autowired
	private InitialisationManager initialisationManager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		int totalUsers = userDao.getTotalUsers();
		username = StringUtils.trimToEmpty(username);
		if (totalUsers == 0 && MashUpMediaConstants.ADMIN_USER_DEFAULT_USERNAME.equals(username)) {
			initialisationManager.initialiseApplication();
			logger.info("Initialised mashupmedia.");
		}
		User user = userDao.getUser(username);
		return user;
	}

}
