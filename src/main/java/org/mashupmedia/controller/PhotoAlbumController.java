package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.service.PhotoManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/photo/album")
public class PhotoAlbumController extends BaseController {

	@Autowired
	private PhotoManager photoManager;

	@Override
	public boolean isTransparentBackground() {
		return false;
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.photo.albums"), "/app/photo/album/list");
		breadcrumbs.add(breadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "photo-albums.title";
	}

	@RequestMapping(value = "/show/{albumId}", method = RequestMethod.GET)
	public String handleGetPhotoAlbum(@PathVariable("albumId") long albumId,
			Model model) {
		
		Album album = photoManager.getAlbum(albumId);

		List<Breadcrumb> breadcrumbs = populateBreadcrumbs();
		Breadcrumb albumBreadcrumb = new Breadcrumb(album.getName());		
		breadcrumbs.add(albumBreadcrumb);
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);
				
		model.addAttribute("album", album);
		return "photo/album/show";
	}

}
