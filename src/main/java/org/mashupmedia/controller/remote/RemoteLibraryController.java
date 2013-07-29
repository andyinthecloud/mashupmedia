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

package org.mashupmedia.controller.remote;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.StringHelper.Encoding;
import org.mashupmedia.util.WebHelper.WebContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/remote")
public class RemoteLibraryController {

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private MediaManager mediaManager;

	@RequestMapping(value = "/stream/{mediaItemId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView handleStreamMediaItem(HttpServletRequest request, @PathVariable Long mediaItemId, Model model) throws IOException {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

		String encodedPath = "unprocessed";
		EncodeStatusType encodeStatusType = mediaItem.getEncodeStatusType();
		if (encodeStatusType == EncodeStatusType.ENCODED) {
			encodedPath = "encoded";
		}

		String servletPath = request.getServletPath();
		servletPath = servletPath.replaceFirst("/remote/.*", "/streaming");		
		String path = servletPath  + "/" + encodedPath + "/" + mediaItemId;
		return new ModelAndView("forward:" + path);
	}


	@RequestMapping(value = "/album-art/{imageType}/{songId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView handleAlbumArt(HttpServletRequest request, @PathVariable String imageTypeValue, @PathVariable Long songId, Model model) throws IOException {
		ImageType imageType = ImageHelper.getImageType(imageTypeValue);
		String servletPath = request.getServletPath();
		servletPath = servletPath.replaceFirst("/remote/.*", "/music/album-art");		
		String path = servletPath  + "/" + imageType.toString().toLowerCase() + "/" + songId;
		return new ModelAndView("forward:" + path);
	}


	@RequestMapping(value = "/connect/{libraryType}/{uniqueName}", method = RequestMethod.GET)
	public ModelAndView handleConnectRemoteLibrary(HttpServletRequest request, @PathVariable String libraryType, @PathVariable String uniqueName,
			Model model) throws IOException {

		Library remoteLibrary = libraryManager.getRemoteLibrary(uniqueName);
		String remoteHost = request.getRemoteHost();
		if (!isValidRemoteLibrary(remoteLibrary, uniqueName, remoteHost)) {
			return null;
		}

		File file = FileHelper.getLibraryXmlFile(remoteLibrary.getId());
		final String xml = FileUtils.readFileToString(file, Encoding.UTF8.getEncodingString());

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(getContentType());
				response.getWriter().print(xml);
			}

			@Override
			public String getContentType() {
				return WebContentType.XML.getContentType();
			}
		});

		return modelAndView;
	}

	private boolean isValidRemoteLibrary(Library remoteLibrary, String connectingUniqueName, String connectingHostName) {
		if (remoteLibrary == null) {
			return false;
		}

		List<RemoteShare> remoteShares = remoteLibrary.getRemoteShares();
		if (remoteShares == null || remoteShares.isEmpty()) {
			return false;
		}

		for (RemoteShare remoteShare : remoteShares) {
			if (!connectingUniqueName.equals(remoteShare.getUniqueName())) {
				continue;
			}

			String remoteUrl = remoteShare.getRemoteUrl();
			if (StringUtils.isBlank(remoteUrl)) {
				remoteShare.setRemoteUrl(connectingHostName);
				libraryManager.saveLibrary(remoteLibrary);
				return true;
			}

			if (remoteUrl.equals(connectingHostName)) {
				return true;
			}

		}

		return false;
	}

}
