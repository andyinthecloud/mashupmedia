package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;

public interface LibraryDao {

	public List<MusicLibrary> getMusicLibraries();

	public void saveMusicLibrary(MusicLibrary musicLibrary);

	public MusicLibrary getMusicLibrary(long id);

	public void deleteLibrary(Library library);

	public MusicLibrary getMusicLibrary(String name);
	
	
}
