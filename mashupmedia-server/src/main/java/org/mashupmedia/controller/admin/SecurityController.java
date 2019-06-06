package org.mashupmedia.controller.admin;

import java.security.Principal;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.mashupmedia.dto.UserDTO;
import org.mashupmedia.model.User;
import org.mashupmedia.security.AuthenticationConfiguration;
import org.mashupmedia.service.AdminManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/security")
public class SecurityController {
	
	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;
	
	@Autowired
	private AdminManager adminManager;
	

	@RequestMapping(path = "/login", method = {RequestMethod.POST, RequestMethod.OPTIONS})
	public UserDTO login(@RequestBody UserDTO userDTO) {		
		String username = userDTO.getUsername();
		String password = userDTO.getPassword();
		
		boolean isAuthenticated = authenticationConfiguration.authenticate(username, password);
		if (!isAuthenticated) {
			return null;
		}
		
		User user  = adminManager.getUser(username);
		ModelMapper modelMapper = new ModelMapper();
		userDTO = modelMapper.map(user , UserDTO.class);		
		return userDTO;
	}

	@RequestMapping("/user")
	public Principal user(HttpServletRequest request) {
		String authToken = request.getHeader("Authorization").substring("Basic".length()).trim();
		return () -> new String(Base64.getDecoder().decode(authToken)).split(":")[0];
	}
}
