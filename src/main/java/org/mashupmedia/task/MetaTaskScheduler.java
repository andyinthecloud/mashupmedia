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

package org.mashupmedia.task;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.ProxyManager;
import org.mashupmedia.service.ProxyManager.ProxyType;
import org.mashupmedia.web.proxy.ProxyTextFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetaTaskScheduler {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ProxyManager proxyManager;

	@Autowired
	private ConfigurationManager configurationManager;

	public void getMashupMediaLatestReleaseInformation() {
		String url = "http://www.mashupmedia.org/latest-release/final";
		try {
			ProxyTextFile proxyTextFile = (ProxyTextFile) proxyManager.loadProxyFile(url, ProxyType.TEXT_FILE);
			Document document = Jsoup.parse(proxyTextFile.getText());
			Elements elements = document.select("div.view-latest-final-release div.views-row");
			String releaseType = elements.select("div.views-field-field-release-type").text();
			String version = elements.select("div.views-field-field-version").text();
			logger.info("Found latest release information, type = " + releaseType + ", version = " + version);
			configurationManager.saveConfiguration(MashUpMediaConstants.LATEST_RELEASE_FINAL_VERSION, version);
		} catch (IOException e) {
			logger.error("Unable to get latest version information from www.mashupmedia.org", e);
			return;
		}

	}

}
