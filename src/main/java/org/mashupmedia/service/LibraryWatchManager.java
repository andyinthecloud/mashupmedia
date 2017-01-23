package org.mashupmedia.service;

public interface LibraryWatchManager {

	public void registerWatchLibraryListeners();
	
	public void removeWatchLibraryListener(long libraryId);

}
