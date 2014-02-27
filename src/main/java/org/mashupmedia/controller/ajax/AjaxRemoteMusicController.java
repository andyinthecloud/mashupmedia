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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.restful.MusicWebService;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ajax/remote")
public class AjaxRemoteMusicController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	@Qualifier("lastFm")
	private MusicWebService mediaWebService;

	@Autowired
	private MusicManager musicManager;

	@RequestMapping(value = "/artist/search", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	List<RemoteMediaMetaItem> handleSearchArtists(@RequestParam("name") String name, Model model) {
		List<RemoteMediaMetaItem> remoteMediaMetasItems = new ArrayList<RemoteMediaMetaItem>();
		try {
			remoteMediaMetasItems = mediaWebService.searchArtist(name);
		} catch (Exception e) {
			logger.error("Error getting remote artist.", e);
		}

		return remoteMediaMetasItems;
	}

	@RequestMapping(value = "/artist/save", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	RemoteMediaMetaItem handleLinkArtistWithDiscogsId(@RequestParam("artistId") Long artistId,
			@RequestParam("remoteArtistId") String remoteArtistId, Model model) {
		Artist artist = musicManager.getArtist(artistId);

		RemoteMediaMetaItem remoteMediaMetaItem = new RemoteMediaMetaItem();

		try {
			remoteMediaMetaItem = mediaWebService.getArtistInformation(artist);
		} catch (Exception e) {
			logger.error("Error finding remote artist.", e);
			return remoteMediaMetaItem;
		}

		String name = StringUtils.trimToEmpty(remoteMediaMetaItem.getName());
		if (StringUtils.isEmpty(name)) {
			logger.info("Remote artist has no name.");
			return remoteMediaMetaItem;
		}

		artist.setName(name);
		artist.setRemoteId(remoteArtistId);
		musicManager.saveArtist(artist);

		return remoteMediaMetaItem;
	}

}
