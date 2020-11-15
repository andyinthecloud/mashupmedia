package org.mashupmedia.controller.playlist;

import java.util.List;

import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/playlist/list/music")
public class ListMusicPlaylistsController extends AbstractListPlaylistsController {

	@Override
	protected PlaylistType getPlaylistType() {
		return PlaylistType.MUSIC;
	}

	@Override
	public void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		Breadcrumb musicBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.music"), "/music");
		breadcrumbs.add(musicBreadcrumb);

		Breadcrumb listPlaylistsBreadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.playlists"));
		breadcrumbs.add(listPlaylistsBreadcrumb);
	}

}
