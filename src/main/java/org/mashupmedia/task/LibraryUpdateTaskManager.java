package org.mashupmedia.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.LibraryUpdateManager;
import org.mashupmedia.service.LibraryWatchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LibraryUpdateTaskManager {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ThreadPoolTaskExecutor libraryUpdateThreadPoolTaskExecutor;

	@Autowired
	private LibraryUpdateManager libraryUpdateManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private LibraryWatchManager libraryWatchManager;

	public void updateLibrary(Library library) {
		LibraryUpdateTask libraryUpdateTask = new LibraryUpdateTask();
		libraryUpdateTask.addLibrary(library);
		libraryUpdateThreadPoolTaskExecutor.execute(libraryUpdateTask);
		libraryWatchManager.registerWatchLibraryListeners();
	}

	public void updateLibraries() {
		LibraryUpdateTask libraryUpdateTask = new LibraryUpdateTask();
		List<Library> libraries = libraryManager.getLibraries(LibraryType.ALL);
		libraryUpdateTask.setLibraries(libraries);
		libraryUpdateThreadPoolTaskExecutor.execute(libraryUpdateTask);
		
	}

	private class LibraryUpdateTask extends Thread {

		private List<Library> libraries;

		private void setLibraries(List<Library> libraries) {
			this.libraries = libraries;
		}

		private void addLibrary(Library library) {
			if (this.libraries == null) {
				this.libraries = new ArrayList<>();
			}
			this.libraries.add(library);
		}

		@Override
		public void run() {
			if (this.libraries == null || this.libraries.isEmpty()) {
				return;
			}

			for (Library library : this.libraries) {
				libraryUpdateManager.updateLibrary(library);
			}

			logger.info("updated library(ies)");

		}

	}

	public void updateRemoteLibrary(Library remoteLibrary) {
		libraryUpdateThreadPoolTaskExecutor.execute(new RemoteLibraryUpdateTask(remoteLibrary));
	}

	private class RemoteLibraryUpdateTask extends Thread {

		private Library library;

		public RemoteLibraryUpdateTask(Library library) {
			this.library = library;
		}

		@Override
		public void run() {
			libraryUpdateManager.updateRemoteLibrary(library);
		}
	}

	private class DeleteLibraryTask extends Thread {
		private long libraryId;

		public DeleteLibraryTask(long libraryId) {
			this.libraryId = libraryId;
		}

		@Override
		public void run() {
			libraryUpdateManager.deleteLibrary(libraryId);
		}

	}

	public void deleteLibrary(long libraryId) {
		libraryUpdateThreadPoolTaskExecutor.execute(new DeleteLibraryTask(libraryId));

	}

}
