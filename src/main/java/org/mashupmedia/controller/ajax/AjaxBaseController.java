package org.mashupmedia.controller.ajax;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.User;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.service.PlaylistManager;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AjaxBaseController {
	
	@Autowired
	private PlaylistManager playlistManager;

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_THEME_PATH)
	public String getThemePath() {
		return "/themes/default";
	}
	
	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_IS_PLAYLIST_OWNER)
	public boolean isPlaylistOwner() {
		Playlist playlist = playlistManager.getLastAccessedPlaylistForCurrentUser(PlaylistType.ALL);
		User createdBy = playlist.getCreatedBy();
		User user = AdminHelper.getLoggedInUser();
		
		// If the createdBy is null presume that the user has just created this playlist
		if (createdBy == null) {
			return true;
		}
		
		if (createdBy.equals(user)) {
			return true;
		}
		
		return false;
	
	}
	
	

}
