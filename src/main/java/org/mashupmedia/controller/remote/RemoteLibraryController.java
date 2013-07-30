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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.StringHelper.Encoding;
import org.mashupmedia.util.WebHelper.WebContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AdminManager adminManager;

	@RequestMapping(value = "/stream/{uniqueName}/{mediaItemId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView handleStreamMediaItem(HttpServletRequest request, @PathVariable String uniqueName, @PathVariable Long mediaItemId, Model model)
			throws IOException {
		Library remoteLibrary = getRemoteLibrary(uniqueName, request, true);
		if (remoteLibrary == null) {
			logger.info("Unable to stream remote media, unknown host: " + request.getRemoteHost());
			return null;
		}

		logInAsSystemuser(request);

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

		String encodedPath = "unprocessed";
		EncodeStatusType encodeStatusType = mediaItem.getEncodeStatusType();
		if (encodeStatusType == EncodeStatusType.ENCODED) {
			encodedPath = "encoded";
		}

		StringBuilder servletPathBuilder = new StringBuilder(request.getServletPath());
		servletPathBuilder.append("/streaming/media");
		servletPathBuilder.append("/" + encodedPath);
		servletPathBuilder.append("/" + mediaItemId);
		return new ModelAndView("forward:" + servletPathBuilder.toString());
	}

	@RequestMapping(value = "/album-art/{uniqueName}/{imageTypeValue}/{songId}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView handleAlbumArt(HttpServletRequest request, @PathVariable String uniqueName, @PathVariable String imageTypeValue,
			@PathVariable Long songId, Model model) throws IOException {
		Library remoteLibrary = getRemoteLibrary(uniqueName, request, true);
		if (remoteLibrary == null) {
			logger.info("Unable to load album art, unknown host: " + request.getRemoteHost());
			return null;
		}

		logInAsSystemuser(request);

		ImageType imageType = ImageHelper.getImageType(imageTypeValue);
		StringBuilder servletPathBuilder = new StringBuilder(request.getServletPath());
		servletPathBuilder.append("/music/album-art");
		servletPathBuilder.append("/" + imageType.toString().toLowerCase());
		MediaItem mediaItem = mediaManager.getMediaItem(songId);
		if (mediaItem == null || !(mediaItem instanceof Song)) {
			return null;
		}
		Song song = (Song) mediaItem;
		Album album = song.getAlbum();
		long albumId = album.getId();

		servletPathBuilder.append("/" + albumId);
		return new ModelAndView("forward:" + servletPathBuilder.toString());
	}

	@RequestMapping(value = "/connect/{libraryType}/{uniqueName}", method = RequestMethod.GET)
	public ModelAndView handleConnectRemoteLibrary(HttpServletRequest request, @PathVariable String libraryType, @PathVariable String uniqueName,
			Model model) throws IOException {

		Library remoteLibrary = getRemoteLibrary(uniqueName, request, false);
		if (remoteLibrary == null) {
			logger.info("Unable to connect to remote library, unknown host: " + request.getRemoteHost());
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

	protected void logInAsSystemuser(HttpServletRequest request) {

		User systemUser = adminManager.getSystemUser();
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(systemUser.getUsername(), systemUser.getPassword());

		// Authenticate the user
		Authentication authentication = authenticationManager.authenticate(authRequest);
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authentication);

		// Create a new session and add the security context.
		HttpSession session = request.getSession(true);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
	}

	private Library getRemoteLibrary(String connectingUniqueName, HttpServletRequest request, boolean isIncreaseAccessedMediaItems) {
		Library remoteLibrary = libraryManager.getRemoteLibrary(connectingUniqueName);
		String remoteHost = request.getRemoteHost();

		if (isValidRemoteLibrary(remoteLibrary, connectingUniqueName, remoteHost, isIncreaseAccessedMediaItems)) {
			return remoteLibrary;
		}

		return null;

	}

	private boolean isValidRemoteLibrary(Library remoteLibrary, String connectingUniqueName, String connectingRemoteHost,
			boolean isIncreaseAccessedMediaItems) {
		if (remoteLibrary == null) {
			return false;
		}

		List<RemoteShare> remoteShares = remoteLibrary.getRemoteShares();
		if (remoteShares == null || remoteShares.isEmpty()) {
			return false;
		}

		RemoteShare remoteShare = null;

		for (RemoteShare rs : remoteShares) {
			if (!connectingUniqueName.equals(rs.getUniqueName())) {
				continue;
			}

			String remoteUrl = rs.getRemoteUrl();
			if (StringUtils.isBlank(remoteUrl)) {
				rs.setRemoteUrl(connectingRemoteHost);
				remoteShare = rs;

				break;
			}

			if (remoteUrl.equals(connectingRemoteHost)) {
				remoteShare = rs;
				break;
			}

		}

		if (remoteShare == null) {
			return false;
		}

		remoteShare.setLastAccessed(new Date());
		if (isIncreaseAccessedMediaItems) {
			long totalPlayedMediaItems = remoteShare.getTotalPlayedMediaItems();
			remoteShare.setTotalPlayedMediaItems(++totalPlayedMediaItems);
		}

		libraryManager.saveLibrary(remoteLibrary);

		return true;
	}

}
