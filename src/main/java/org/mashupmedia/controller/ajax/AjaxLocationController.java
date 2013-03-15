package org.mashupmedia.controller.ajax;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.util.ProcessHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AjaxLocationController {

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ConnectionManager connectionManager;

	@RequestMapping(value = "/ajax/check-folder-location", method = RequestMethod.POST)
	public String checkFolderLocation(@RequestParam("path") String path, Model model) {
		File file = new File(path);
		boolean isValid = false;
		String messageCode = "musiclibrary.location.invalid";
		if (file.isDirectory()) {
			isValid = true;
			messageCode = "musiclibrary.location.ok";
		}
		model.addAttribute("isValid", isValid);
		model.addAttribute("messageCode", messageCode);
		return "ajax/configuration/check-folder-location";
	}

	@RequestMapping(value = "/ajax/check-ftp-location", method = RequestMethod.POST)
	public String checkFtpLocation(@RequestParam("host") String host, @RequestParam(required = false, value = "port") String portValue,
			@RequestParam(required = false, value = "path") String path, @RequestParam(required = false, value = "username") String username,
			@RequestParam(required = false, value = "password") String password, Model model) {

		FtpLocation ftpLocation = new FtpLocation();
		ftpLocation.setHost(host);
		Integer port = null;
		portValue = StringUtils.trimToEmpty(portValue);
		if (StringUtils.isNotEmpty(portValue)) {
			port = NumberUtils.toInt(portValue);
		}

		ftpLocation.setPort(port);
		ftpLocation.setPath(path);
		ftpLocation.setUsername(username);
		ftpLocation.setPassword(password);

		boolean isValid = false;
		String messageCode = "musiclibrary.location.ftp.invalid";
		if (connectionManager.isFtpLocationValid(ftpLocation)) {
			isValid = true;
			messageCode = "musiclibrary.location.ok";
		}

		model.addAttribute("isValid", isValid);
		model.addAttribute("messageCode", messageCode);
		return "ajax/configuration/check-folder-location";
	}

	@RequestMapping(value = "/ajax/check-ffmpeg", method = RequestMethod.POST)
	public String checkFfmpegLocation(@RequestParam("path") String path, Model model) {
		File file = new File(path);
		boolean isValid = false;

		String messageCode = "musiclibrary.location.invalid";
		if (file.isFile()) {
			isValid = true;
			messageCode = "musiclibrary.location.ok";
			try {
				ProcessHelper.callProcess(file.getAbsolutePath());
			} catch (IOException e) {
				isValid = false;
				logger.error("Error runnning process:  ", e);
			}
		}

		model.addAttribute("isValid", isValid);
		model.addAttribute("messageCode", messageCode);
		return "ajax/configuration/check-folder-location";
	}
}
