package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ajax/encoding")
public class AjaxEncodingController {

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;
	
	private MusicManager musicManager;

	@RequestMapping(value = "/encode/{mediaItemId}/{mediaContentTypeValue}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	boolean handleEncodeMediaItem(@PathVariable long mediaItemId, @PathVariable String mediaContentTypeValue) {		
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		if (mediaContentType == MediaContentType.UNSUPPORTED) {
			return false;
		}
		
		encodeMediaItemTaskManager.queueMediaItemForEncoding(mediaItemId, mediaContentType);
		return true;
	}


	@RequestMapping(value = "/encode/album/{albumId}/{mediaContentTypeValue}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	boolean handleEncodeAlbum(@PathVariable long albumId, @PathVariable String mediaContentTypeValue) {
		
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
		if (mediaContentType == MediaContentType.UNSUPPORTED) {
			return false;
		}

		Album album = musicManager.getAlbum(albumId);
		if (album == null) {
			return false;
		}
		
		List<Song> songs =  album.getSongs();
		for (Song song : songs) {
			encodeMediaItemTaskManager.queueMediaItemForEncoding(song.getId(), mediaContentType);			
		}
		
		return true;
	}


}
