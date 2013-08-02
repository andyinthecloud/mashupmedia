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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.restful.DiscogsWebService;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/ajax/discogs")
public class AjaxDiscogsController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DiscogsWebService discogsWebService;

	@Autowired
	private MusicManager musicManager;

	@RequestMapping(value = "/search-artist", method = RequestMethod.POST)
	public ModelAndView handleSearchArtists(@RequestParam("name") String name, Model model) {

		JSONArray jsonArray = new JSONArray();
		try {
			List<RemoteMediaMetaItem> remoteMediaMetas = discogsWebService.searchArtist(name);
			jsonArray = JSONArray.fromObject(remoteMediaMetas);
		} catch (Exception e) {
			logger.error("Error getting Discogs artist.", e);
		}

		final JSONArray finalJsonArray = jsonArray;

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(getContentType());
				response.getWriter().print(finalJsonArray.toString());
			}

			@Override
			public String getContentType() {
				return "application/json; charset=utf-8";
			}
		});

		return modelAndView;
	}

	@RequestMapping(value = "/save-artist", method = RequestMethod.POST)
	public ModelAndView handleLinkArtistWithDiscogsId(@RequestParam("artistId") Long artistId, @RequestParam("discogsId") String discogsId,
			Model model) {
		Artist artist = musicManager.getArtist(artistId);
		artist.setRemoteId(discogsId);
		musicManager.saveArtist(artist);

		JSONObject jsonObject = new JSONObject();
		try {
			RemoteMediaMetaItem remoteMediaMeta = discogsWebService.getDiscogsArtistMeta(discogsId);
			jsonObject = JSONObject.fromObject(remoteMediaMeta);
		} catch (Exception e) {
			logger.error("Error saving Discogs artist.", e);
		}

		final JSONObject finalJsonObject = jsonObject;
		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(getContentType());
				response.getWriter().print(finalJsonObject.toString());
			}

			@Override
			public String getContentType() {
				return "application/json; charset=utf-8";
			}
		});

		return modelAndView;
	}

	@RequestMapping(value = "/discogs-artist-id/{discogsArtistId}", method = RequestMethod.GET)
	public ModelAndView handleGetDiscogsId(@PathVariable String discogsArtistId, Model model) {

		JSONObject jsonObject = new JSONObject();
		try {
			RemoteMediaMetaItem remoteMediaMeta = discogsWebService.getDiscogsArtistMeta(discogsArtistId);
			jsonObject = JSONObject.fromObject(remoteMediaMeta);
		} catch (Exception e) {
			logger.error("Error getting Discogs artist.", e);
		}

		final JSONObject finalJsonObject = jsonObject;

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(getContentType());
				response.getWriter().print(finalJsonObject.toString());
			}

			@Override
			public String getContentType() {
				return "application/json; charset=utf-8";
			}
		});

		return modelAndView;
	}

}
