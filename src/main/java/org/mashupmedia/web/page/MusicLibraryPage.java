package org.mashupmedia.web.page;

import org.mashupmedia.model.library.MusicLibrary;

public class MusicLibraryPage extends LibraryPage{
	
	private MusicLibrary musicLibrary;
	boolean isExists;
	
	public boolean isExists() {
		return isExists;
	}
	
	public boolean getIsExists() {
		return isExists();
	}

	public void setExists(boolean isExists) {
		this.isExists = isExists;
	}

	public MusicLibrary getMusicLibrary() {
		return musicLibrary;
	}

	public void setMusicLibrary(MusicLibrary musicLibrary) {
		this.musicLibrary = musicLibrary;
	}
	
}
