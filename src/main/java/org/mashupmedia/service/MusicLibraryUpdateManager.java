package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.Song;

public interface MusicLibraryUpdateManager {

	public void updateLibrary(Library library);
	
	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs);
	
	public void deleteSongs(List<Song> songs);
	
	public void deleteEmpty();
	

}
