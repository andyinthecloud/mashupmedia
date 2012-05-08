package org.mashupmedia.util;

import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import org.apache.commons.lang3.StringUtils;

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

	/**
	 * Returns true if the ftpFile is a folder and contains songs
	 * 
	 * @param ftpFile
	 * @return
	 * @throws FTPException
	 * @throws FTPIllegalReplyException
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws FTPListParseException
	 * @throws FTPAbortedException
	 * @throws FTPDataTransferException
	 */
	public static boolean isAlbum(FTPFile ftpFile, FTPClient ftpClient) throws IllegalStateException, IOException, FTPIllegalReplyException,
			FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {
		if (ftpFile.getType() != FTPFile.TYPE_DIRECTORY) {
			return false;
		}

		String name = ftpFile.getName();
		String path = ftpClient.currentDirectory();
		String albumPath = path + "/" + name;

		try {
			ftpClient.changeDirectory(albumPath);
			String[] fileNames = ftpClient.listNames();
			for (String fileName : fileNames) {
				if (isSupportedSong(fileName)) {
					return true;
				}
			}
			return false;
		} finally {
			ftpClient.changeDirectory(path);
		}
	}

	public static boolean hasFolders(FTPFile ftpFile, FTPClient ftpClient) throws IllegalStateException, IOException, FTPIllegalReplyException,
			FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {
		if (ftpFile.getType() != FTPFile.TYPE_DIRECTORY) {
			return false;
		}

		String name = ftpFile.getName();
		String path = ftpClient.currentDirectory();
		String albumPath = path + "/" + name;

		try {
			ftpClient.changeDirectory(albumPath);
			FTPFile[] childFtpFiles = ftpClient.list();
			for (FTPFile childFtpFile : childFtpFiles) {
				if (childFtpFile.getType() == FTPFile.TYPE_DIRECTORY) {
					return true;
				}
			}
			return false;
		} finally {
			ftpClient.changeDirectory(path);
		}
	}

}
