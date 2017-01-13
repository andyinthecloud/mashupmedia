package org.mashupmedia.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mashupmedia.service.LibraryManager;

public class WatchLibraryListener {

	private Logger logger = Logger.getLogger(getClass());
	private LibraryManager libraryManager;
	private WatchService watcher;
	private Map<WatchKey, Path> keys;
	private long librayId;

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path folder) throws IOException {
		WatchKey key = folder.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

		Path prev = keys.get(key);
		if (prev == null) {
			logger.info("register: " + folder);
		} else {
			if (!folder.equals(prev)) {
				logger.info("update: " + prev + " -> " + folder);
			}
		}
		keys.put(key, folder);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public WatchLibraryListener(long libraryId, Path dir, LibraryManager libraryManager) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.librayId = libraryId;
		this.libraryManager = libraryManager;

		logger.debug("Scanning %s ...\n" + dir);
		registerAll(dir);
		logger.debug("Done.");

	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	public void processEvents() {
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				logger.error("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;

				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				logger.info(event.kind().name() + ": " + child);

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
							registerAll(child);
						}

					} catch (IOException x) {
						// ignore to keep sample readbale
					}
				}
				
				processFileEvent(child.toFile(), kind);
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void processFileEvent(File file, Kind kind) {
		if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
			libraryManager.saveMedia(librayId, file);
		} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
			libraryManager.deleteMedia(librayId, file);
		}
	}

	public long getLibrayId() {
		return librayId;
	}

	public void cancel() {
		if (keys == null || keys.isEmpty()) {
			return;
		}

		Collection<WatchKey> watchKeys = keys.keySet();
		for (Iterator<WatchKey> iterator = watchKeys.iterator(); iterator.hasNext();) {
			WatchKey watchKey = (WatchKey) iterator.next();
			watchKey.cancel();
			watchKeys.remove(watchKey);
		}

	}

	// static void usage() {
	// System.err.println("usage: java WatchDir [-r] dir");
	// System.exit(-1);
	// }
	//
	// public static void main(String[] args) throws IOException {
	// // parse arguments
	// if (args.length == 0 || args.length > 2)
	// usage();
	// boolean recursive = false;
	// int dirArg = 0;
	// if (args[0].equals("-r")) {
	// if (args.length < 2)
	// usage();
	// recursive = true;
	// dirArg++;
	// }
	//
	// // register directory and process its events
	// Path dir = Paths.get(args[dirArg]);
	//// new WatchLibrary(1, dir, recursive).processEvents();
	// }
}
