package org.mashupmedia.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.watch.WatchLibraryListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LibraryWatchManagerImpl implements LibraryWatchManager {


	@Autowired
	private LibraryManager libraryManager;

	private WatchThread watchThread = null;

	private List<WatchLibraryListener> watchLibraryListeners;

	@Override
	public void registerWatchLibraryListeners() {

		if (watchThread == null) {
			watchThread = new WatchThread();
			watchThread.start();
			return;
		}
		
		watchThread.removeWatchLibraries();
		watchThread.interrupt();
		watchThread = new WatchThread();
		watchThread.start();
	}
	
	
	@Override
	public void removeWatchLibraryListeners() {
		if (watchThread == null) {
			return;
		}

		if (!watchThread.isAlive()) {
			return;
		}
		
		watchThread.removeWatchLibraries();
	}
	

	@Override
	public void removeWatchLibraryListener(long libraryId) {
		if (watchLibraryListeners == null || watchLibraryListeners.isEmpty()) {
			return;
		}

		for (Iterator<WatchLibraryListener> iterator = watchLibraryListeners.iterator(); iterator.hasNext();) {
			WatchLibraryListener watchLibrary = (WatchLibraryListener) iterator.next();
			watchLibrary.setActive(false);			
			if (libraryId == watchLibrary.getLibrayId()) {
				watchLibrary.cancel();
				watchLibraryListeners.remove(watchLibrary);
				log.info("Watch libray removed. Library id = " + libraryId);
				return;
			}
		}
	}

	private void registerWatchLibraryListener(Library library) {

		if (library.isRemote()) {
			return;
		}

		long libraryId = library.getId();
		Location location = library.getLocation();
		String path = location.getPath();
		registerWatchLibraryListener(libraryId, path);

	}

	private void registerWatchLibraryListener(long libraryId, String pathValue) {

		Path path = Paths.get(pathValue);

		try {
			WatchLibraryListener watchLibraryListener = new WatchLibraryListener(libraryId, path, libraryManager);
			watchLibraryListener.setActive(true);
			watchLibraryListener.processEvents();
			addWatchLibraryListener(watchLibraryListener);
		} catch (IOException e) {
			log.error("Error creating watch library", e);
		}

	}

	private void addWatchLibraryListener(WatchLibraryListener watchLibraryListener) {
		if (watchLibraryListeners == null) {
			watchLibraryListeners = new ArrayList<>();
		}

		watchLibraryListeners.add(watchLibraryListener);
	}

	private class WatchThread extends Thread {
		
		public WatchThread() {
			setName(getClass().getSimpleName());
		}
		
		private void registerWatchLibraries() {
			List<Library> libraries = libraryManager.getLocalLibraries(LibraryType.ALL);
			if (libraries == null || libraries.isEmpty()) {
				return;
			}

			for (Library library : libraries) {
				// Try to remove listener just in case it is already registered
				long libraryId = library.getId();
				removeWatchLibraryListener(libraryId);
				registerWatchLibraryListener(library);

			}

		}
		

		private void removeWatchLibraries() {
			List<Library> libraries = libraryManager.getLocalLibraries(LibraryType.ALL);
			if (libraries == null || libraries.isEmpty()) {
				return;
			}
			
			for (Library library : libraries) {
				long libraryId = library.getId();
				removeWatchLibraryListener(libraryId);				
			}
		}

		@Override
		public void run() {
			registerWatchLibraries();
		}

	}

}
