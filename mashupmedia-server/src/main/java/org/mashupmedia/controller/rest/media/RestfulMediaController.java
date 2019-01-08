package org.mashupmedia.controller.rest.media;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful/media")
public class RestfulMediaController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MediaManager mediaManager;
	
	@RequestMapping(value = "/save-media-name", method = RequestMethod.POST)
	public String saveMediaName(@RequestParam(value = "id") String id, @RequestParam(value = "value") String value) {
		id = StringUtils.trimToEmpty(id);
		if (StringUtils.isEmpty(id)) {
			logger.info("Unable to save mediaItem name without id. Id = " + id);
			return value;
		}
		long mediaItemId = NumberUtils.toLong(id.replaceAll("\\D", ""));
		
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			logger.info("Unable to find mediaItem with id: " + mediaItemId);
			return value;
		}
		
		value = StringUtils.trimToEmpty(value);
		if (StringUtils.isEmpty(value)) {
			logger.info("Unable to save empty mediaItem name.");
			return value;
		}		
				
		mediaItem.setDisplayTitle(value);
		mediaManager.saveMediaItem(mediaItem);
		return value;
	}
	
}
