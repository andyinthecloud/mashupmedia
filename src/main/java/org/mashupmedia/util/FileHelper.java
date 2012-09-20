package org.mashupmedia.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.MashupMediaException;

public class FileHelper {
	private static Logger logger = Logger.getLogger(FileHelper.class);

	public final static String ALBUM_ART_FOLDER = "cover-art";

	public enum FileType {
		ALBUM_ART("album-art"), MEDIA_ITEM_STREAM("media-item-streams");

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
		File mediaFile = new File(mediaFolder, String.valueOf(mediaItemId));
		return mediaFile;
	}

	public static File createAlbumArtFile(long libraryId) {
		File libraryFolder = getLibraryFolder(libraryId);
		File mediaFolder = new File(libraryFolder, FileType.ALBUM_ART.getFolderName());
		mediaFolder.mkdirs();
		File mediaFile = new File(mediaFolder, Long.toString(System.nanoTime()));
		return mediaFile;
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

	public static String writeAlbumArt(long libraryId, String artistName, String albumName, String mimeType, byte[] bytes) throws IOException {
		mimeType = StringUtils.trimToEmpty(mimeType);
		String extension = "jpg";
		if (StringUtils.isNotEmpty(mimeType)) {
			extension = StringHelper.find(mimeType, "/.*").toLowerCase();
			extension = extension.replaceFirst("/", "");
		}

		File libraryAlbumArtFolder = new File(getLibraryFolder(libraryId), ALBUM_ART_FOLDER);
		libraryAlbumArtFolder.mkdirs();
		String albumArtFileName = StringUtils.trimToEmpty(artistName + "-" + albumName + "." + extension).toLowerCase();
		albumArtFileName = albumArtFileName.replaceAll("\\s", "");

		File albumArtFile = new File(libraryAlbumArtFolder, albumArtFileName);
		if (albumArtFile.exists()) {
			logger.info("Album art file already exists for libraryId:" + libraryId + ", artistName:" + artistName + ", albumName: " + albumName
					+ ". Exiting...");
			return albumArtFile.getAbsolutePath();

		}

		FileUtils.writeByteArrayToFile(albumArtFile, bytes);
		return albumArtFile.getAbsolutePath();
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
			throw new MashupMediaException("Unable to delete library folder", e);
		}

	}

	public static boolean isMatchingFileNamePattern(String fileName, String fileNamePattern) {
		fileName = StringUtils.trimToEmpty(fileName).toLowerCase();
		if (StringUtils.isEmpty(fileName)) {
			return false;
		}

		fileNamePattern = StringUtils.trimToEmpty(fileNamePattern);
		if (StringUtils.isEmpty(fileNamePattern)) {
			fileNamePattern = MashUpMediaConstants.COVER_ART_DEFAULT_NAME + "*";
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

}
