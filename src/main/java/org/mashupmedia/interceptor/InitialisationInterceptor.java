package org.mashupmedia.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.InitialisationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Service
public class InitialisationInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private InitialisationManager initialisationManager;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		int totalUsers = adminManager.getTotalUsers();
		if (totalUsers == 0) {
			initialisationManager.initialiseApplication();
			logger.info("Initialised mashupmedia.");
		}
		return super.preHandle(request, response, handler);
	}

}
