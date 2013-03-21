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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ProcessHelper {
	private static Logger logger = Logger.getLogger(ProcessHelper.class);

	private static List<String> processCache = new ArrayList<String>();

	public static String callProcess(String path) throws IOException {
		List<String> commands = new ArrayList<String>();
		commands.add(path);
		return callProcess(commands);
	}

	public synchronized static String callProcess(List<String> commands) throws IOException {

		String processString = commands.toString();
		try {

			if (processCache.contains(processString)) {
				logger.info("Process already running. Exiting...");
			}
			processCache.add(processString);
			logger.info("Starting process...");
			

			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = null;
			StringBuilder outputBuilder = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null) {
				logger.debug(line);
				outputBuilder.append(line);
			}

			try {
				logger.info("Process waitFor value = " + process.waitFor());
				logger.info("Process exit value = " + process.exitValue());
			} catch (InterruptedException e) {
				logger.error("Error running process:", e);
			}

			return outputBuilder.toString();
		} finally {
			processCache.remove(processString);
		}
	}

}
