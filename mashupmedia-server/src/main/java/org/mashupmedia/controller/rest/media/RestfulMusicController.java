package org.mashupmedia.controller.rest.media;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.service.MusicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful/media/music")
public class RestfulMusicController extends RestfulMediaController{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MusicManager musicManager;
	
	@RequestMapping(value = "/save-album-name", method = RequestMethod.POST)
	public String saveAlbumName(@RequestParam(value = "id") String id, @RequestParam(value = "value") String value) {
		id = StringUtils.trimToEmpty(id);
		if (StringUtils.isEmpty(id)) {
			logger.info("Unable to save album name without id. Id = " + id);
			return value;
		}
		long albumId = NumberUtils.toLong(id.replaceAll("\\D", ""));
		
		Album album = musicManager.getAlbum(albumId);
		if (album == null) {
			logger.info("Unable to find album with id: " + albumId);
			return value;
		}
		
		 
		
		value = StringUtils.trimToEmpty(value);
		if (StringUtils.isEmpty(value)) {
			logger.info("Unable to save empty album name.");
			return value;
		}		
				
		album.setName(value);
		musicManager.saveAlbum(album);
		String savedAlbumName = album.getName();
		return savedAlbumName;
	}
	
	@RequestMapping(value = "/save-artist-name", method = RequestMethod.POST)
	public String saveArtistName(@RequestParam(value = "id") String id, @RequestParam(value = "value") String value) {
		id = StringUtils.trimToEmpty(id);
		if (StringUtils.isEmpty(id)) {
			logger.info("Unable to save artist name without id. Id = " + id);
			return value;
		}
		long artistId = NumberUtils.toLong(id.replaceAll("\\D", ""));
		
		Artist artist = musicManager.getArtist(artistId);
		if (artist == null) {
			logger.info("Unable to find artist with id: " + artistId);
			return value;
		}
		
		value = StringUtils.trimToEmpty(value);
		if (StringUtils.isEmpty(value)) {
			logger.info("Unable to save empty artist name.");
			return value;
		}		
				
		artist.setName(value);
		musicManager.saveArtist(artist);
		String savedArtistName = artist.getName();
		return savedArtistName;
	}

}
