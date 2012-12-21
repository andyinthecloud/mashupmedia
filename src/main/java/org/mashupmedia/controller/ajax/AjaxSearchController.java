package org.mashupmedia.controller.ajax;

import java.util.List;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.criteria.MediaItemSearchCriteria.MediaSortType;
import org.mashupmedia.exception.PageNotFoundException;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.PlaylistHelper;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ajax/search")
public class AjaxSearchController extends BaseAjaxController {
	

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private MusicManager musicManager;
	
	@Autowired
	private PlaylistManager playlistManager;

	@RequestMapping(value = "/media-items-autocomplete", method = RequestMethod.POST)
	public String handleMediaItemsAutocomplete(@RequestParam("searchWords") String searchWords, Model model) {
		List<String> suggestions = mediaManager.findAutoCompleteMediaItems(searchWords);
		model.addAttribute("suggestions", suggestions);
		return "ajax/search/suggestions";
	}

	@RequestMapping(value = "/media-items", method = RequestMethod.POST)
	public String handleMediaItems(@RequestParam(value = "mediaType", required = false) String mediaTypeValue,
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam("searchWords") String searchWords,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "isAscending", required = false) Boolean isAscending,
			@RequestParam(value = "action", required = false) String action, @RequestParam(value = "maximumResults", required = false) Integer maximumResults, Model model) {

		MediaItemSearchCriteria mediaItemSearchCriteria = new MediaItemSearchCriteria();
		
		if (maximumResults != null && maximumResults < 500) {
			mediaItemSearchCriteria.setMaximumResults(maximumResults);
		}

		MediaType mediaType = MediaItemHelper.getMediaType(mediaTypeValue);
		if (mediaType == null) {
			mediaType = MediaType.SONG;
		}
		mediaItemSearchCriteria.setMediaType(mediaType);

		if (pageNumber == null) {
			pageNumber = 0;
		}
		mediaItemSearchCriteria.setPageNumber(pageNumber);
		mediaItemSearchCriteria.setSearchWords(searchWords);

		if (isAscending == null) {
			isAscending = true;
		}

		mediaItemSearchCriteria.setAscending(isAscending);

		MediaSortType mediaSortType = MediaItemHelper.getMediaSortType(orderBy);
		mediaItemSearchCriteria.setMediaSortType(mediaSortType);

		List<MediaItem> mediaItems = null;

		if (mediaType == MediaType.SONG) {
			mediaItems = musicManager.findSongs(mediaItemSearchCriteria);
		} else {
			mediaItems = mediaManager.findMediaItems(mediaItemSearchCriteria);
		}

		ActionType actionType = WebHelper.getActionType(action);
		
		String page = preparePage(model, actionType, pageNumber, mediaItems, mediaSortType, mediaType, isAscending);
		return page;
//		
//		
//		if (actionType == ActionType.NONE) {
//			if (pageNumber == 0 && mediaItems.isEmpty()) {
//				return "ajax/search/no-results";
//			}
//
//			model.addAttribute("orderBy", mediaSortType.toString().toLowerCase());
//			model.addAttribute("isAscending", isAscending);
//
//			if (mediaType == MediaType.SONG) {
//				model.addAttribute("pageNumber", pageNumber);
//				model.addAttribute("songs", mediaItems);
//				return "ajax/search/songs";
//
//			}
//			
//		}
//		
//		
//
//		throw new PageNotFoundException("");

	}
	
	private String preparePage(Model model, ActionType actionType, int pageNumber, List<MediaItem> mediaItems, MediaSortType mediaSortType, MediaType mediaType, boolean isAscending) {
		if (actionType == ActionType.NONE) {
			if (pageNumber == 0 && mediaItems.isEmpty()) {
				return "ajax/search/no-results";
			}

			model.addAttribute("orderBy", mediaSortType.toString().toLowerCase());
			model.addAttribute("isAscending", isAscending);

			if (mediaType == MediaType.SONG) {
				model.addAttribute("pageNumber", pageNumber);
				model.addAttribute("songs", mediaItems);
				return "ajax/search/songs";

			}
			
		} 
		
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.MUSIC);
		
		if (actionType == ActionType.APPEND) {
			
			PlaylistHelper.appendPlaylist(playlist, mediaItems);
			playlistManager.savePlaylist(playlist);

			model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_IS_SUCCESSFUL, true);
			model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_CODE, MashUpMediaConstants.MODEL_KEY_JSON_MESSAGE_SUCCESS);
			return "ajax/json/response";

		} 
		
		if (actionType == ActionType.PLAY) {
			PlaylistHelper.replacePlaylist(playlist, mediaItems);
			playlistManager.savePlaylist(playlist);

			MediaItem mediaItem = PlaylistHelper.getRelativePlayingMediaItemFromPlaylist(playlist, 0);
			model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_MEDIA_ITEM, mediaItem);
			model.addAttribute(MashUpMediaConstants.MODEL_KEY_JSON_PLAYLIST, playlist);
			return "ajax/json/media-item";
		}
		
		throw new PageNotFoundException("");
		
	}
}
