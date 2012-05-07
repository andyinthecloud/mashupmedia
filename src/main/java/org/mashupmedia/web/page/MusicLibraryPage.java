package org.mashupmedia.web.page;

import org.mashupmedia.model.library.MusicLibrary;

public class MusicLibraryPage extends LibraryPage{
	
	private MusicLibrary musicLibrary;

	public MusicLibrary getMusicLibrary() {
		return musicLibrary;
	}

	public void setMusicLibrary(MusicLibrary musicLibrary) {
		this.musicLibrary = musicLibrary;
	}
	
}
