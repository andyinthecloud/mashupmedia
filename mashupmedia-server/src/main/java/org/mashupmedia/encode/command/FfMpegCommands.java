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

package org.mashupmedia.encode.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.exception.MediaItemEncodeException;
import org.mashupmedia.exception.MediaItemEncodeException.EncodeExceptionType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FfMpegCommands implements EncodeCommands {

	public static final String EXECUTABLE_NAME = "ffmpeg";

	@Override
	public String getEncoderPathKey() {
		return "ffmpegPath";
	}

	@Override
	public String getTestOutputParameter() {
		return "ffmpeg";
	}

	@Override
	public List<String> getEncodingProcessCommands(String encoderPath, MediaItem mediaItem,
			MediaContentType mediaContentType)
			throws MediaItemEncodeException {

		File inputFile = new File(mediaItem.getPath());
		File outputFile = FileHelper.getEncodedMediaFile(mediaItem, mediaContentType);
		boolean isDeleted = FileHelper.deleteFile(outputFile);

		if (!isDeleted) {
			String errorText = "Unable to delete encoded media file will try when webserver stops: "
					+ outputFile.getAbsolutePath();
			log.error(errorText);
		}

		List<String> commands = new ArrayList<String>();

		if (mediaContentType == MediaContentType.AUDIO_MP3) {
			commands = mp3EncodeCommands(encoderPath, inputFile, outputFile);
		} else if (mediaContentType == MediaContentType.VIDEO_MP4) {
			commands = mp4EncodeCommands(encoderPath, inputFile, outputFile);
		} else if (mediaContentType == MediaContentType.VIDEO_WEBM) {
			commands = webMEncodeCommands(encoderPath, inputFile, outputFile);
		} else if (mediaContentType == MediaContentType.VIDEO_OGG) {
			commands = ogvEncodeCommands(encoderPath, inputFile, outputFile);
		} else {
			throw new MediaItemEncodeException(EncodeExceptionType.UNSUPPORTED_ENCODING_FORMAT,
					mediaContentType.name() + " not supported");
		}

		return commands;

	}

	private List<String> mp3EncodeCommands(String encoderPath, File inputFile, File outputFile) {

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputFile.getAbsolutePath());
		commands.add("-codec:a");
		commands.add("libmp3lame");
		// commands.add("-b:a");
		// commands.add("192k");
		commands.add("-q:a");
		commands.add("2");
		commands.add("-f");
		commands.add("mp3");
		commands.add(outputFile.getAbsolutePath());

		// ffmpeg -i input.wav -codec:a libmp3lame -q:a 2 output.mp3

		return commands;
	}

	// private List<String> queueEncodeAudioToOga(String pathToFfMpeg, File
	// inputFile, File outputFile) {

	// List<String> commands = new ArrayList<String>();
	// commands.add(pathToFfMpeg);
	// commands.add("-y");
	// commands.add("-i");
	// commands.add(inputFile.getAbsolutePath());
	// commands.add("-codec:a");
	// commands.add("libvorbis");
	// commands.add("-f");
	// commands.add("oga");
	// commands.add(outputFile.getAbsolutePath());

	// return commands;
	// }

	private List<String> mp4EncodeCommands(String encoderPath, File inputFile, File outputFile) {

		// ffmpeg -y -i test.avi -c:v libx264 -preset:v veryfast -strict
		// experimental -c:a aac -b:a 240k -f mp4 output.encoded

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
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

	private List<String> webMEncodeCommands(String encoderPath, File inputFile, File outputFile) {

		// ffmpeg -i input.mp4 -c:v libvpx -b:v 1M -c:a libvorbis -qscale:a 5
		// output.webm

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
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

	private List<String> ogvEncodeCommands(String encoderPath, File inputFile, File outputFile) {

		// ffmpeg -y -i input.mp4 -sn -codec:v libtheora -qscale:v 7 -codec:a
		// libvorbis -qscale:a 5 output.ogv

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
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

}
