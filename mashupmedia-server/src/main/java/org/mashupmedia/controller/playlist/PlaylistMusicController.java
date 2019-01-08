package org.mashupmedia.controller.playlist;

import java.util.List;

import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/playlist/music")
public class PlaylistMusicController extends AbstractPlaylistController {

	@Override
	protected String getPlaylistPath() {
		return "playlist.music";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb musicBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.music"), "/app/music/albums");
		breadcrumbs.add(musicBreadcrumb);
		
		Breadcrumb listPlaylistsBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.playlist"));
		breadcrumbs.add(listPlaylistsBreadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "music.playlist.title";
	}

	@Override
	protected PlaylistType getPlaylistType() {
		return PlaylistType.MUSIC;
	}

}
