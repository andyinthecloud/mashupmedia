/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.controller.ajax;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ajax/media/")
public class AjaxMediaItemController {

	public static final String MODEL_KEY_IS_SUCCESSFUL = "isSuccessful";

	@Autowired
	@Lazy
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private MusicManager musicManager;

//	@RequestMapping(value = "/encode/{mediaItemId}/{mediaContentTypeValue}", method = RequestMethod.GET, produces = "application/json")
//	public @ResponseBody
//	boolean handleEncodeMediaItem(@PathVariable long mediaItemId, @PathVariable String mediaContentTypeValue) {
//		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
//		if (mediaContentType == MediaContentType.UNSUPPORTED) {
//			return false;
//		}
//
//		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
//		encodeMediaItemTaskManager.processMediaItemForEncoding(mediaItem, mediaContentType);
//		return true;
//	}
//
//	@RequestMapping(value = "/encode/album/{albumId}/{mediaContentTypeValue}", method = RequestMethod.GET, produces = "application/json")
//	public @ResponseBody
//	boolean handleEncodeAlbum(@PathVariable long albumId, @PathVariable String mediaContentTypeValue) {
//		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
//		if (mediaContentType == MediaContentType.UNSUPPORTED) {
//			return false;
//		}
//
//		boolean isSuccessful = encodeAlbum(albumId, mediaContentType);
//		return isSuccessful;
//	}
//
//	@RequestMapping(value = "/encode/album/{albumId}", method = RequestMethod.GET, produces = "application/json")
//	public @ResponseBody
//	boolean handleDefaultEncodeAlbum(@PathVariable long albumId) {
//		MediaContentType mediaContentType = MediaContentType.MP3;
//		boolean isSuccessful = encodeAlbum(albumId, mediaContentType);
//		return isSuccessful;
//	}
//
//	protected boolean encodeAlbum(long albumId, MediaContentType mediaContentType) {
//		Album album = musicManager.getAlbum(albumId);
//		if (album == null) {
//			return false;
//		}
//
//		List<Song> songs = album.getSongs();
//		for (Song song : songs) {
//			encodeMediaItemTaskManager.processMediaItemForEncoding(song, mediaContentType);
//		}
//
//		return true;
//	}
//
//
//	@RequestMapping(value = "/encode/artist/{artistId}/{mediaContentTypeValue}", method = RequestMethod.GET, produces = "application/json")
//	public @ResponseBody
//	boolean handleEncodeArtist(@PathVariable long artistId, @PathVariable String mediaContentTypeValue) {
//		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaContentTypeValue);
//		if (mediaContentType == MediaContentType.UNSUPPORTED) {
//			return false;
//		}
//
//		boolean isSuccessful = encodeArtist(artistId, mediaContentType);
//		return isSuccessful;
//	}
//
//	@RequestMapping(value = "/encode/artist/{artistId}", method = RequestMethod.GET, produces = "application/json")
//	public @ResponseBody
//	boolean handleDefaultEncodeArtist(@PathVariable long artistId) {
//		MediaContentType mediaContentType = MediaContentType.MP3;
//		boolean isSuccessful = encodeArtist(artistId, mediaContentType);
//		return isSuccessful;
//	}
//	
//	protected boolean encodeArtist(long artistId, MediaContentType mediaContentType) {
//		Artist artist = musicManager.getArtist(artistId);
//		if (artist == null) {
//			return false;
//		}
//
//		List<Album> albums = artist.getAlbums();
//		for (Album album : albums) {
//			long albumId = album.getId();
//			encodeAlbum(albumId, mediaContentType);
//		}
//
//		return true;
//	}

	@RequestMapping(value = "/{mediaItemId}", method = RequestMethod.GET)
	public String handleGetOriginalMediaFormat(@PathVariable Long mediaItemId, Model model) {
		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		model.addAttribute("mediaItem", mediaItem);
		String format = mediaItem.getFormat();
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(format);
		model.addAttribute("jPlayerFormat", mediaContentType.getjPlayerContentType());
		return "ajax.media.media-item";
	}

	@RequestMapping(value = "/ffmpeg/status", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	boolean handleGetIsFfMpegInstalled() {
		boolean isFfMpegInstalled = BooleanUtils.toBoolean(configurationManager
				.getConfigurationValue(MashUpMediaConstants.IS_FFMPEG_INSTALLED));
		return isFfMpegInstalled;
	}

}
