package org.mashupmedia.controller;

import java.util.List;

import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/playlist/music")
public class MusicPlaylistController extends PlaylistController {

	@Override
	protected String getPlaylistPath() {
		return "playlist/music";
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb musicBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.music"), "/app/music");
		breadcrumbs.add(musicBreadcrumb);
	}

	@Override
	public String getPageTitleMessageKey() {
		return "music.playlist.title";
	}

}
