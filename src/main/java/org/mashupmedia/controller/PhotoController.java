package org.mashupmedia.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.PhotoManager;
import org.mashupmedia.service.PhotoManager.PhotoSequenceType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.PhotoPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/photo")
public class PhotoController extends BaseController {

	private Logger logger = Logger.getLogger(getClass());

	public static int MAXIMUM_PHOTOS = 50;
	private static String MODEL_KEY_PHOTOS = "photos";
	private static String MODEL_KEY_ALBUM = "album";

	public enum PhotoListType {
		LATEST("photo-list-latest");

		private PhotoListType(String className) {
			this.className = className;
		}

		private String className;

		public String getClassName() {
			return className;
		}
	}

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private PhotoManager photoManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		breadcrumbs.add(getLatestPhotosBreadcrumb());
	}

	protected Breadcrumb getListAlbumsBreadcrumb() {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.photo.albums"),
				"/app/photo/albums");
		return breadcrumb;
	}

	protected Breadcrumb getLatestPhotosBreadcrumb() {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.photo.latest-photos"),
				"/app/photo/photos");
		return breadcrumb;
	}

	protected Breadcrumb getAlbumBreadcrumb(Album album) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.photo-album"),
				"/app/photo/album/" + album.getId());
		return breadcrumb;
	}

	protected List<Breadcrumb> getListAlbumsBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(getHomeBreadcrumb());
		breadcrumbs.add(getListAlbumsBreadcrumb());
		return breadcrumbs;
	}

	protected List<Breadcrumb> getLatestPhotosBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(getHomeBreadcrumb());
		breadcrumbs.add(getLatestPhotosBreadcrumb());
		return breadcrumbs;
	}

	protected List<Breadcrumb> getAlbumBreadcrumbs(Album album) {
		List<Breadcrumb> breadcrumbs = getListAlbumsBreadcrumbs();
		Breadcrumb albumBreadcrumb = getAlbumBreadcrumb(album);
		breadcrumbs.add(albumBreadcrumb);
		return breadcrumbs;
	}

	@Override
	public String getPageTitleMessageKey() {
		return "photo.title";
	}

	@RequestMapping(value = "/photos", method = RequestMethod.GET)
	public String handleGetPhotoList(
			@RequestParam(value = MashUpMediaConstants.PARAM_IS_APPEND, required = false) Boolean isAppend,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@RequestParam(value = PARAM_PAGE_NUMBER, required = false) Integer pageNumber, Model model) {

		model.addAttribute(MODEL_KEY_BREADCRUMBS, getLatestPhotosBreadcrumbs());

		if (pageNumber == null) {
			pageNumber = 0;
		}

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_IS_APPEND, BooleanUtils.toBoolean(isAppend));
		model.addAttribute(PhotoListType.LATEST);

		List<Photo> photos = photoManager.getLatestPhotos(pageNumber, MAXIMUM_PHOTOS);
		model.addAttribute(MODEL_KEY_PHOTOS, photos);
		String pagePath = getPath(isFragment, "photos.photos");
		return pagePath;
	}

	@RequestMapping(value = "/show/{photoId}", method = RequestMethod.GET)
	public String getPhotoPage(@PathVariable("photoId") Long photoId,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) throws Exception {
		MediaItem mediaItem = mediaManager.getMediaItem(photoId);
		if (!(mediaItem instanceof Photo)) {
			logger.error("Expecting a photo got " + mediaItem.getClass() + " from id = " + mediaItem.getId());
			return null;
		}

		Photo photo = (Photo) mediaItem;
		Album album = photo.getAlbum();

		List<Breadcrumb> breadcrumbs = getAlbumBreadcrumbs(album);
		Breadcrumb photoBreadCrumb = new Breadcrumb(photo.getDisplayTitle());
		breadcrumbs.add(photoBreadCrumb);
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);

		Photo previousPhoto = photoManager.getPhotoInSequence(photo, PhotoSequenceType.PREVIOUS);
		Photo nextPhoto = photoManager.getPhotoInSequence(photo, PhotoSequenceType.NEXT);

		PhotoPage photoPage = new PhotoPage();
		photoPage.setPhoto(photo);
		photoPage.setPreviousPhoto(previousPhoto);
		photoPage.setNextPhoto(nextPhoto);

		model.addAttribute("photoPage", photoPage);

		String pagePath = getPath(isFragment, "photo.show");
		return pagePath;
	}

	@RequestMapping(value = "/album/{albumId}", method = RequestMethod.GET)
	public String handleGetPhotoAlbum(@PathVariable("albumId") long albumId,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {

		Album album = photoManager.getAlbum(albumId);
		List<Breadcrumb> breadcrumbs = getAlbumBreadcrumbs(album);
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);
		
		model.addAttribute(MODEL_KEY_ALBUM, album);
		
		List<Photo> photos = album.getPhotos();
		model.addAttribute(MODEL_KEY_PHOTOS, photos);
		
		String pagePath = getPath(isFragment, "photos.photos");
		return pagePath;
	}

	@RequestMapping(value = "/albums", method = RequestMethod.GET)
	public String handleGetPhotoAlbumList(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			Model model) {
		
		List<Breadcrumb> breadcrumbs = getListAlbumsBreadcrumbs();
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);
		
		List<Album> albums = photoManager.getAlbums();
		model.addAttribute("albums", albums);

		String pagePath = getPath(isFragment, "photo.albums");
		return pagePath;
	}

}
