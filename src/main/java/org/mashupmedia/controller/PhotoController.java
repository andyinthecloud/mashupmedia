package org.mashupmedia.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.view.MediaItemImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/photo")
public class PhotoController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConnectionManager connectionManager;

	@RequestMapping(value = "/thumbnail/{photoId}", method = RequestMethod.GET)
	public ModelAndView getThumbnail(@PathVariable("photoId") Long photoId,
			Model model) throws Exception {
		ModelAndView modelAndView = getPhotoModelAndView(photoId,
				ImageType.THUMBNAIL);
		return modelAndView;
	}

	@RequestMapping(value = "/orignal/{photoId}", method = RequestMethod.GET)
	public ModelAndView getOriginal(@PathVariable("photoId") Long photoId,
			Model model) throws Exception {
		ModelAndView modelAndView = getPhotoModelAndView(photoId,
				ImageType.ORIGINAL);
		return modelAndView;
	}

	protected ModelAndView getPhotoModelAndView(long photoId,
			ImageType imageType) throws IOException {
		MediaItem mediaItem = mediaManager.getMediaItem(photoId);
		if (!(mediaItem instanceof Photo)) {
			logger.error("Expecting a photo got " + mediaItem.getClass()
					+ " from id = " + mediaItem.getId());
			return null;
		}

		Photo photo = (Photo) mediaItem;

		byte[] photoBytes = connectionManager.getPhotoBytes(photo, imageType);
		ModelAndView modelAndView = new ModelAndView(new MediaItemImageView(
				photoBytes, MediaContentType.PNG, MediaType.PHOTO));
		return modelAndView;
	}

}
