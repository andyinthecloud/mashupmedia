package org.mashupmedia.task;

import java.util.List;

import org.apache.log4j.Logger;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.LibraryManager.LibraryType;
import org.mashupmedia.service.MusicLibraryUpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MashupMediaTaskScheduler {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private LibraryManager libraryManager;
	
	@Autowired
	private MusicLibraryUpdateManager libraryUpdateManager;
	
	
	public void updateLibraries() {
		@SuppressWarnings("unchecked")
		List<MusicLibrary> musicLibraries = (List<MusicLibrary>)libraryManager.getLibraries(LibraryType.MUSIC);
		for (MusicLibrary musicLibrary : musicLibraries) {
			logger.info("About to update library: " + musicLibrary.getName());
			libraryUpdateManager.updateLibrary(musicLibrary);
			logger.info("Library updated: " + musicLibrary.getName());
		}
	}

}
