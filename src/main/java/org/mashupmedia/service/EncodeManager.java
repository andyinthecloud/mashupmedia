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

package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.util.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncodeManager {

	private static final String FFMPEG_FOLDER_NAME = "ffmpeg";
	private static final String FFMPEG_EXECUTABLE_NAME = "ffmpeg";
	private static final String[] FFMPEG_EXECUTABLE_EXTENSIONS = new String[] { "exe", "sh"};
	private static final String FFMPEG_EXECUTABLE_LINK = "ffmpeg.txt";
	
	private Logger logger = Logger.getLogger(EncodeManager.class);
	
	@Autowired
	private ProcessManager processManager;

	public String getFFMpegFolderPath() {
		File ffMpegFolder = new File(FileHelper.getApplicationFolder(), FFMPEG_FOLDER_NAME);
		return ffMpegFolder.getAbsolutePath();
	}

	public File findFFMpegExecutable() {
		File ffMpegFolder = new File(getFFMpegFolderPath());
		ffMpegFolder.mkdirs();
		File ffMpegExecutableFile = findFFMpegExecutable(ffMpegFolder);
		if (ffMpegExecutableFile == null) {
			return ffMpegExecutableFile;
		}

		return ffMpegExecutableFile;
	}

	private File findFFMpegExecutable(File folder) {
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

	private File getFfmpegExceutable(File file) {
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
	
	public  boolean isValidFfMpeg(File ffMpegExecutableFile) throws IOException {
		boolean isSuccessful = processManager.callProcess(ffMpegExecutableFile.getAbsolutePath());
//		if (outputText.contains(FFMPEG_EXECUTABLE_NAME)) {
//			return true;
//		}
		return isSuccessful;
	}

	public  boolean encodeAudioToHtml5(String pathToFfMpeg, File inputFile, File outputFile) throws IOException {
		
		// ffmpeg -i input.wav -codec:a libmp3lame -b:a 192k output.mp3
		
		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-acodec");
		commands.add("libmp3lame");
		commands.add("-b:a");
		commands.add("192k");
		commands.add("-f");
		commands.add("mp3");		
		commands.add(outputFile.getAbsolutePath());
		
		boolean isSuccessful = processManager.callProcess(commands);
//		logger.info(outputText);
	
//		boolean hasError = hasError(outputText);
		return isSuccessful;		

	}

	public boolean encodeVideoToHtml5(String pathToFfMpeg, File inputFile, File outputFile) throws IOException {
		
// ffmpeg -i video.mp4 -y -vcodec libx264 -r 25 -b:v 1024k -ab 128k -ac 2 -async 1 -f mp4 video.encoded
		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-y");
		commands.add("-vcodec");
		commands.add("libx264");
		commands.add("-r");
		commands.add("25");
		commands.add("-b:v");
		commands.add("1024k");
		commands.add("-ab");
		commands.add("128k");
		commands.add("-ac");
		commands.add("2");
		commands.add("-async");
		commands.add("1");
		commands.add("-f");
		commands.add("mp4");		
		commands.add(outputFile.getAbsolutePath());
		
		boolean isSuccessful = processManager.callProcess(commands);
//		logger.info(outputText);		
		
//		boolean hasError = hasError(outputText);
		return isSuccessful;		
	}
	
//	protected  boolean hasError(String text) {
//		text = StringUtils.trimToEmpty(text);
//		if (StringUtils.isEmpty(text)) {
//			return false;
//		}
//		
//		if (text.matches("^Error")) {
//			return true;
//		}
//		
//		return false;
//		
//	}
	
	
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
