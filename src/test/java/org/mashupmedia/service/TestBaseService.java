package org.mashupmedia.service;

import java.util.Date;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mashupmedia.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/WEB-INF/applicationContext.xml")
@Transactional
public abstract class TestBaseService {
	
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
		adminManager.saveUser(user);	
		
		SecurityContext securityContext =  SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new TestingAuthenticationToken(user, "test"));
		SecurityContextHolder.setContext(securityContext);
	}

}
