package org.mashupmedia.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;

public class FileHelper {

	private static Logger logger = Logger.getLogger(FileHelper.class);

	public final static String ALBUM_ART_FOLDER = "cover-art";
	public final static int FILE_TIME_OUT_SECONDS = 300;
	public final static int FILE_WAIT_FOR_SECONDS = 1;

	public enum FileType {
		ALBUM_ART("album-art"), ALBUM_ART_THUMBNAIL("album-art-thumbnail"), MEDIA_ITEM_STREAM("media-item-stream"), MEDIA_ITEM_ENCODED("media-item-encoded") ;

		private String folderName;

		private FileType(String folderName) {
			this.folderName = folderName;
		}

		public String getFolderName() {
			return folderName;
		}

	}

	public static File createMediaFile(long libraryId, long mediaItemId, FileType fileType) {
		File libraryFolder = getLibraryFolder(libraryId);
		File mediaFolder = new File(libraryFolder, fileType.getFolderName());
		mediaFolder.mkdirs();
		
		String fileName = String.valueOf(mediaItemId);
		if (fileType == FileType.MEDIA_ITEM_ENCODED) {
			fileName += ".ogg";
		}
		
		File mediaFile = new File(mediaFolder, fileName);
		return mediaFile;
	}

	public static File createAlbumArtFile(long libraryId) {
		File libraryFolder = getLibraryFolder(libraryId);
		File mediaFolder = new File(libraryFolder, FileType.ALBUM_ART.getFolderName());
		mediaFolder.mkdirs();
		File mediaFile = new File(mediaFolder, String.valueOf(System.nanoTime()));
		return mediaFile;
	}
	
	public static File createAlbumArtThumbnailFile(long libraryId) {
		File libraryFolder = getLibraryFolder(libraryId);
		File thumbnailFolder = new File(libraryFolder, FileType.ALBUM_ART_THUMBNAIL.getFolderName());
		thumbnailFolder.mkdirs();
		File thumbnailFile = new File(thumbnailFolder, String.valueOf(System.nanoTime()));
		return thumbnailFile;
	}

	public static boolean isSupportedSong(String fileName) {
		fileName = StringUtils.trimToEmpty(fileName).toLowerCase();
		if (StringUtils.isEmpty(fileName)) {
			return false;
		}

		if (fileName.endsWith(".mp3")) {
			return true;
		} else if (fileName.endsWith(".ogg")) {
			return true;
		} else if (fileName.endsWith(".m4a")) {
			return true;
		} else if (fileName.endsWith(".aac")) {
			return true;
		}

		return false;

	}

	public static boolean isSupportedImage(String fileName) {
		fileName = StringUtils.trimToEmpty(fileName).toLowerCase();
		if (StringUtils.isEmpty(fileName)) {
			return false;
		}

		if (fileName.endsWith(".tif")) {
			return true;
		} else if (fileName.endsWith(".jpg")) {
			return true;
		} else if (fileName.endsWith(".jpeg")) {
			return true;
		} else if (fileName.endsWith(".png")) {
			return true;
		} else if (fileName.endsWith(".gif")) {
			return true;
		}

		return false;

	}

	public static String getFileExtension(String fileName) {
		fileName = StringUtils.trimToEmpty(fileName);
		String extension = "jpg";
		if (StringUtils.isNotEmpty(fileName)) {
			extension = extension.replaceAll(".*\\.", "").toLowerCase();
		}

		return extension;

	}

	private static File getLibraryFolder(long libraryId) {
		File libraryFolder = new File(MessageHelper.getMessage(MashUpMediaConstants.APPLICATION_FOLDER), "libraries/" + libraryId);
		libraryFolder.mkdirs();
		return libraryFolder;
	}

	public static void deleteLibrary(long libraryId) {
		File libraryFolder = getLibraryFolder(libraryId);
		try {
			FileUtils.deleteDirectory(libraryFolder);
		} catch (IOException e) {
			logger.error("Unable to delete library", e);
		}

	}

	public static boolean isMatchingFileNamePattern(String fileName, String fileNamePattern) {
		fileName = StringUtils.trimToEmpty(fileName).toLowerCase();
		if (StringUtils.isEmpty(fileName)) {
			return false;
		}

		fileNamePattern = StringUtils.trimToEmpty(fileNamePattern);
		if (StringUtils.isEmpty(fileNamePattern)) {
			fileNamePattern = MashUpMediaConstants.COVER_ART_DEFAULT_NAME;
		}

		String[] patterns = fileNamePattern.split(",|;");
		for (String pattern : patterns) {
			pattern = StringUtils.trimToEmpty(pattern).toLowerCase();
			if (StringUtils.isEmpty(pattern)) {
				continue;
			}
			pattern = pattern.replaceAll("\\*", ".*.");
			if (fileName.matches(pattern)) {
				return true;
			}
		}
		return false;
	}

	public static String getDisplayBytes(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static void waitForFile(File file, long sizeInBytes) throws InterruptedException {
		// Allow for a 5% margin of error
		Double sizeInBytesWithErrorMargin = sizeInBytes * 0.95;
		int timeOutSeconds = 0;
		while (timeOutSeconds < FILE_TIME_OUT_SECONDS) {
			if (sizeInBytesWithErrorMargin < file.length()) {
				logger.info("File is ready.");
				return;
			}

			Thread.sleep(FILE_WAIT_FOR_SECONDS * 1000);
			timeOutSeconds = timeOutSeconds + FILE_WAIT_FOR_SECONDS;
		}

		logger.info("File timed out.");

	}

}
