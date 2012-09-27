package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.FormatContentType;
import org.mashupmedia.web.page.AlbumPage;
import org.mashupmedia.web.page.AlbumsPage;
import org.mashupmedia.web.page.ArtistsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/ajax/music")
public class AjaxMusicController extends BaseAjaxController{

	@Autowired
	private MusicManager musicManager;
	
	@Autowired
	private MediaManager mediaManager;

	@RequestMapping(value = "/random-albums", method = RequestMethod.GET)
	public String getMusic(Model model) {
		List<Album> albums = musicManager.getRandomAlbums(30);
		model.addAttribute("albums", albums);
		return "ajax/music/random-albums";

	}

	@RequestMapping(value = "/album/{albumId}", method = RequestMethod.GET)
	public String getAlbum(@PathVariable("albumId") Long albumId, Model model) throws Exception {
		Album album = musicManager.getAlbum(albumId);
		List<Song> songs = album.getSongs();
		AlbumPage albumPage = new AlbumPage();
		albumPage.setAlbum(album);
		albumPage.setSongs(songs);
		model.addAttribute(albumPage);
		return "ajax/music/album";
	}

	@RequestMapping(value = "/albums", method = RequestMethod.GET)
	public String getAlbums(Model model) {
		AlbumsPage albumsPage = new AlbumsPage();
		List<Album> albums = musicManager.getAlbums();
		albumsPage.setAlbums(albums);
		model.addAttribute(albumsPage);
		return "ajax/music/albums";
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
	
	@RequestMapping(value = "/play/{mediaItemId}", method = RequestMethod.GET)
	public String playSong(@PathVariable("mediaItemId") Long mediaItemId, Model model) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		String format = WebHelper.getContentType(mediaItem.getFormat(), FormatContentType.JPLAYER);		
		model.addAttribute("format", format);
		model.addAttribute("mediaItemId", mediaItem.getId());
		return "ajax/music/player-script";
	}
	

}
