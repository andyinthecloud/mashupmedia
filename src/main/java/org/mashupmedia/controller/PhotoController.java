package org.mashupmedia.controller;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PhotoManager;
import org.mashupmedia.service.PhotoManager.PhotoSequenceType;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.view.MediaItemImageView;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.PhotoPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/photo")
public class PhotoController extends BaseController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConnectionManager connectionManager;
	
	@Autowired
	private PhotoManager photoManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb photosBreadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.photos"), "/app/photos");
		breadcrumbs.add(photosBreadcrumb);
		Breadcrumb photoBreadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.photo"));
		breadcrumbs.add(photoBreadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "photo.title";
	}

	@RequestMapping(value = "/thumbnail/{photoId}", method = RequestMethod.GET)
	public ModelAndView getThumbnail(@PathVariable("photoId") Long photoId,
			Model model) throws Exception {
		ModelAndView modelAndView = getPhotoModelAndView(photoId,
				ImageType.THUMBNAIL);
		return modelAndView;
	}

	@RequestMapping(value = "/original/{photoId}", method = RequestMethod.GET)
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

	@RequestMapping(value = "/show/{photoId}", method = RequestMethod.GET)
	public String getPhotoPage(@PathVariable("photoId") Long photoId,
			Model model) throws Exception {
		MediaItem mediaItem = mediaManager.getMediaItem(photoId);
		if (!(mediaItem instanceof Photo)) {
			logger.error("Expecting a photo got " + mediaItem.getClass()
					+ " from id = " + mediaItem.getId());
			return null;
		}
		
		Photo photo = (Photo) mediaItem;
		Photo previousPhoto = photoManager.getPhotoInSequence(photo, PhotoSequenceType.PREVIOUS);
		Photo nextPhoto = photoManager.getPhotoInSequence(photo, PhotoSequenceType.NEXT);
		
		
		PhotoPage photoPage = new PhotoPage();
		photoPage.setPhoto(photo);
		photoPage.setPreviousPhoto(previousPhoto);
		photoPage.setNextPhoto(nextPhoto);
		
		model.addAttribute("photoPage", photoPage);
		return "photo/show";
	}

}
