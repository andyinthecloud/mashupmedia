package org.mashupmedia.task;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.LibraryUpdateManager;
import org.mashupmedia.service.LibraryWatchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryTaskScheduler {
	
	@Autowired
	@Lazy
	private LibraryManager libraryManager;
	
	@Autowired
	@Lazy
	private LibraryUpdateManager libraryUpdateManager;
	
	@Autowired
	@Lazy
	private LibraryWatchManager libraryWatchManager;
	
	
	public void updateLibraries() {
		List<Library> libraries = libraryManager.getLocalLibraries(LibraryType.ALL);
		for (Library library : libraries) {
			log.info("About to update library: " + library.getName());
			if (library.isRemote()) {
				continue;
			}
			libraryUpdateManager.asynchronousUpdateLibrary(library.getId());
			log.info("Library updated: " + library.getName());
		}
		
//		libraryWatchManager.registerWatchLibraryListeners();
	}

}
