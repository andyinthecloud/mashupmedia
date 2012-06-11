package org.mashupmedia.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.web.Breadcrumb;
import org.mashupmedia.web.page.MusicPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/music")
public class MusicController extends BaseController {

	private final int NUMBER_OF_RANDOM_ALBUMS = 50;

	@Autowired
	private MusicManager musicManager;
	@Autowired
	private ConnectionManager connectionManager;
	@Autowired
	private PlaylistManager playlistManager;

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.music"), "/app/music");
		breadcrumbs.add(breadcrumb);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getMusic(Model model) {
		MusicPage musicPage = new MusicPage();
		List<Album> albums = musicManager.getRandomAlbums(NUMBER_OF_RANDOM_ALBUMS);
		musicPage.setAlbums(albums);
		Playlist playlist = playlistManager.getLastAccessedMusicPlaylistForCurrentUser();
		// If the user has no playlist create a default one
		if (playlist == null) {
			playlist = new Playlist();
			playlist.setDefault(true);
		}
		musicPage.setPlaylist(playlist);
		
		model.addAttribute(musicPage);
		return "music";
	}

	protected void addBreadcrumbsToModel(Model model, String messageKey) {
		List<Breadcrumb> breadcrumbs = populateBreadcrumbs();
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage(messageKey));
		breadcrumbs.add(breadcrumb);
		model.addAttribute(breadcrumbs);

	}

	@RequestMapping(value = "/album-art/{albumId}", method = RequestMethod.GET)
	public ModelAndView getAlbumArt(@PathVariable("albumId") Long albumId, Model model) throws Exception {
		Album album = musicManager.getAlbum(albumId);
		AlbumArtImage image = album.getAlbumArtImage();

		final byte[] imageBytes = connectionManager.getAlbumArtImageBytes(image);
		final String contentType = WebHelper.getImageContentType(image);
		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				if (imageBytes == null || imageBytes.length == 0) {
					response.sendRedirect(request.getContextPath() + "/images/no-album-art.png");
					return;
				}

				ServletOutputStream outputStream = response.getOutputStream();
				IOUtils.write(imageBytes, outputStream);
				outputStream.flush();
				outputStream.close();
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

}
