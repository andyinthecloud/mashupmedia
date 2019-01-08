/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.controller.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.encode.FfMpegManager;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.validator.EncodingPageValidator;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EncodingPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("encodingPage")
public class EncodingConfigurationController extends BaseController {

	private final static String PAGE_NAME = "encoding";
	private final static String PAGE_PATH = "configuration." + PAGE_NAME;
	private final static String PAGE_URL = "/configuration/" + PAGE_NAME;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private FfMpegManager encodeManager;

	@Override
	public String getPageTitleMessageKey() {
		return "encoding.title";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"),
				"/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.encoding"));
		breadcrumbs.add(networkBreadcrumb);
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.GET)
	public String getEncodingConfiguration(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			Model model) {
		EncodingPage encodingPage = new EncodingPage();
		String ffMpegFolderPath = encodeManager.getFFMpegFolderPath();
		encodingPage.setFfmpegFolderPath(ffMpegFolderPath);

		String ffMpegFilePath = "";
		File ffMpegFile = encodeManager.findFFMpegExecutable();
		boolean isFfmpegValid = false;
		if (ffMpegFile != null) {
			try {
				if (encodeManager.isValidFfMpeg(ffMpegFile)) {
					ffMpegFilePath = ffMpegFile.getAbsolutePath();
					isFfmpegValid = true;
				}
			} catch (IOException e) {
				encodingPage.setAdditionalErrorMessage(e.getLocalizedMessage());
				logger.error("Error running ffmpeg: " + ffMpegFilePath, e);
			}

		}
		encodingPage.setFfMpegFound(isFfmpegValid);
		configurationManager.saveConfiguration(MashUpMediaConstants.IS_FFMPEG_INSTALLED,
				BooleanUtils.toStringTrueFalse(isFfmpegValid));
		configurationManager.saveConfiguration(MashUpMediaConstants.FFMPEG_PATH, ffMpegFilePath);

		int totalFfmpegProcesses = NumberUtils
				.toInt(configurationManager.getConfigurationValue(ProcessManager.KEY_TOTAL_FFMPEG_PROCESSES));
		encodingPage.setTotalFfmpegProcesses(totalFfmpegProcesses);

		model.addAttribute(encodingPage);

		String pagePath = getPath(isFragment, PAGE_PATH);
		return pagePath;
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.POST)
	public String processSubmitUser(@ModelAttribute("encodingPage") EncodingPage encodingPage,
			BindingResult bindingResult, Model model) {

		new EncodingPageValidator().validate(encodingPage, bindingResult);
		if (bindingResult.hasErrors()) {
			return PAGE_PATH + FRAGMENT_APPEND_PATH;
		}

		int totalFfmpegprocesses = encodingPage.getTotalFfmpegProcesses();
		configurationManager.saveConfiguration(ProcessManager.KEY_TOTAL_FFMPEG_PROCESSES,
				String.valueOf(totalFfmpegprocesses));

		return "redirect:/app/" + PAGE_PATH + "?" + PARAM_FRAGMENT + "=true";
	}

}
