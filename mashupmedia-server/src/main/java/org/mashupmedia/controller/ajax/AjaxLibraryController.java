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

package org.mashupmedia.controller.ajax;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.model.library.RemoteShare.RemoteShareStatusType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.DateHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.DateHelper.DateFormatType;
import org.mashupmedia.util.WebHelper.WebContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/ajax/library")
public class AjaxLibraryController {

	@Autowired
	private LibraryManager libraryManager;

	@RequestMapping(value = "/add-remote-share", method = RequestMethod.POST)
	public ModelAndView handleAddRemoteShare(@RequestParam("libraryId") Long libraryId, Model model) {

		Library library = libraryManager.getLibrary(libraryId);
		List<RemoteShare> remoteShares = library.getRemoteShares();
		if (remoteShares == null) {
			remoteShares = new ArrayList<RemoteShare>();
		}

		remoteShares = new ArrayList<RemoteShare>(remoteShares);
		RemoteShare remoteShare = new RemoteShare();
		remoteShare.setStatusType(RemoteShareStatusType.ENABLED);
		remoteShare.setUniqueName(LibraryHelper.createUniqueName());
		remoteShares.add(remoteShare);
		library.setRemoteShares(remoteShares);
		libraryManager.saveLibrary(library);

		ModelAndView modelAndView = getRemoteShares(library);
		return modelAndView;
	}

	@RequestMapping(value = "/get-remote-shares", method = RequestMethod.GET)
	public ModelAndView handleGetRemoteShares(@RequestParam("libraryId") Long libraryId, Model model) {
		Library library = libraryManager.getLibrary(libraryId);
		ModelAndView modelAndView = getRemoteShares(library);
		return modelAndView;
	}

	@RequestMapping(value = "/update-remote-shares", method = RequestMethod.POST)
	public ModelAndView handleUpdateRemoteShares(@RequestParam("libraryId") Long libraryId, @RequestParam("remoteShareIds[]") Long[] remoteShareIds,
			@RequestParam("remoteShareStatus") String remoteShareStatus, Model model) {

		remoteShareStatus = StringUtils.trimToEmpty(remoteShareStatus);
		if (remoteShareStatus.equalsIgnoreCase("delete")) {
			deleteRemoteShares(libraryId, remoteShareIds);

		} else {
			libraryManager.saveRemoteShares(remoteShareIds, remoteShareStatus);
		}

		Library library = libraryManager.getLibrary(libraryId);
		ModelAndView modelAndView = getRemoteShares(library);
		return modelAndView;
	}

	private void deleteRemoteShares(Long libraryId, Long[] remoteShareIds) {

		if (remoteShareIds == null || remoteShareIds.length == 0) {
			return;
		}

		Library library = libraryManager.getLibrary(libraryId);
		List<RemoteShare> remoteShares = library.getRemoteShares();
		if (remoteShares == null || remoteShares.isEmpty()) {
			return;
		}

		for (Long remoteShareId : remoteShareIds) {
			RemoteShare remoteShareToDelete = null;
			for (RemoteShare remoteShare : remoteShares) {
				if (remoteShare.getId() == remoteShareId) {
					remoteShareToDelete = remoteShare;
				}
			}
			if (remoteShareToDelete != null) {
				remoteShares.remove(remoteShareToDelete);
			}

		}

		libraryManager.saveLibrary(library);
	}

	private ModelAndView getRemoteShares(Library library) {
		List<RemoteShare> remoteShares = library.getRemoteShares();

		final JSONArray jsonArray = new JSONArray();
		if (remoteShares == null || remoteShares.isEmpty()) {
			remoteShares = new ArrayList<RemoteShare>();
		}

		for (RemoteShare remoteShare : remoteShares) {
			JSONObject remoteSharePropertiesJson = new JSONObject();
			remoteSharePropertiesJson.put("createdBy", remoteShare.getCreatedBy().getName());
			remoteSharePropertiesJson.put("createdOn", DateHelper.parseToText(remoteShare.getCreatedOn(), DateFormatType.SHORT_DISPLAY_WITH_TIME));
			remoteSharePropertiesJson.put("id", remoteShare.getId());
			String lastAccessedValue = "";
			Date lastAccessed = remoteShare.getLastAccessed();
			if (lastAccessed != null) {
				lastAccessedValue = DateHelper.parseToText(remoteShare.getLastAccessed(), DateFormatType.SHORT_DISPLAY_WITH_TIME);
			}

			remoteSharePropertiesJson.put("lastAccessed", lastAccessedValue);
			remoteSharePropertiesJson.put("remoteUrl", StringUtils.trimToEmpty(remoteShare.getRemoteUrl()));
			remoteSharePropertiesJson.put("totalPlayedMediaItems", remoteShare.getTotalPlayedMediaItems());
			remoteSharePropertiesJson.put("uniqueName", StringUtils.trimToEmpty(remoteShare.getUniqueName()));
			remoteSharePropertiesJson.put("status", remoteShare.getStatusType().toString());

			JSONObject remoteShareJson = new JSONObject();
			remoteShareJson.put("remoteShare", remoteSharePropertiesJson);
			jsonArray.add(remoteShareJson);

		}

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(getContentType());

				response.getWriter().print(jsonArray.toString());

			}

			@Override
			public String getContentType() {
				return WebContentType.JSON.getContentType();
			}
		});

		return modelAndView;
	}

}
