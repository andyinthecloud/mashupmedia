package org.mashupmedia.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.photo.Album;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/photo")
public class PhotoController extends BaseController {

	private Logger logger = Logger.getLogger(getClass());
	
	public static int MAXIMUM_PHOTOS = 50;
	private static String ATTRIBUTE_PHOTOS = "photos";


	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private PhotoManager photoManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
//		Breadcrumb albumsBreadcrumb = new Breadcrumb(
//				MessageHelper.getMessage("breadcrumb.photo.albums"),
//				"/app/photo/album/list");
//		breadcrumbs.add(albumsBreadcrumb);

		breadcrumbs.add(getLatestPhotoBreadcrumb());
		
	}
	
	protected Breadcrumb getLatestPhotoBreadcrumb() {
		Breadcrumb breadcrumb = new Breadcrumb("breadcrumb.photo", "/app/photo/album/list");
		return breadcrumb;
	}
	
	protected List<Breadcrumb> getLatestPhotoBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(getHomeBreadcrumb());
		breadcrumbs.add(getLatestPhotoBreadcrumb());
		return breadcrumbs;
	}
	

	@Override
	public String getPageTitleMessageKey() {
		return "photo.title";
	}

	
	@RequestMapping(method = RequestMethod.GET)
	public String handleGetPhotoList(
			@RequestParam(value = MashUpMediaConstants.PARAM_IS_APPEND, required = false) Boolean isAppend,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@RequestParam(value = PARAM_PAGE_NUMBER, required = false) Integer pageNumber, Model model) {
		
		model.addAttribute(MODEL_KEY_BREADCRUMBS, getLatestPhotoBreadcrumbs());
		
		List<Photo> photos = photoManager.getLatestPhotos(pageNumber, MAXIMUM_PHOTOS);
		model.addAttribute(ATTRIBUTE_PHOTOS, photos);
		String pagePath = getPath(isFragment, "photos.list-photos");
		return pagePath;
	}	
	
	@RequestMapping(value = "/thumbnail/{photoId}", method = RequestMethod.GET)
	public ModelAndView getThumbnail(@PathVariable("photoId") Long photoId,
			Model model) throws Exception {
		ModelAndView modelAndView = getPhotoModelAndView(photoId,
				ImageType.THUMBNAIL);
		return modelAndView;
	}

	@RequestMapping(value = "/web-optimised/{photoId}", method = RequestMethod.GET)
	public ModelAndView getWebOptimised(@PathVariable("photoId") Long photoId,
			Model model) throws Exception {
		ModelAndView modelAndView = getPhotoModelAndView(photoId,
				ImageType.WEB_OPTIMISED);
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
				photoBytes, MediaContentType.JPEG, MediaType.PHOTO));
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
		
		List<Breadcrumb> breadcrumbs = populateBreadcrumbs();
		Album album = photo.getAlbum();		
		Breadcrumb albumBreadcrumb = new Breadcrumb(album.getName(), "/app/photo/album/show/" + album.getId());		
		breadcrumbs.add(albumBreadcrumb);
		Breadcrumb photoBreadcrumb = new Breadcrumb(photo.getDisplayTitle());
		breadcrumbs.add(photoBreadcrumb);		
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);		
		
		Photo previousPhoto = photoManager.getPhotoInSequence(photo,
				PhotoSequenceType.PREVIOUS);
		Photo nextPhoto = photoManager.getPhotoInSequence(photo,
				PhotoSequenceType.NEXT);

		PhotoPage photoPage = new PhotoPage();
		photoPage.setPhoto(photo);
		photoPage.setPreviousPhoto(previousPhoto);
		photoPage.setNextPhoto(nextPhoto);

		model.addAttribute("photoPage", photoPage);
		return "photo.show";
	}

}
