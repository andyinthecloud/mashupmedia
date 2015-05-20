package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.service.PhotoManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/photo/album/list")
public class ListPhotoAlbumsController extends BaseController {

	@Autowired
	private PhotoManager photoManager;

	@RequestMapping(method = RequestMethod.GET)
	public String handleGetPhotoAlbumList(Model model) {
		List<Album> albums = photoManager.getAlbums();
		model.addAttribute("albums", albums);
		return "photo/album/list";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.photo.albums"));
		breadcrumbs.add(breadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "photo-albums.title";
	}

}