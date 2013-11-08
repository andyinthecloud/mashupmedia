package org.mashupmedia.controller.ajax;

import java.io.File;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.util.EncodeHelper;
import org.mashupmedia.util.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AjaxLocationController {

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private ConfigurationManager configurationManager;

	@RequestMapping(value = "/ajax/check-folder-location", method = RequestMethod.POST)
	public String checkFolderLocation(@RequestParam("path") String path, Model model) {
		File file = new File(path);
		boolean isValid = false;
		String messageCode = "library.location.invalid";
		if (file.isDirectory()) {
			isValid = true;
			messageCode = "musiclibrary.location.ok";
		}
		model.addAttribute("isValid", isValid);
		model.addAttribute("messageCode", messageCode);
		return "ajax/configuration/check-folder-location";
	}


	@RequestMapping(value = "/ajax/check-ffmpeg", method = RequestMethod.GET)
	public String checkFfmpegLocation(Model model) {

		boolean isValid = false;
		String ffmpegStatusText = MessageHelper.getMessage("encoding.ffmpeg.path.invalid");
		String ffMpegFilePath = "";
		File ffMpegFile = EncodeHelper.findFFMpegExecutable();
		if (ffMpegFile != null) {
			ffMpegFilePath = ffMpegFile.getAbsolutePath();
			ffmpegStatusText = MessageHelper.getMessage("encoding.ffmpeg.path.valid");
			isValid = true;
		}

		configurationManager.saveConfiguration(MashUpMediaConstants.FFMPEG_PATH, ffMpegFilePath);

		model.addAttribute("isValid", isValid);
		model.addAttribute("messageCode", ffmpegStatusText);
		return "ajax/configuration/check-folder-location";
	}
}
