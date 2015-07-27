package org.mashupmedia.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.view.MediaItemImageView;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/music")
public class MusicController extends BaseController {

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

	@Override
	@ModelAttribute("isTransparentBackground")
	public boolean isTransparentBackground() {
		return false;
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.music"), "/app/music");
		breadcrumbs.add(breadcrumb);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getMusic(Model model) {
		model.addAttribute("orderBy", "song-title");
		model.addAttribute("ascending", true);
		model.addAttribute("mediaType", "song");
		model.addAttribute("searchWords", "");

		return "music";
	}

	protected void addBreadcrumbsToModel(Model model, String messageKey) {
		List<Breadcrumb> breadcrumbs = populateBreadcrumbs();
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage(messageKey));
		breadcrumbs.add(breadcrumb);
		model.addAttribute(breadcrumbs);

	}

	@RequestMapping(value = "/album-art/{imageType}/{albumId}", method = RequestMethod.GET)
	public ModelAndView getAlbumArt(@PathVariable("imageType") String imageTypeValue, @PathVariable("albumId") Long albumId, Model model)
			throws Exception {
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

		ModelAndView modelAndView = new ModelAndView(new MediaItemImageView(imageBytes, mediaContentType, MediaType.SONG));
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
