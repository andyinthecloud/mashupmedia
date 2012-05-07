package org.mashupmedia.service;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;

public interface LibraryUpdateManager {
	
	public void updateMusicLibrary(MusicLibrary musicLibrary);

	public void updateLibrary(Library library);
	

}
