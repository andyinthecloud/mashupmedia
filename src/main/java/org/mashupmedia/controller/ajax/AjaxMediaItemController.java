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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ajax/media/")
public class AjaxMediaItemController {

	public static final String MODEL_KEY_IS_SUCCESSFUL = "isSuccessful";

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConfigurationManager configurationManager;

	@RequestMapping(value = "/encode/{mediaItemId}", method = RequestMethod.GET)
	public String handleEncodeHtml5(@PathVariable Long mediaItemId,
			@RequestParam(value = "mediaContentType", required = false) String mediaContentTypeValue, Model model) {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);

		MediaContentType mediaContentType = MediaContentType.UNSUPPORTED;
		mediaContentTypeValue = StringUtils.trimToEmpty(mediaContentTypeValue);
		if (StringUtils.isNotEmpty(mediaContentTypeValue)) {
			mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		}

		if (mediaContentType != MediaContentType.UNSUPPORTED) {
			String page = prepareEncodeMediaModel(mediaItemId, mediaContentType, model);
			return page;
		}

		mediaContentType = null;
		MediaEncoding mediaEncoding = mediaItem.getBestMediaEncoding();
		if (mediaEncoding != null) {
			mediaContentType = mediaEncoding.getMediaContentType();
			String page = prepareEncodeMediaModel(mediaItemId, mediaContentType, model);
			return page;
		}

		mediaContentType = MediaContentType.MP3;
		if (mediaItem.getMediaType() == MediaType.VIDEO) {
			mediaContentType = MediaContentType.MP4;
		}

		String page = prepareEncodeMediaModel(mediaItemId, mediaContentType, model);
		return page;
	}

	protected String prepareEncodeMediaModel(long mediaItemId, MediaContentType mediaContentType, Model model) {
		encodeMediaItemTaskManager.queueMediaItemForEncoding(mediaItemId, mediaContentType);
		model.addAttribute(MODEL_KEY_IS_SUCCESSFUL, true);
		return "ajax/message";
	}

	@RequestMapping(value = "/{mediaItemId}", method = RequestMethod.GET)
	public String handleGetOriginalMediaFormat(@PathVariable Long mediaItemId, Model model) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		model.addAttribute("mediaItem", mediaItem);
		String format = mediaItem.getFormat();
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(format);
		model.addAttribute("jPlayerFormat", mediaContentType.getjPlayerContentType());
		return "ajax/media/media-item";
	}

	@RequestMapping(value = "/ffmpeg/status", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	boolean handleGetIsFfMpegInstalled() {
		boolean isFfMpegInstalled = BooleanUtils.toBoolean(configurationManager
				.getConfigurationValue(MashUpMediaConstants.IS_FFMPEG_INSTALLED));
		return isFfMpegInstalled;
	}

}
