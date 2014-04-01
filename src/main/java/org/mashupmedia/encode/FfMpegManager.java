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

package org.mashupmedia.encode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FfMpegManager {

	private static final String FFMPEG_FOLDER_NAME = "ffmpeg";
	private static final String FFMPEG_EXECUTABLE_NAME = "ffmpeg";
	private static final String[] FFMPEG_EXECUTABLE_EXTENSIONS = new String[] { "exe", "sh" };
	private static final String FFMPEG_EXECUTABLE_LINK = "ffmpeg.txt";

	private Logger logger = Logger.getLogger(FfMpegManager.class);

	@Autowired
	private ProcessManager processManager;

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private MediaManager mediaManager;

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

	public boolean isValidFfMpeg(File ffMpegExecutableFile) throws IOException {
		if (ffMpegExecutableFile == null) {
			return false;
		}
		
		String outputText = processManager.callProcess(ffMpegExecutableFile.getAbsolutePath());
		if (outputText.contains(FFMPEG_EXECUTABLE_NAME)) {
			return true;
		}

		return false;
	}

	public void encodeMediaItem(MediaItem mediaItem, MediaContentType mediaContentType) throws IOException {

		String pathToFfMpeg = configurationManager.getConfigurationValue(MashUpMediaConstants.FFMPEG_PATH);
		if (StringUtils.isBlank(pathToFfMpeg)) {
			String errorText = "Unable to encode media, ffmpeg is not configured.";
			logger.info(errorText);
			return;
		}

		long mediaItemId = mediaItem.getId();

		File inputFile = new File(mediaItem.getPath());
		File outputFile = FileHelper.getEncodedMediaFile(mediaItem, mediaContentType);
		boolean isDeleted = FileHelper.deleteFile(outputFile);

		if (!isDeleted) {
			String errorText = "Exiting, unable to delete encoded media file: " + outputFile.getAbsolutePath();
			logger.info(errorText);
			return;
		}

		if (mediaContentType == MediaContentType.MP3) {
			encodeAudioToMp3(pathToFfMpeg, inputFile, outputFile, mediaItemId);
		} else if (mediaContentType == MediaContentType.MP4) {
			encodeVideoToMp4(pathToFfMpeg, inputFile, outputFile, mediaItemId);
		} else if (mediaContentType == MediaContentType.WEBM) {
			encodeVideoToWebM(pathToFfMpeg, inputFile, outputFile, mediaItemId);
		} else if (mediaContentType == MediaContentType.OGV) {
			encodeVideoToOGV(pathToFfMpeg, inputFile, outputFile, mediaItemId);
		} else {
			logger.info(mediaContentType.name() + " not supported");
		}

	}

	private void encodeAudioToMp3(String pathToFfMpeg, File inputFile, File outputFile, long mediaItemId)
			throws IOException {

		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-codec:a");
		commands.add("libmp3lame");
		commands.add("-b:a");
		commands.add("192k");
		commands.add("-f");
		commands.add("mp3");
		commands.add(outputFile.getAbsolutePath());

		processManager.callProcess(commands, mediaItemId, MediaContentType.MP3);
	}

	private void encodeVideoToMp4(String pathToFfMpeg, File inputFile, File outputFile, long mediaItemId)
			throws IOException {

		// ffmpeg -y -i test.avi -c:v libx264 -preset:v veryfast -strict
		// experimental -c:a aac -b:a 240k -f mp4 output.encoded

		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-sn");
		commands.add("-c:v");
		commands.add("libx264");
		commands.add("-preset:v");
		commands.add("veryfast");
		commands.add("-strict");
		commands.add("experimental");
		commands.add("-c:a");
		commands.add("aac");
		commands.add("-b:a");
		commands.add("240k");
		commands.add("-f");
		commands.add("mp4");
		commands.add(outputFile.getAbsolutePath());

		processManager.callProcess(commands, mediaItemId, MediaContentType.MP4);
	}

	private void encodeVideoToWebM(String pathToFfMpeg, File inputFile, File outputFile, long mediaItemId)
			throws IOException {

		// ffmpeg -i input.mp4 -c:v libvpx -b:v 1M -c:a libvorbis -qscale:a 5
		// output.webm

		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-sn");
		commands.add("-c:v");
		commands.add("libvpx");
		commands.add("-b:v");
		commands.add("1M");
		commands.add("-c:a");
		commands.add("libvorbis");
		commands.add("-qscale:a");
		commands.add("5");
		commands.add("-f");
		commands.add("webm");
		commands.add(outputFile.getAbsolutePath());

		processManager.callProcess(commands, mediaItemId, MediaContentType.WEBM);
	}

	private void encodeVideoToOGV(String pathToFfMpeg, File inputFile, File outputFile, long mediaItemId)
			throws IOException {

		// ffmpeg -y -i input.mp4 -sn -codec:v libtheora -qscale:v 7 -codec:a libvorbis -qscale:a 5 output.ogv
		
		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-sn");
		commands.add("-codec:v");
		commands.add("libtheora");
		commands.add("-qscale:v");
		commands.add("7");
		commands.add("-codec:a");
		commands.add("libvorbis");
		commands.add("-qscale:a");
		commands.add("5");
		commands.add(outputFile.getAbsolutePath());
		commands.add("-f");
		commands.add("ogv");
		
		processManager.callProcess(commands, mediaItemId, MediaContentType.OGV);
	}

}
