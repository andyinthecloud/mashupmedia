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

import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.EncodeHelper;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.EncodingPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EncodingConfigurationController extends BaseController{

	private final static String PAGE_NAME = "encoding";
	private final static String PAGE_PATH = "configuration/" + PAGE_NAME;
	private final static String PAGE_URL = "/" + PAGE_PATH;
	
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;
	
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
		String ffMpegFolderPath = EncodeHelper.getFFMpegFolderPath();
		encodingPage.setFfmpegFolderPath(ffMpegFolderPath);
		
		String ffMpegFilePath = "";
		File ffMpegFile = EncodeHelper.findFFMpegExecutable();
		if (ffMpegFile != null) {
			try {
				if (EncodeHelper.isValidFfMpeg(ffMpegFile)) {
					ffMpegFilePath = ffMpegFile.getAbsolutePath();
					encodingPage.setFfMpegFound(true);
				}
			} catch (IOException e) {
				encodingPage.setAdditionalErrorMessage(e.getLocalizedMessage());
				logger.error("Error running ffmpeg: " + ffMpegFilePath, e );
			}
				
			
		}
		configurationManager.saveConfiguration(MashUpMediaConstants.FFMPEG_PATH, ffMpegFilePath);
		
		model.addAttribute(encodingPage);
		return PAGE_PATH;
	}

	@RequestMapping(value = PAGE_URL, method = RequestMethod.POST)
	public String processNetwork(@ModelAttribute("encodingPage") EncodingPage encodingPage, Model model, BindingResult result) {				
		
		return "redirect:/app/configuration";
	}
	
	

}
