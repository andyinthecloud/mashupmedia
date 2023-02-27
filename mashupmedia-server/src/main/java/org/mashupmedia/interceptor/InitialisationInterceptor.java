package org.mashupmedia.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.InitialisationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitialisationInterceptor implements HandlerInterceptor {


	@Autowired
	private AdminManager adminManager;

	@Autowired
	private InitialisationManager initialisationManager;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		int totalUsers = adminManager.getTotalUsers();
		if (totalUsers == 0) {
			initialisationManager.initialiseApplication();
			log.info("Initialised mashupmedia.");
		}
		return true;
	}

}
