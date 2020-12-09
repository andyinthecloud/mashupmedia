package org.mashupmedia.task;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.LibraryUpdateManager;
import org.mashupmedia.service.LibraryWatchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryUpdateTaskManager {

	@Autowired
	private ThreadPoolTaskExecutor libraryUpdateThreadPoolTaskExecutor;

	@Autowired
	private LibraryUpdateManager libraryUpdateManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private LibraryWatchManager libraryWatchManager;



	public void updateLibrary(Library library) {
		LibraryUpdateTask libraryUpdateTask = new LibraryUpdateTask(library);
		libraryUpdateThreadPoolTaskExecutor.execute(libraryUpdateTask);				
//		libraryWatchManager.registerWatchLibraryListeners();
	}

	public void updateLibraries() {
		List<Library> libraries = libraryManager.getLibraries(LibraryType.ALL);
		for (Library library : libraries) {
			updateLibrary(library);
		}
	}

	private class LibraryUpdateTask implements Runnable{

		private Library library;

		public LibraryUpdateTask(Library library) {
			this.library = library;
		}
		
		


		@Override
		public void run() {

			if (this.library == null) {
				return;
			}
			libraryUpdateManager.updateLibrary(library);
			log.info("updated library");
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
