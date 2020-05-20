package org.mashupmedia.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.AlbumArtManager;
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
import org.mashupmedia.web.page.AlbumPage;
import org.mashupmedia.web.page.ArtistPage;
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

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/music")
@Slf4j
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

	@Autowired
	private MusicManager musicManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private PlaylistManager playlistManager;
	
	@Autowired
	private AlbumArtManager albumArtManager;
	

	@Override
	public String getPageTitleMessageKey() {
		return "music.title";
	}


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
		breadcrumbs.add(getMusicBreadcrumb());
	}

	protected Breadcrumb getMusicBreadcrumb() {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.music"), "/app/music/albums");
		return breadcrumb;
	}

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

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_IS_APPEND, BooleanUtils.toBoolean(isAppend));

		model.addAttribute("albums", albums);
		model.addAttribute(MusicAlbumListType.RANDOM);

		String pagePath = getPath(isFragment, "music.albums");

		return pagePath;
	}

	@RequestMapping(value = "/latest-albums", method = RequestMethod.GET)
	public String getLatestAlbums(
			@RequestParam(value = MashUpMediaConstants.PARAM_IS_APPEND, required = false) Boolean isAppend,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@RequestParam(value = PARAM_PAGE_NUMBER, required = false) Integer pageNumber, Model model) {

		if (pageNumber == null || pageNumber < 0) {
			pageNumber = 0;
		}

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_IS_APPEND, BooleanUtils.toBoolean(isAppend));

		List<Album> albums = musicManager.getLatestAlbums(pageNumber, MAX_ALBUMS);
		model.addAttribute("albums", albums);

		model.addAttribute(MusicAlbumListType.LATEST);

		String pagePath = getPath(isFragment, "music.albums");
		return pagePath;
	}

	@RequestMapping(value = "/albums", method = RequestMethod.GET)
	public String getAlbums(
			@RequestParam(value = MashUpMediaConstants.PARAM_IS_APPEND, required = false) Boolean isAppend,
			@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@RequestParam(value = PARAM_PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(value = "searchLetter", required = false) String searchLetter, Model model) {

		List<String> albumIndexLetters = musicManager.getAlbumIndexLetters();

		model.addAttribute("albumIndexLetters", albumIndexLetters);

		if (pageNumber == null || pageNumber < 0) {
			pageNumber = 0;
		}

		model.addAttribute(MashUpMediaConstants.MODEL_KEY_IS_APPEND, BooleanUtils.toBoolean(isAppend));

		if (searchLetter != null) {
			searchLetter = StringUtils.trimToEmpty(searchLetter);
			if (searchLetter.equals(".")) {
				searchLetter = "#";
			}
		}

		List<Album> albums = musicManager.getAlbums(searchLetter, pageNumber, MAX_ALBUMS);
		model.addAttribute("albums", albums);

		model.addAttribute(MusicAlbumListType.ALPHABETICAL);

		String pagePath = getPath(isFragment, "music.albums");
		return pagePath;
	}

	@RequestMapping(value = "/artists", method = RequestMethod.GET)
	public String getArtists(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment, Model model) {
		ArtistsPage artistsPage = new ArtistsPage();
		List<String> artistIndexLetters = musicManager.getArtistIndexLetters();
		artistsPage.setArtistIndexLetters(artistIndexLetters);
		List<Artist> artists = musicManager.getArtists();
		artistsPage.setArtists(artists);
		model.addAttribute(artistsPage);

		String pagePath = getPath(isFragment, "music.artists");
		return pagePath;
	}

	@RequestMapping(value = "/artist/{artistId}", method = RequestMethod.GET)
	public String getArtist(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@PathVariable("artistId") Long artistId, Model model) {
		Artist artist = musicManager.getArtist(artistId);
		ArtistPage artistPage = new ArtistPage();
		artistPage.setArtist(artist);

		model.addAttribute(artistPage);

		String pagePath = getPath(isFragment, "music.artist");
		return pagePath;
	}

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
			log.error("Unable to find album id: " + albumId);
			return null;
		}
		
		

		AlbumArtImage albumArtImage = album.getAlbumArtImage();

		byte[] imageBytes = null;

		try {
			imageBytes = connectionManager.getAlbumArtImageBytes(albumArtImage, imageType);
			if (imageBytes == null || imageBytes.length == 0) {
				// Try to regenerate the album art if the image is empty
				List<Song> songs = album.getSongs();
				if (songs != null && !songs.isEmpty()) {
					Song song = songs.get(0);
					MusicLibrary musicLibrary = (MusicLibrary) song.getLibrary();
					albumArtImage = albumArtManager.getAlbumArtImage(musicLibrary, song);
					imageBytes = connectionManager.getAlbumArtImageBytes(albumArtImage, imageType);					
				}				
			}
			
		} catch (IOException e) {
			log.info("Unable to read album art: " + albumArtImage.getUrl(), e);
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

	protected List<Breadcrumb> prepareBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(getHomeBreadcrumb());
		breadcrumbs.add(getMusicBreadcrumb());
		return breadcrumbs;
	}

	@RequestMapping(value = "/album/{albumId}", method = RequestMethod.GET)
	public String getAlbum(@RequestParam(value = PARAM_FRAGMENT, required = false) Boolean isFragment,
			@PathVariable("albumId") Long albumId, Model model) throws Exception {
		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		AlbumPage albumPage = new AlbumPage();
		albumPage.setAlbum(album);
		albumPage.setSongs(songs);
		model.addAttribute(albumPage);

		List<Breadcrumb> breadcrumbs = prepareBreadcrumbs();
		breadcrumbs.add(new Breadcrumb(MessageHelper.getMessage("breadcrumb.music.album")));
		model.addAttribute(MODEL_KEY_BREADCRUMBS, breadcrumbs);

		// prepareBreadcrumbs(breadcrumbs);

		String pagePath = getPath(isFragment, "music.album");
		return pagePath;
	}

}
