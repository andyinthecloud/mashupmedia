package org.mashupmedia.task;

import java.util.List;

import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.LibraryUpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LibraryTaskScheduler {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private LibraryManager libraryManager;
	
	@Autowired
	private LibraryUpdateManager libraryUpdateManager;
	
	
	public void updateLibraries() {
		List<Library> libraries = libraryManager.getLocalLibraries(LibraryType.ALL);
		for (Library library : libraries) {
			logger.info("About to update library: " + library.getName());
			if (library.isRemote()) {
				continue;
			}
			libraryUpdateManager.updateLibrary(library);
			logger.info("Library updated: " + library.getName());
		}
	}

}
