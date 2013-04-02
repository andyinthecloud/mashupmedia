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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.util.DateHelper;
import org.mashupmedia.util.DateHelper.DateFormatType;
import org.mashupmedia.util.WebHelper.WebFormatType;
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
		Set<RemoteShare> remoteShares = library.getRemoteShares();
		if (remoteShares == null) {
			remoteShares = new HashSet<RemoteShare>();
		}

		remoteShares = new HashSet<RemoteShare>(remoteShares);
		RemoteShare remoteShare = new RemoteShare();
		remoteShare.setUniqueName(LibraryHelper.createUniqueName());		
		remoteShares.add(remoteShare);
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

	private ModelAndView getRemoteShares(Library library) {
		Set<RemoteShare> remoteShares = library.getRemoteShares();

		final JSONArray jsonArray = new JSONArray();
		if (remoteShares == null || remoteShares.isEmpty()) {
			remoteShares = new HashSet<RemoteShare>();
		}

		for (RemoteShare remoteShare : remoteShares) {
			JSONObject remoteSharePropertiesJson = new JSONObject();
			remoteSharePropertiesJson.put("createdBy", remoteShare.getCreatedBy().getName());
			remoteSharePropertiesJson.put("createdOn", DateHelper.parseToText(remoteShare.getCreatedOn(), DateFormatType.SHORT_DISPLAY_WITH_TIME));
			remoteSharePropertiesJson.put("id", remoteShare.getId());
			remoteSharePropertiesJson.put("lastAccessed", remoteShare.getLastAccessed());
			remoteSharePropertiesJson.put("remoteUrl", remoteShare.getRemoteUrl());
			remoteSharePropertiesJson.put("totalPlayedMediaItems", remoteShare.getTotalPlayedMediaItems());
			remoteSharePropertiesJson.put("unique", remoteShare.getUniqueName());

			JSONObject remoteShareJson = new JSONObject();
			remoteShareJson.put("remoteShare", remoteSharePropertiesJson);
			jsonArray.add(remoteShare);

		}

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(getContentType());

				response.getWriter().print(jsonArray.toString());

			}

			@Override
			public String getContentType() {
				return WebFormatType.JSON.getContentType();
			}
		});

		return modelAndView;
	}

}
