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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MediaItemTranscodeException;
import org.mashupmedia.exception.MediaItemTranscodeException.EncodeExceptionType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.util.AdminHelper;

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
	public List<String> getEncodingProcessCommands(String encoderPath, MediaContentType mediaContentType,
			Path inputPath, Path outputPath)
			throws MediaItemTranscodeException {

		// File inputFile = inputPath.toFile();
		// Path inpuPath = inputPath.toFile();
		// File outputFile = FileHelper.getEncodedMediaFile(mediaItem,
		// mediaContentType);
		User user = AdminHelper.getLoggedInUser();
		// user.getUserTempPath()
		// Path outputPath =
		// user.getUserTempPath().resolve(String.valueOf(System.currentTimeMillis()));

		// boolean isDeleted = FileHelper.deleteFile(outputFile);

		// if (!isDeleted) {
		// String errorText = "Unable to delete encoded media file will try when
		// webserver stops: "
		// + outputFile.getAbsolutePath();
		// log.error(errorText);
		// }

		// File

		String inputAbsolutePath = inputPath.toAbsolutePath().toString();
		String outputAbsolutePath = outputPath.toAbsolutePath().toString();

		List<String> commands = new ArrayList<String>();

		switch (mediaContentType) {
			case MediaContentType.AUDIO_AAC:
				commands = aacEncodeCommands(encoderPath, inputAbsolutePath, outputAbsolutePath);
				break;
			case MediaContentType.AUDIO_MP3:
				commands = mp3EncodeCommands(encoderPath, inputAbsolutePath, outputAbsolutePath);
				break;
			case MediaContentType.VIDEO_MP4:
				commands = mp4EncodeCommands(encoderPath, inputAbsolutePath, outputAbsolutePath);
				break;
			case MediaContentType.VIDEO_WEBM:
				commands = webMEncodeCommands(encoderPath, inputAbsolutePath, outputAbsolutePath);
				break;
			case MediaContentType.VIDEO_OGG:
				commands = ogvEncodeCommands(encoderPath, inputAbsolutePath, outputAbsolutePath);
				break;
			default:
				throw new MediaItemTranscodeException(EncodeExceptionType.UNSUPPORTED_ENCODING_FORMAT,
						mediaContentType.name() + " not supported");
		}

		return commands;

	}

	private List<String> aacEncodeCommands(String encoderPath, String inputPath, String outputPath) {

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputPath);
		commands.add("-codec:a");
		commands.add("libfdk_aac");
		commands.add("-b:a");
		commands.add("128k");
		commands.add("-f");
		commands.add("aac");
		commands.add(outputPath);

		// ffmpeg -i input.wav -c:a libfdk_aac -b:a 128k output.m4a

		return commands;
	}

	private List<String> mp3EncodeCommands(String encoderPath, String inputPath, String outputPath) {

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputPath);
		commands.add("-codec:a");
		commands.add("libmp3lame");
		// commands.add("-b:a");
		// commands.add("192k");
		commands.add("-q:a");
		commands.add("2");
		commands.add("-f");
		commands.add("mp3");
		commands.add(outputPath);

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

	private List<String> mp4EncodeCommands(String encoderPath, String inputPath, String outputPath) {

		// ffmpeg -y -i test.avi -c:v libx264 -preset:v veryfast -strict
		// experimental -c:a aac -b:a 240k -f mp4 output.encoded

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputPath);
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
		commands.add(outputPath);

		return commands;
	}

	private List<String> webMEncodeCommands(String encoderPath, String inputPath, String outputPath) {

		// ffmpeg -i input.mp4 -c:v libvpx -b:v 1M -c:a libvorbis -qscale:a 5
		// output.webm

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputPath);
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
		commands.add(outputPath);

		return commands;
	}

	private List<String> ogvEncodeCommands(String encoderPath, String inputPath, String outputPath) {

		// ffmpeg -y -i input.mp4 -sn -codec:v libtheora -qscale:v 7 -codec:a
		// libvorbis -qscale:a 5 output.ogv

		List<String> commands = new ArrayList<String>();
		commands.add(encoderPath);
		commands.add("-y");
		commands.add("-i");
		commands.add(inputPath);
		commands.add("-sn");
		commands.add("-codec:v");
		commands.add("libtheora");
		commands.add("-qscale:v");
		commands.add("7");
		commands.add("-codec:a");
		commands.add("libvorbis");
		commands.add("-qscale:a");
		commands.add("5");
		commands.add(outputPath);
		commands.add("-f");
		commands.add("ogv");

		return commands;
	}

}
