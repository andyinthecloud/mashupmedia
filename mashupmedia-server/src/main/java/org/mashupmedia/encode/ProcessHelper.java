/*
 * This file is part of MashupMedia.
 *
 * MashupMedia is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * MashupMedia is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MashupMedia. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.encode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessHelper {
	public static String callProcess(String path) throws IOException {
		List<String> commands = new ArrayList<String>();
		commands.add(path);

		String textOutput = callProcess(commands);
		return textOutput;
	}


	public static String callProcess(List<String> commands) throws IOException {

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {

			log.info("Starting process...");

			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			StringBuilder outputBuilder = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null) {
				log.info(line);
				outputBuilder.append(line);
			}

			try {
				int waitForValue = process.waitFor();
				log.info("Process waitFor value = " + waitForValue);
			} catch (InterruptedException e) {
				log.error("Error waiting for waitFor.", e);
			}

			int exitValue = process.exitValue();
			log.info("Process exit value = " + exitValue);

			return outputBuilder.toString();

		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				log.info("Unable to close stream", e);

			}
		}
	}

}
