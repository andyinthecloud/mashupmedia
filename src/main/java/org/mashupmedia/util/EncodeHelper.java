/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.util;

import java.io.File;
import java.io.IOException;

import org.mashupmedia.constants.MashUpMediaConstants;

public class EncodeHelper {

	private static final String FFMPEG_FOLDER_NAME = "ffmpeg";
	private static final String FFMPEG_EXECUTABLE_NAME = "ffmpeg";
	private static final String[] FFMPEG_EXECUTABLE_EXTENSIONS = new String[]{"exe", "sh"};

	public static String getFFMpegFolderPath() {
		File ffMpegFolder = new File(MessageHelper.getMessage(MashUpMediaConstants.APPLICATION_FOLDER), FFMPEG_FOLDER_NAME);
		return ffMpegFolder.getAbsolutePath();
	}

	public static File findFFMpegExecutable()  {
		File ffMpegFolder = new File(getFFMpegFolderPath());
		ffMpegFolder.mkdirs();		
		File ffMpegExecutableFile = findFFMpegExecutable(ffMpegFolder);
		if (ffMpegExecutableFile == null) {
			return ffMpegExecutableFile;
		}
		
		return ffMpegExecutableFile;
	}

	private static File findFFMpegExecutable(File folder) {
		File[] files = folder.listFiles();		
		for (File file : files) {
						
			if (file.isDirectory()) {
				File ffMpegExecutable = findFFMpegExecutable(file);
				if (ffMpegExecutable != null) {
					return ffMpegExecutable;
				}
			} else {
				if (isFfmpegExceutable(file)) {
					return file;
				}
			}
		}
		return null;
	}

	private static boolean isFfmpegExceutable(File file) {
		String fileName = file.getName().toLowerCase();
		if (!fileName.startsWith(FFMPEG_EXECUTABLE_NAME)) {
			return false;
		}		

		if (fileName.equals(FFMPEG_EXECUTABLE_NAME)) {			
			return true;
		}
		
		String fileExtension = FileHelper.getFileExtension(fileName);
		
		for (String ffmpegExecutableFileExtension : FFMPEG_EXECUTABLE_EXTENSIONS) {			
			if (fileExtension.equals(ffmpegExecutableFileExtension)) {
				return true;
			}			
		}
		
		return false;
	}

	public static boolean isValidFfMpeg(File ffMpegExecutableFile) throws IOException {
		ProcessHelper.callProcess(ffMpegExecutableFile.getAbsolutePath());
		return true;
	}

}
