package org.mashupmedia.web.page;

import org.mashupmedia.model.library.MusicLibrary;

public class MusicLibraryPage extends LibraryPage {

	private MusicLibrary musicLibrary;
	boolean exists;

	public boolean isExists() {
		return exists;
	}
	
	public boolean getIsExists() {
		return isExists();
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public MusicLibrary getMusicLibrary() {
		return musicLibrary;
	}

	public void setMusicLibrary(MusicLibrary musicLibrary) {
		this.musicLibrary = musicLibrary;
	}

}
