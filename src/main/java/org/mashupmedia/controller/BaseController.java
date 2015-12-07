package org.mashupmedia.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

	protected final static String MODEL_KEY_HAS_ERRORS = "hasErrors";
	protected final static String MODEL_KEY_BREADCRUMBS = "breadcrumbs";
	protected final static String MODEL_KEY_HEAD_PAGE_TITLE = "headPageTitle";	
	protected final static String FRAGMENT_PARAM = "fragment";
	protected final static String FRAGMENT_APPEND_PATH = "/fragment";
	protected final static String PAGE_NUMBER_PARAM = "pageNumber";

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private ConfigurationManager configurationManager;

	protected Breadcrumb getHomeBreadcrumb() {
		Breadcrumb breadcrumb = new Breadcrumb(
				MessageHelper.getMessage("breadcrumb.home"), "/app/home");
		return breadcrumb;
	}

	
//	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_IS_TRANSPARENT_BACKGROUND)
//	public boolean isTransparentBackground() {
//		return true;
//	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_VERSION)
	public String getVersion() {
		String version = MessageHelper.getMessage("application.version");
		return version;
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_CURRENT_YEAR)
	public String getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return String.valueOf(year);
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_THEME_PATH)
	public String getThemePath() {
		return "/themes/default";
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_IS_NEW_MASHUP_MEDIA_AVAILABLE)
	public boolean isNewMashupMediaVersionAvailable() {
		String latestVersionValue = configurationManager
				.getConfigurationValue(MashUpMediaConstants.LATEST_RELEASE_FINAL_VERSION);
		double latestVersion = NumberUtils.toDouble(latestVersionValue);
		if (latestVersion == 0) {
			return false;
		}

		double currentVersion = NumberUtils.toDouble(MessageHelper
				.getMessage(MashUpMediaConstants.APPLICATION_VERSION));

		if (latestVersion > currentVersion) {
			return true;
		}

		return false;
	}

	@ModelAttribute(MODEL_KEY_BREADCRUMBS)
	public List<Breadcrumb> populateBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(getHomeBreadcrumb());
		prepareBreadcrumbs(breadcrumbs);
		return breadcrumbs;
	}

	public abstract void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs);

	public abstract String getPageTitleMessageKey();

	@ModelAttribute(MODEL_KEY_HEAD_PAGE_TITLE)
	public String populateHeadPageTitle() {
		StringBuilder titleBuilder = new StringBuilder(
				MessageHelper.getMessage("page.default.title.prefix"));
		titleBuilder.append(" ");
		titleBuilder.append(MessageHelper.getMessage(getPageTitleMessageKey()));
		return titleBuilder.toString();
	}

	@ModelAttribute("pageTitle")
	public String populatePageTitle() {
		return getPageTitleMessageKey();
	}

	@ModelAttribute("musicPlaylists")
	public List<Playlist> populatePlaylists() {
		List<Playlist> playlist = playlistManager
				.getPlaylistsForCurrentUser(PlaylistType.MUSIC);
		return playlist;
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_JQUERYUI_VERSION)
	public String populateJQueryUIVersion() {
		return MashUpMediaConstants.JQUERYUI_VERSION;
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_JQUERY_VERSION)
	public String populateJQuery() {
		return MashUpMediaConstants.JQUERY_VERSION;
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_JQUERY_MOBILE_VERSION)
	public String populateJQueryMobile() {
		return MashUpMediaConstants.JQUERY_MOBILE_VERSION;
	}

	
	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_JQUERY_ADDRESS_VERSION)
	public String populateJQueryAddress() {
		return MashUpMediaConstants.JQUERY_ADDRESS_VERSION;
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_JPLAYER_VERSION)
	public String populateJPlayer() {
		return MashUpMediaConstants.JPLAYER_VERSION;
	}

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_DATATABLES_VERSION)
	public String populateDataTables() {
		return MashUpMediaConstants.DATATABLES_VERSION;
	}

	
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(
				" \t\r\n\f", true));
	}

	@ModelAttribute("mediaType")
	public String populateMediaType() {
		return "";
	}
	

	protected String getPath(Boolean isFragment, String path) {
		if (isFragment == null) {
			return path;
		}
		
		if (isFragment) {
			return path + FRAGMENT_APPEND_PATH;
		}
		
		return path;
	}

}
