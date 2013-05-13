package org.mashupmedia.controller;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

	@Autowired
	private PlaylistManager playlistManager;

	protected Breadcrumb getHomeBreadcrumb() {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.home"), "/app/home");
		return breadcrumb;
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_THEME_PATH)
	public String getThemePath() {
		return "/themes/default";
	}

	@ModelAttribute("breadcrumbs")
	public List<Breadcrumb> populateBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(getHomeBreadcrumb());
		prepareBreadcrumbs(breadcrumbs);
		return breadcrumbs;
	}

	public abstract void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs);
	
	public abstract String getPageTitleMessageKey();
	
	@ModelAttribute("pageTitle")
	public String populatePageTitle() {
		StringBuilder titleBuilder = new StringBuilder(MessageHelper.getMessage("page.default.title.prefix"));
		titleBuilder.append(" ");
		titleBuilder.append(MessageHelper.getMessage(getPageTitleMessageKey()));
		return titleBuilder.toString();
	}
	

	@ModelAttribute("musicPlaylists")
	public List<Playlist> populatePlaylists() {
		List<Playlist> playlist = playlistManager.getPlaylistsForCurrentUser(PlaylistType.MUSIC);
		return playlist;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(" \t\r\n\f", true));
	}

}
