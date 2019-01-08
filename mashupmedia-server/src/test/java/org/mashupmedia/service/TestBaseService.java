package org.mashupmedia.service;

import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/WEB-INF/applicationContext.xml", "classpath:dataSource.xml" })
@TransactionConfiguration
@Transactional
public abstract class TestBaseService {

	public static ResourceBundle testResourceBundle = ResourceBundle.getBundle("test");

	@Autowired
	private InitialisationManager initialisationManager;

	@Autowired
	private AdminManager adminManager;

	@Before
	public void initialiseTests() {

		initialisationManager.initialiseApplication();
		User user = new User();
		Date date = new Date();
		user.setCreatedOn(date);
		user.setEnabled(true);
		user.setUpdatedOn(date);
		user.setPassword("test");
		user.setUsername("test");
		user.setGroups(new HashSet<Group>(adminManager.getGroups()));		
		
		adminManager.saveUser(user);

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new TestingAuthenticationToken(user, "test"));
		SecurityContextHolder.setContext(securityContext);
	}

}
