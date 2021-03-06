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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.exception.MediaItemEncodeException.EncodeExceptionType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FfMpegManager {

	private static final String FFMPEG_FOLDER_NAME = "ffmpeg";
	private static final String FFMPEG_EXECUTABLE_NAME = "ffmpeg";
	private static final String[] FFMPEG_EXECUTABLE_EXTENSIONS = new String[] { "exe", "sh" };
	private static final String FFMPEG_EXECUTABLE_LINK = "ffmpeg.txt";

	@Autowired
	private ProcessManager processManager;

	@Autowired
	private ConfigurationManager configurationManager;

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
				link = StringUtils.trimToEmpty(FileUtils.readFileToString(file, StandardCharsets.UTF_8.name()));
			} catch (IOException e) {
				log.error("Unable to read link file", e);
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

	public void queueMediaItemBeforeEncoding(MediaItem mediaItem, MediaContentType mediaContentType)
			throws MediaItemEncodeException {

		String pathToFfMpeg = configurationManager.getConfigurationValue(MashUpMediaConstants.FFMPEG_PATH);
		if (StringUtils.isBlank(pathToFfMpeg)) {
			String errorText = "Unable to encode media, ffmpeg is not configured.";
			throw new MediaItemEncodeException(EncodeExceptionType.ENCODER_NOT_CONFIGURED, errorText);
		}

		long mediaItemId = mediaItem.getId();

		File inputFile = new File(mediaItem.getPath());
		File outputFile = FileHelper.getEncodedMediaFile(mediaItem, mediaContentType);
		boolean isDeleted = FileHelper.deleteFile(outputFile);

		if (!isDeleted) {
			String errorText = "Unable to delete encoded media file whil try when webserver stops: " + outputFile.getAbsolutePath();
			log.error(errorText);
		}

		List<String> commands = new ArrayList<String>();

		if (mediaContentType == MediaContentType.MP3) {
			commands = queueEncodeAudioToMp3(pathToFfMpeg, inputFile, outputFile);
		} else if (mediaContentType == MediaContentType.OGA) {
			commands = queueEncodeAudioToOga(pathToFfMpeg, inputFile, outputFile);
		} else if (mediaContentType == MediaContentType.MP4) {
			commands = queueEncodeVideoToMp4(pathToFfMpeg, inputFile, outputFile);
		} else if (mediaContentType == MediaContentType.WEBM) {
			commands = queueEncodeVideoToWebM(pathToFfMpeg, inputFile, outputFile);
		} else if (mediaContentType == MediaContentType.OGV) {
			commands = queueEncodeVideoToOgv(pathToFfMpeg, inputFile, outputFile);
		} else {
			throw new MediaItemEncodeException(EncodeExceptionType.UNSUPPORTED_ENCODING_FORMAT,
					mediaContentType.name() + " not supported");
		}

		processManager.addProcessToQueue(commands, mediaItemId, mediaContentType);

	}

	private List<String> queueEncodeAudioToMp3(String pathToFfMpeg, File inputFile, File outputFile) {

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

		return commands;
	}
	
	
	private List<String> queueEncodeAudioToOga(String pathToFfMpeg, File inputFile, File outputFile) {

		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-codec:a");
		commands.add("libvorbis");
		commands.add("-f");
		commands.add("oga");
		commands.add(outputFile.getAbsolutePath());

		return commands;
	}

	private List<String> queueEncodeVideoToMp4(String pathToFfMpeg, File inputFile, File outputFile) {

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

		return commands;
	}

	private List<String> queueEncodeVideoToWebM(String pathToFfMpeg, File inputFile, File outputFile) {

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

		return commands;
	}

	private List<String> queueEncodeVideoToOgv(String pathToFfMpeg, File inputFile, File outputFile) {

		// ffmpeg -y -i input.mp4 -sn -codec:v libtheora -qscale:v 7 -codec:a
		// libvorbis -qscale:a 5 output.ogv

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

		return commands;
	}
	
	

	
	private List<String> removeAudioTags(String pathToFfMpeg, File inputFile, File outputFile) {
//		ffmpeg -i tagged.mp3 -vn -codec:a copy -map_metadata -1 out.mp3
		
		List<String> commands = new ArrayList<String>();
		commands.add(pathToFfMpeg);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-vn");
		commands.add("-codec:a");
		commands.add("copy");
		commands.add("-map_metadata");
		commands.add("-1");
		commands.add(outputFile.getAbsolutePath());
		
		
		return commands;
	}



	public List<String> queueCopyAudioWithoutTags(File mediaFile, File playlistTemporaryFile) throws MediaItemEncodeException{
		String pathToFfMpeg = configurationManager.getConfigurationValue(MashUpMediaConstants.FFMPEG_PATH);
		if (StringUtils.isBlank(pathToFfMpeg)) {
			String errorText = "Unable to encode media, ffmpeg is not configured.";
			throw new MediaItemEncodeException(EncodeExceptionType.ENCODER_NOT_CONFIGURED, errorText);
		}

		List<String> commands = removeAudioTags(pathToFfMpeg, mediaFile, playlistTemporaryFile);
		return commands;
		
	}
	
	

}
