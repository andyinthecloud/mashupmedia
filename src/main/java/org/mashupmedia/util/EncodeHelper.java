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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class EncodeHelper {

	private static final String FFMPEG_FOLDER_NAME = "ffmpeg";
	private static final String FFMPEG_EXECUTABLE_NAME = "ffmpeg";
	private static final String[] FFMPEG_EXECUTABLE_EXTENSIONS = new String[] { "exe", "sh"};
	private static final String FFMPEG_EXECUTABLE_LINK = "ffmpeg.txt";
	
	private static Logger logger = Logger.getLogger(EncodeHelper.class);

	public static String getFFMpegFolderPath() {
		File ffMpegFolder = new File(FileHelper.getApplicationFolder(), FFMPEG_FOLDER_NAME);
		return ffMpegFolder.getAbsolutePath();
	}

	public static File findFFMpegExecutable() {
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
				File ffmpegFile = getFfmpegExceutable(file);
				if (ffmpegFile != null) {
					return ffmpegFile;
				}
			}
		}
		return null;
	}

	private static File getFfmpegExceutable(File file) {
		String fileName = file.getName().toLowerCase();
		if (!fileName.startsWith(FFMPEG_EXECUTABLE_NAME)) {
			return null;
		}

		if (fileName.equals(FFMPEG_EXECUTABLE_NAME)) {
			return file;
		}

		if (fileName.equalsIgnoreCase(FFMPEG_EXECUTABLE_LINK)) {
			String link = null;
			try {
				link = StringUtils.trimToEmpty(FileUtils.readFileToString(file));				
			} catch (IOException e) {
				logger.error("Unable to read link file", e);
				return null;
			}
			
			if (StringUtils.isEmpty(link)) {
				return null;
			}
			
			file = new File(link);
			if (file.exists()) {
				return file;
			}
		}
		
		String fileExtension = FileHelper.getFileExtension(fileName);

		for (String ffmpegExecutableFileExtension : FFMPEG_EXECUTABLE_EXTENSIONS) {
			if (fileExtension.equalsIgnoreCase(ffmpegExecutableFileExtension)) {
				return file;
			}
		}
		

		

		return null;
	}
	
	public static boolean isValidFfMpeg(File ffMpegExecutableFile) throws IOException {
		String outputText = ProcessHelper.callProcess(ffMpegExecutableFile.getAbsolutePath());
		if (outputText.contains(FFMPEG_EXECUTABLE_NAME)) {
			return true;
		}
		return false;
	}

	public static void encodeAudioToHtml5(String pathToFfMpeg, File inputAudioFile, File outputAudioFile) throws IOException {
		
		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-i");
		commands.add(inputAudioFile.getAbsolutePath());
		commands.add("-y");
		commands.add("-f");
		commands.add("ogg");
		commands.add("-acodec");
		commands.add("libvorbis");
		commands.add("-ab");
		commands.add("128k");
		commands.add(outputAudioFile.getAbsolutePath());
		
		String outputText = ProcessHelper.callProcess(commands);
		logger.info(outputText);
	}
	
	
//	public static void encodeAudioToHtml5(String pathToFfMpeg, File inputAudioFile, File outputAudioFile) throws IOException {
//		
//		List<String> commands = new ArrayList<String>();
//		commands.add(pathToFfMpeg);
//		commands.add("-i");
//		commands.add(inputAudioFile.getAbsolutePath());
//		commands.add("-b:a");
//		commands.add("160k");
//		commands.add(outputAudioFile.getAbsolutePath());
//		
//		String outputText = ProcessHelper.callProcess(commands);
//		logger.info(outputText);
//	}
	

}
