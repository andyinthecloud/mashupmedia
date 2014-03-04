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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ProcessManager {
	private static Logger logger = Logger.getLogger(ProcessManager.class);

	private List<String> processCache = new ArrayList<String>();

	public boolean callProcess(String path) throws IOException {
		List<String> commands = new ArrayList<String>();
		commands.add(path);
		return callProcess(commands);
	}

	public boolean callProcess(List<String> commands) throws IOException {

		String processString = commands.toString();
		try {

			if (processCache.contains(processString)) {
				logger.info("Process already running. Exiting...");
			}
			processCache.add(processString);
			logger.info("Starting process...");

			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			// processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			// String standardInputStreamText =
			// loadStream(process.getInputStream());

			StreamHandler standardOutputHandler = new StreamHandler(process.getInputStream());
			standardOutputHandler.start();

			StreamHandler errorOutputHandler = new StreamHandler(process.getInputStream());
			errorOutputHandler.start();

			// process.exitValue();

			try {
				int waitForValue = process.waitFor();
				logger.info("Process waitFor value = " + waitForValue);
			} catch (InterruptedException e) {
				logger.error("Error waiting for waitFor.", e);
			}

			int exitValue = process.exitValue();
			logger.info("Process exit value = " + exitValue);

			// if (errorOutputHandler.)

			boolean hasErrorOutput = errorOutputHandler.hasOutput();
			if (hasErrorOutput) {
				return false;
			} 
						
			return true;
		} finally {
			processCache.remove(processString);
		}

	}

	class StreamHandler extends Thread {
		StringBuilder outputBuilder;
		InputStream inputStream;

		StreamHandler(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public void run() {
			try {
				InputStreamReader reader = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(reader);
				String line = null;
				while ((line = br.readLine()) != null) {
					logger.info(line);
					outputBuilder.append(line);
				}
			} catch (IOException e) {
				logger.error("Error running ffmpeg", e);
			}
		}

		String getOutput() {
			return outputBuilder.toString();
		}

		boolean hasOutput() {
			if (outputBuilder == null || outputBuilder.length() == 0) {
				return false;
			}

			String outputText = StringUtils.trimToEmpty(outputBuilder.toString());
			if (StringUtils.isEmpty(outputText)) {
				return false;
			}

			return true;
		}

	}

}
