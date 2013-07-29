package org.mashupmedia.controller.music;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.controller.BaseController;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

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

		final byte[] imageBytes = connectionManager.getAlbumArtImageBytes(albumArtImage, imageType);

		Song remoteSong = getFirstRemoteSongInAlbum(album);

		if (remoteSong != null && isEmptyBytes(imageBytes)) {
			Library library = remoteSong.getLibrary();
			Location location = library.getLocation();
			String path = location.getPath();
			path = LibraryHelper.getRemoteAlbumArtPath(path);
			long remoteMediaItemId = NumberUtils.toLong(remoteSong.getPath());

			if (StringUtils.isNotBlank(path) && remoteMediaItemId > 0) {
				String imageTypeValue = imageType.toString().toLowerCase();
				return new ModelAndView("forward:" + path + "/" + imageTypeValue + "/" + remoteMediaItemId);
			}
		}

		final String contentType = WebHelper.getImageContentType(albumArtImage);
		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				if (isEmptyBytes(imageBytes)) {
					response.sendRedirect(request.getContextPath() + "/images/no-album-art.png");
					return;
				}
				ServletOutputStream outputStream = response.getOutputStream();
				try {
					IOUtils.write(imageBytes, outputStream);
					outputStream.flush();
				} finally {
					IOUtils.closeQuietly(outputStream);
				}
			}

			@Override
			public String getContentType() {
				if (StringUtils.isBlank(contentType)) {
					return "image/png";
				}

				return contentType;
			}
		});
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

	private boolean isEmptyBytes(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return true;
		}
		return false;
	}

}
