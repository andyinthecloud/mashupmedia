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

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/ajax/media/")
public class AjaxMediaItemController {

	public static final String MODEL_KEY_IS_SUCCESSFUL = "isSuccessful";

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@Autowired
	private MediaManager mediaManager;

	@RequestMapping(value = "/encode/{mediaItemId}", method = RequestMethod.GET)
	public String handleEncodeHtml5(@PathVariable Long mediaItemId, Model model) {
		encodeMediaItemTaskManager.encodeMediaItem(mediaItemId);
		model.addAttribute(MODEL_KEY_IS_SUCCESSFUL, true);
		return "ajax/message";
	}

	@RequestMapping(value = "/format-unprocessed/{mediaItemId}", method = RequestMethod.GET)
	public String handleSetFormatUnprocessed(@PathVariable Long mediaItemId, Model model) {
		String page = setMediaEncodeStatus(mediaItemId, EncodeStatusType.OVERRIDE, model);
		return page;
	}
	
	@RequestMapping(value = "/format-encoded/{mediaItemId}", method = RequestMethod.GET)
	public String handleSetFormatEncoded(@PathVariable Long mediaItemId, Model model) {		
		String page = setMediaEncodeStatus(mediaItemId, EncodeStatusType.ENCODED, model);
		return page;
	}

	protected String setMediaEncodeStatus(long mediaItemId, EncodeStatusType encodeStatusType, Model model) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		mediaItem.setEncodeStatusType(encodeStatusType);
		mediaManager.saveMediaItem(mediaItem);
		model.addAttribute(MODEL_KEY_IS_SUCCESSFUL, true);
		return "ajax/message";
	}
	

	@RequestMapping(value = "/{mediaItemId}", method = RequestMethod.GET)
	public String handleGetOriginalMediaFormat(@PathVariable Long mediaItemId, Model model) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		model.addAttribute("mediaItem", mediaItem);
		String originalMediaFormat = mediaItem.getFormat();
		MediaContentType mediaContentType = WebHelper.getMediaContentType(originalMediaFormat, MediaContentType.MP3);
		model.addAttribute("jPlayerFormat", mediaContentType.getjPlayerContentType());
		return "ajax/media/media-item";
	}

}
