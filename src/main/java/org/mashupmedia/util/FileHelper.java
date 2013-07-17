package org.mashupmedia.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.MashupMediaRuntimeException;

public class FileHelper {

	private static Logger logger = Logger.getLogger(FileHelper.class);
	private static String MASHUP_MEDIA_HOME = "MASHUP_MEDIA_HOME";

	public final static String ALBUM_ART_FOLDER = "cover-art";
	private static File applicationHomeFolder = null;

	public enum FileType {
		ALBUM_ART("album-art"), ALBUM_ART_THUMBNAIL("album-art-thumbnail"), MEDIA_ITEM_STREAM_UNPROCESSED("media-item-stream"), MEDIA_ITEM_STREAM_ENCODED(
				"media-item-encoded");

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
		if (fileType == FileType.MEDIA_ITEM_STREAM_ENCODED) {
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
		} else if (fileName.endsWith(".mp4")) {
			return true;
		} else if (fileName.endsWith(".flac")) {
			return true;
		} else if (fileName.endsWith(".wma")) {
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
			extension = fileName.replaceAll(".*\\.", "").toLowerCase();
		}

		return extension;

	}
	
	public static File getApplicationFolder() {
		if (applicationHomeFolder != null) {
			return applicationHomeFolder;
		}
		
		String applicationHomePath = System.getenv(MASHUP_MEDIA_HOME);
		if(StringUtils.isBlank(applicationHomePath)) {
			applicationHomePath = System.getProperty(MASHUP_MEDIA_HOME);
		}
		
		if (StringUtils.isNotBlank(applicationHomePath)) {
			applicationHomeFolder = new File(applicationHomePath);
		} else {
			applicationHomePath = System.getProperty("user.home");
			applicationHomeFolder = new File(applicationHomePath, "mashup_media");
			applicationHomeFolder.mkdirs();
			if (!applicationHomeFolder.isDirectory()) {
				logger.error("Unable to create Mashup Media folder in the user home: " + applicationHomeFolder.getAbsolutePath());
				logger.error("Will default to temp directory but please set the system property MASHUP_MEDIA_HOME variable as files inside this folder are deleted regualary by the system.");
				applicationHomePath = System.getProperty("java.io.tmpdir");
				applicationHomeFolder = new File(applicationHomePath, "mashup_media");
			}
			
		}
		applicationHomeFolder.mkdirs();
		if (!applicationHomeFolder.isDirectory()) {
			throw new MashupMediaRuntimeException("Cannot proceed, unable to create the folder MASHUP_MEDIA_HOME: " + applicationHomeFolder.getAbsolutePath());
		}
		
		return applicationHomeFolder;
	}

	private static File getLibraryFolder(long libraryId) {
		File libraryFolder = new File(getApplicationFolder(), "libraries/" + libraryId);
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

	public static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			logger.info("Unable to delete file. It is in fact a folder: " + file.getAbsolutePath());
			return true;
		}

		if (!file.exists()) {
			logger.info("Unable to delete file. It is does not exist: " + file.getAbsolutePath());
			return true;
		}

		try {
			if (file.delete()) {
				return true;
			}
		} catch (Exception e) {
			logger.info("Unable to delete file: ", e);
		}

		logger.info("Unable to delete file, will try to remove when web server stops...");
		file.deleteOnExit();
		return false;
	}

	public static File getLibraryXmlFile(long libraryId) {
		File file = new File(getLibraryFolder(libraryId), String.valueOf(libraryId));
		return file;
	}

}
