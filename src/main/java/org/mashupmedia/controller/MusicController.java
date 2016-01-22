package org.mashupmedia.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.view.MediaItemImageView;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.ArtistsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/music")
public class MusicController extends BaseController {

	private final static int MAX_ALBUMS = 60;

	public enum MusicAlbumListType {
		RANDOM("music-random-albums"), LATEST("music-latest-albums"), ALPHABETICAL("music-alphabetical-albums");

		private MusicAlbumListType(String className) {
			this.className = className;
		}

		private String className;

		public String getClassName() {
			return className;
		}
	}

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MusicManager musicManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private PlaylistManager playlistManager;

	@Autowired
	private ConfigurationManager configurationManager;

	@Override
	public String getPageTitleMessageKey() {
		return "music.title";
	}

	// @Override
	// @ModelAttribute("isTransparentBackground")
	// public boolean isTransparentBackground() {
	// return false;
	// }

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_IS_PLAYLIST_OWNER)
	public boolean isPlaylistOwner() {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.ALL);
		User createdBy = playlist.getCreatedBy();
		User user = AdminHelper.getLoggedInUser();

		// If the createdBy is null presume that the user has just created this
		// playlist
		if (createdBy == null) {
			return true;
		}

		if (createdBy.equals(user)) {
			return true;
		}

		return false;

	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.music"), "/app/music");
		breadcrumbs.add(breadcrumb);
	}

	/*
	 * @RequestMapping(method = RequestMethod.GET) public String
	 * getMusic(@RequestParam(value = FRAGMENT_PARAM, required = false) Boolean
	 * isFragment, Model model) { String pagePath = getPath(isFragment,
	 * PAGE_PATH); return pagePath; }
	 */

	protected void addBreadcrumbsToModel(Model model, String messageKey) {
		List<Breadcrumb> breadcrumbs = populateBreadcrumbs();
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage(messageKey));
		breadcrumbs.add(breadcrumb);
		model.addAttribute(breadcrumbs);

	}

	@RequestMapping(value = "/random-albums", method = RequestMethod.GET)
	public String getRandomAlbums(
			@RequestParam(value = MashUpMediaConstants.PARAM_IS_APPEND, required = false) Boolean isAppend,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {
		List<Album> albums = musicManager.getRandomAlbums(MAX_ALBUMS);

		if (isAppend == null) {
			isAppend = false;
		}
		model.addAttribute("isAppend", isAppend);

		model.addAttribute("albums", albums);
		model.addAttribute(MusicAlbumListType.RANDOM);

		String pagePath = getPath(isFragment, "music/albums");

		return pagePath;
	}

	/*
	 * @RequestMapping(value = "/append-random-albums", method =
	 * RequestMethod.GET) public String getAppendRandomAlbums(Model model) {
	 * List<Album> albums = musicManager.getRandomAlbums(MAX_ALBUMS);
	 * model.addAttribute("isAppend", true); model.addAttribute("albums",
	 * albums); return "/tiles/music/albums"; }
	 */

	@RequestMapping(value = "/latest-albums", method = RequestMethod.GET)
	public String getLatestAlbums(
			@RequestParam(value = MashUpMediaConstants.PARAM_IS_APPEND, required = false) Boolean isAppend,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, 
			@RequestParam(value = PARAM_PAGE_NUMBER, required = false) Integer pageNumber, 			
			Model model) {
		
		if(pageNumber == null || pageNumber < 0) {
			pageNumber = 0;
		}
		
		List<Album> albums = musicManager.getLatestAlbums(pageNumber, MAX_ALBUMS);

		if (isAppend == null) {
			isAppend = false;
		}
		model.addAttribute("isAppend", isAppend);

		model.addAttribute("albums", albums);
		model.addAttribute(MusicAlbumListType.LATEST);

		String pagePath = getPath(isFragment, "music/albums");

		return pagePath;
	}
	
	
	@RequestMapping(value = "/artists", method = RequestMethod.GET)
	public String getArtists(Model model) {
		ArtistsPage artistsPage = new ArtistsPage();
		List<String> artistIndexLetters = musicManager.getArtistIndexLetters();
		artistsPage.setArtistIndexLetters(artistIndexLetters);
		List<Artist> artists = musicManager.getArtists();
		artistsPage.setArtists(artists);
		model.addAttribute(artistsPage);
		return "ajax/music/artists";
	}

	/*
	 * @RequestMapping(value = "/append-latest-albums", method =
	 * RequestMethod.GET) public String
	 * getAppendLatestAlbums(@RequestParam(value = PAGE_NUMBER_PARAM, required =
	 * true) int pageNumber, Model model) { List<Album> albums =
	 * musicManager.getLatestAlbums(pageNumber, MAX_ALBUMS);
	 * model.addAttribute("isAppend", true); model.addAttribute("albums",
	 * albums); return "/tiles/music/albums"; }
	 */

	@RequestMapping(value = "/album-art/{imageType}/{albumId}", method = RequestMethod.GET)
	public ModelAndView getAlbumArt(@PathVariable("imageType") String imageTypeValue,
			@PathVariable("albumId") Long albumId, Model model) throws Exception {
		ImageType imageType = ImageHelper.getImageType(imageTypeValue);
		ModelAndView modelAndView = getAlbumArtModelAndView(albumId, imageType);
		return modelAndView;
	}

	protected ModelAndView getAlbumArtModelAndView(Long albumId, ImageType imageType) throws Exception {

		Album album = musicManager.getAlbum(albumId);
		if (album == null) {
			logger.error("Unable to find album id: " + albumId);
			return null;
		}

		AlbumArtImage albumArtImage = album.getAlbumArtImage();

		byte[] imageBytes = null;

		try {
			imageBytes = connectionManager.getAlbumArtImageBytes(albumArtImage, imageType);
		} catch (IOException e) {
			logger.info("Unable to read album art: " + albumArtImage.getUrl(), e);
		}

		Song remoteSong = getFirstRemoteSongInAlbum(album);

		if (remoteSong != null && FileHelper.isEmptyBytes(imageBytes)) {
			Library library = remoteSong.getLibrary();
			Location location = library.getLocation();
			String path = location.getPath();
			path = LibraryHelper.getRemoteAlbumArtPath(path);
			long remoteMediaItemId = NumberUtils.toLong(remoteSong.getPath());

			if (StringUtils.isNotBlank(path) && remoteMediaItemId > 0) {
				String imageTypeValue = imageType.toString().toLowerCase();
				String redirectUrl = path + "/" + imageTypeValue + "/" + remoteMediaItemId;
				return new ModelAndView(new RedirectView(redirectUrl));
			}
		}

		MediaContentType mediaContentType = MediaContentType.UNSUPPORTED;
		if (albumArtImage != null) {
			if (imageType == ImageType.THUMBNAIL) {
				mediaContentType = MediaContentType.JPEG;
			} else {
				mediaContentType = MediaItemHelper.getMediaContentType(albumArtImage.getContentType());
			}
		}

		ModelAndView modelAndView = new ModelAndView(
				new MediaItemImageView(imageBytes, mediaContentType, MediaType.SONG));
		return modelAndView;
	}

	private Song getFirstRemoteSongInAlbum(Album album) {
		List<Song> songs = album.getSongs();
		if (songs == null || songs.isEmpty()) {
			return null;
		}

		for (Song song : songs) {
			Library library = song.getLibrary();
			if (library.isRemote()) {
				return song;
			}
		}

		return null;
	}

	@Override
	public String populateMediaType() {
		return "music";
	}

}
