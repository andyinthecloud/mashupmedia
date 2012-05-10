package org.mashupmedia.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.MashupMediaException;

public class FileHelper {

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

//	/**
//	 * Returns true if the ftpFile is a folder and contains songs
//	 * 
//	 * @param ftpFile
//	 * @return
//	 * @throws FTPException
//	 * @throws FTPIllegalReplyException
//	 * @throws IOException
//	 * @throws IllegalStateException
//	 * @throws FTPListParseException
//	 * @throws FTPAbortedException
//	 * @throws FTPDataTransferException
//	 */
//	public static boolean isAlbum(FTPFile ftpFile, FTPClient ftpClient) throws IllegalStateException, IOException, FTPIllegalReplyException,
//			FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {
//		if (ftpFile.getType() != FTPFile.TYPE_DIRECTORY) {
//			return false;
//		}
//
//		String name = ftpFile.getName();
//		String path = ftpClient.currentDirectory();
//		String albumPath = path + "/" + name;
//
//		try {
//			ftpClient.changeDirectory(albumPath);
//			String[] fileNames = ftpClient.listNames();
//			for (String fileName : fileNames) {
//				if (isSupportedSong(fileName)) {
//					return true;
//				}
//			}
//			return false;
//		} finally {
//			ftpClient.changeDirectory(path);
//		}
//	}
//
//	public static boolean hasFolders(FTPFile ftpFile, FTPClient ftpClient) throws IllegalStateException, IOException, FTPIllegalReplyException,
//			FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {
//		if (ftpFile.getType() != FTPFile.TYPE_DIRECTORY) {
//			return false;
//		}
//
//		String name = ftpFile.getName();
//		String path = ftpClient.currentDirectory();
//		String albumPath = path + "/" + name;
//
//		try {
//			ftpClient.changeDirectory(albumPath);
//			FTPFile[] childFtpFiles = ftpClient.list();
//			for (FTPFile childFtpFile : childFtpFiles) {
//				if (childFtpFile.getType() == FTPFile.TYPE_DIRECTORY) {
//					return true;
//				}
//			}
//			return false;
//		} finally {
//			ftpClient.changeDirectory(path);
//		}
//	}

	public static String writeAlbumArt(long libraryId, String mimeType, byte[] bytes) throws FileNotFoundException, IOException {
		mimeType = StringUtils.trimToEmpty(mimeType);
		String extension = "jpg";
		if (StringUtils.isNotEmpty(mimeType)) {
			extension = StringHelper.find(mimeType, "/.*").toLowerCase();
			extension.replaceFirst("/", "");
		}
		
		File albumArtFile = new File(getLibraryFolder(libraryId), MashUpMediaConstants.COVER_ART_DEFAULT_NAME + "." + extension);
		albumArtFile.mkdirs();		
		IOUtils.write(bytes, new FileOutputStream(albumArtFile));
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

}
