package org.mashupmedia.task;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.service.MusicLibraryUpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LibraryUpdateTaskManager {

    @Autowired
    private ThreadPoolTaskExecutor libraryUpdateThreadPoolTaskExecutor;
    
    @Autowired
    private MusicLibraryUpdateManager musicLibraryUpdateManager;

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
    		MusicLibrary musicLibrary = (MusicLibrary) library;
        	musicLibraryUpdateManager.updateRemoteLibrary(musicLibrary);
        }
    }

}
