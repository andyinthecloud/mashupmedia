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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.encode.FfMpegManager;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EncodingPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EncodingConfigurationController extends BaseController {

	private final static String PAGE_NAME = "encoding";
	private final static String PAGE_PATH = "configuration/" + PAGE_NAME;
	private final static String PAGE_URL = "/" + PAGE_PATH;

	private Logger logger = Logger.getLogger(getClass());

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
		Breadcrumb configurationBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration"), "/app/configuration");
		breadcrumbs.add(configurationBreadcrumb);

		Breadcrumb networkBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.configuration.encoding"));
		breadcrumbs.add(networkBreadcrumb);
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.GET)
	public String getNetwork(Model model) {
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
		configurationManager.saveConfiguration(MashUpMediaConstants.IS_ENCODER_INSTALLED, BooleanUtils.toStringTrueFalse(isFfmpegValid));
		configurationManager.saveConfiguration(MashUpMediaConstants.FFMPEG_PATH, ffMpegFilePath);

		model.addAttribute(encodingPage);
		return PAGE_PATH;
	}

}
