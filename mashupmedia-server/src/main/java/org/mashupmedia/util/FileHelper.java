package org.mashupmedia.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;

@Slf4j
public class FileHelper {

	private static String MASHUP_MEDIA_HOME = "MASHUP_MEDIA_HOME";

	public final static String ALBUM_ART_FOLDER = "cover-art";
	private static File applicationHomeFolder = null;

	public enum FileType {
		ALBUM_ART("album-art"), ALBUM_ART_THUMBNAIL("album-art-thumbnail"), MEDIA_ITEM_STREAM_UNPROCESSED(
				"media-item-stream"), MEDIA_ITEM_STREAM_ENCODED("media-item-encoded"), PHOTO_THUMBNAIL(
						"photo-thumbnail"), PHOTO_WEB_OPTIMISED("photo-web-optimised");

		private String folderName;

		private FileType(String folderName) {
			this.folderName = folderName;
		}

		public String getFolderName() {
			return folderName;
		}

	}

	public static File[] getEncodedFiles(long libraryId, long mediaItemId, FileType fileType) {
		File libraryFolder = getLibraryFolder(libraryId);
		File mediaFolder = new File(libraryFolder, fileType.getFolderName());
		mediaFolder.mkdirs();

		final String filePrefix = mediaItemId + ".";

		File[] files = mediaFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(filePrefix)) {
					return true;
				}
				return false;
			}
		});

		return files;
	}

	public static File getMediaFile(MediaItem mediaItem, MediaEncoding mediaEncoding) {
		if (mediaEncoding.isOriginal()) {
			File mediaFile = new File(mediaItem.getPath());
			return mediaFile;
		}

		MediaContentType mediaContentType = mediaEncoding.getMediaContentType();
		File encodedFile = getEncodedMediaFile(mediaItem, mediaContentType);
		return encodedFile;
	}

	public static File getEncodedMediaFile(MediaItem mediaItem, MediaContentType mediaContentType) {

		Library library = mediaItem.getLibrary();
		File libraryFolder = getLibraryFolder(library.getId());
		File mediaFolder = new File(libraryFolder, FileType.MEDIA_ITEM_STREAM_ENCODED.getFolderName());
		mediaFolder.mkdirs();

		String fileName = String.valueOf(mediaItem.getId());
		fileName += "." + mediaContentType.getName().toLowerCase();

		File file = new File(mediaFolder, fileName);
		return file;
	}

	public static File createMediaItemFile(long libraryId, FileType fileType) {
		File libraryFolder = getLibraryFolder(libraryId);
		File thumbnailFolder = new File(libraryFolder, fileType.getFolderName());
		thumbnailFolder.mkdirs();
		File thumbnailFile = new File(thumbnailFolder, String.valueOf(System.nanoTime()));
		return thumbnailFile;
	}

	public static boolean isSupportedTrack(String fileName) {
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
		} else if (fileName.endsWith(".tiff")) {
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

	public static boolean isSupportedVideo(String fileName) {
		fileName = StringUtils.trimToEmpty(fileName).toLowerCase();
		if (StringUtils.isEmpty(fileName)) {
			return false;
		}

		if (fileName.endsWith(".mp4")) {
			return true;
		} else if (fileName.endsWith(".avi")) {
			return true;
		} else if (fileName.endsWith(".mkv")) {
			return true;
		} else if (fileName.endsWith(".ogg")) {
			return true;
		} else if (fileName.endsWith(".ogv")) {
			return true;
		} else if (fileName.endsWith(".xvid")) {
			return true;
		} else if (fileName.endsWith(".divx")) {
			return true;
		} else if (fileName.endsWith(".webm")) {
			return true;
		} else if (fileName.endsWith(".mpg")) {
			return true;
		} else if (fileName.endsWith(".mpeg")) {
			return true;
		} else if (fileName.endsWith(".mov")) {
			return true;
		} else if (fileName.endsWith(".3gp")) {
			return true;
		} else if (fileName.endsWith(".wmv")) {
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

		String applicationHomePath = System.getProperty(MASHUP_MEDIA_HOME);
		if (StringUtils.isBlank(applicationHomePath)) {
			applicationHomePath = System.getenv(MASHUP_MEDIA_HOME);
		}

		if (StringUtils.isNotBlank(applicationHomePath)) {
			applicationHomeFolder = new File(applicationHomePath);
		} else {
			applicationHomePath = System.getProperty("user.home");
			applicationHomeFolder = new File(applicationHomePath, ".mashup_media");
			applicationHomeFolder.mkdirs();
			if (!applicationHomeFolder.isDirectory()) {
				log.error("Unable to create Mashup Media folder in the user home: "
						+ applicationHomeFolder.getAbsolutePath());
				log.error(
						"Will default to temp directory but please set the system property MASHUP_MEDIA_HOME variable as files inside this folder are deleted regualary by the system.");
				applicationHomePath = System.getProperty("java.io.tmpdir");
				applicationHomeFolder = new File(applicationHomePath, ".mashup_media");
			}

		}

		applicationHomeFolder.mkdirs();

		if (!applicationHomeFolder.isDirectory()) {
			log.error("Error creating application folder: " + applicationHomeFolder.getAbsolutePath());
			throw new MashupMediaRuntimeException("Cannot proceed, unable to create the folder MASHUP_MEDIA_HOME: "
					+ applicationHomeFolder.getAbsolutePath());
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
			log.error("Unable to delete library folder", e);
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

	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		return deleteFile(file);
	}

	public static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			log.info("Unable to delete file. It is in fact a folder: " + file.getAbsolutePath());
			return false;
		}

		if (!file.exists()) {
			log.info("Unable to delete file. It is does not exist: " + file.getAbsolutePath());
			return false;
		}

		try {
			if (file.delete()) {
				return true;
			}
		} catch (Exception e) {
			log.info("Unable to delete file: ", e);
		}

		log.info("Unable to delete file, will try to remove when web server stops...");
		file.deleteOnExit();
		return false;
	}

	public static File getLibraryXmlFile(long libraryId) {
		File file = new File(getLibraryFolder(libraryId), String.valueOf(libraryId));
		return file;
	}

	private static File getVideoFolder(long libraryId, long videoId) {
		File libraryFolder = FileHelper.getLibraryFolder(libraryId);
		File videoFolder = new File(libraryFolder, String.valueOf(videoId));
		return videoFolder;
	}

	public static void deleteProcessedVideo(long libraryId, long videoId) {

		if (libraryId == 0 || videoId == 0) {
			log.info(
					"Unable to delete video files from libray, libraryId = " + libraryId + ", videoId = " + videoId);
			return;
		}

		File videoFolder = getVideoFolder(libraryId, videoId);

		if (!videoFolder.isDirectory()) {
			log.debug("Unable to delete video folder: " + videoFolder.getAbsolutePath() + ". Does not exist.");
			return;
		}

		try {
			FileUtils.deleteDirectory(videoFolder);
		} catch (IOException e) {
			log.error("Error deleting folder: " + videoFolder.getAbsolutePath(), e);
		}
	}

	public static boolean isEmptyBytes(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return true;
		}
		return false;
	}

	public static Path getPath(MediaItem mediaItem) {
		if (mediaItem == null) {
			return null;
		}

		File file = new File(mediaItem.getPath());
		Path path = file.toPath();
		return path;
	}

	private static File getPlaylistFolder(long playlistId) {		
		File playlistFolder = new File(getApplicationFolder(), "playlists/" + playlistId);
		playlistFolder.mkdirs();
		return playlistFolder;
	}
	
	
	public static File createTemporaryPlaylistFile(long playlistId) {
		User user = AdminHelper.getLoggedInUser();
		String userPrefix = "";
		if (user != null) {
			userPrefix = user.getUsername();
		}
		
		File file = new File(getPlaylistFolder(playlistId), userPrefix + "-" + System.currentTimeMillis());
		return file;
	}
}
