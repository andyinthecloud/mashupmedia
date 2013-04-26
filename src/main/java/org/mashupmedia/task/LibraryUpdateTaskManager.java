package org.mashupmedia.task;

import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryStatusType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.MusicLibraryUpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LibraryUpdateTaskManager {
	private Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ThreadPoolTaskExecutor libraryUpdateThreadPoolTaskExecutor;
    
    @Autowired
    private MusicLibraryUpdateManager musicLibraryUpdateManager;
    
    @Autowired
    private LibraryManager libraryManager;


    public void updateLibrary(Library library) {
    	libraryUpdateThreadPoolTaskExecutor.execute(new LibraryUpdateTask(library));
    }

    private class LibraryUpdateTask implements Runnable {

        private Library library;

        public LibraryUpdateTask(Library library) {
            this.library = library;
        }

        public void run() {
        	if (library instanceof MusicLibrary) {
        		MusicLibrary musicLibrary = (MusicLibrary) library;
        		musicLibraryUpdateManager.updateLibrary(musicLibrary);	
        	}
        	
        }
    }

	public void updateRemoteLibrary(Library remoteLibrary) {
		libraryUpdateThreadPoolTaskExecutor.execute(new RemoteLibraryUpdateTask(remoteLibrary));
	}

	
    private class RemoteLibraryUpdateTask implements Runnable {

        private Library library;

        public RemoteLibraryUpdateTask(Library library) {
            this.library = library;
        }

        public void run() {
        	if (library instanceof MusicLibrary) {
        		MusicLibrary musicLibrary = (MusicLibrary) library;
            	try {
					musicLibraryUpdateManager.updateRemoteLibrary(musicLibrary);
				} catch (Exception e) {
					logger.error(e);
					musicLibrary.setStatus(LibraryStatusType.ERROR);
					libraryManager.saveLibrary(musicLibrary);
				}        		
        	}
        }
    }

}
