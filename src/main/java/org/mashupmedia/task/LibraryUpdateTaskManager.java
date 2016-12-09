package org.mashupmedia.task;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.service.LibraryUpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LibraryUpdateTaskManager {

	@Autowired
	private ThreadPoolTaskExecutor libraryUpdateThreadPoolTaskExecutor;

	@Autowired
	private LibraryUpdateManager libraryUpdateManager;

	public void updateLibrary(Library library) {
		libraryUpdateThreadPoolTaskExecutor.execute(new LibraryUpdateTask(library));
	}

	private class LibraryUpdateTask implements Runnable {

		private Library library;

		public LibraryUpdateTask(Library library) {
			this.library = library;
		}

		public void run() {
			libraryUpdateManager.updateLibrary(library);
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
			libraryUpdateManager.updateRemoteLibrary(library);
		}
	}

	private class DeleteLibraryTask implements Runnable {
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
