package org.mashupmedia.controller.rest.media;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/restful/media")
@Slf4j
public class RestfulMediaController {
	
	
	@Autowired
	private MediaManager mediaManager;
	
	@RequestMapping(value = "/save-media-name", method = RequestMethod.POST)
	public String saveMediaName(@RequestParam(value = "id") String id, @RequestParam(value = "value") String value) {
		id = StringUtils.trimToEmpty(id);
		if (StringUtils.isEmpty(id)) {
			log.info("Unable to save mediaItem name without id. Id = " + id);
			return value;
		}
		long mediaItemId = NumberUtils.toLong(id.replaceAll("\\D", ""));
		
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		if (mediaItem == null) {
			log.info("Unable to find mediaItem with id: " + mediaItemId);
			return value;
		}
		
		value = StringUtils.trimToEmpty(value);
		if (StringUtils.isEmpty(value)) {
			log.info("Unable to save empty mediaItem name.");
			return value;
		}		
				
		mediaItem.setDisplayTitle(value);
		mediaManager.saveMediaItem(mediaItem);
		return value;
	}
	
}
