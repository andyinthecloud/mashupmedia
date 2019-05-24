package org.mashupmedia.controller.admin;

import java.security.Principal;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.mashupmedia.dto.User;
import org.mashupmedia.security.AuthenticationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SecurityController {
	
	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;

	@RequestMapping("/login")
	public boolean login(@RequestBody User user) {		
		boolean isAuthenticated = authenticationConfiguration.authenticate(user.getUsername(), user.getPassword());
		return isAuthenticated;
	}

	@RequestMapping("/user")
	public Principal user(HttpServletRequest request) {
		String authToken = request.getHeader("Authorization").substring("Basic".length()).trim();
		return () -> new String(Base64.getDecoder().decode(authToken)).split(":")[0];
	}
}
