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
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class ProcessHelper {
	private static Logger logger = Logger.getLogger(ProcessHelper.class);

	public static void callProcess(String path) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(path);
		Process process = processBuilder.start();

		InputStream inputStream = process.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		String line = null;
		int exit = -1;

		while ((line = bufferedReader.readLine()) != null) {
			logger.debug(line);
			try {
				exit = process.exitValue();
				if (exit == 0) {
					break;
				}
			} catch (IllegalThreadStateException t) {
				process.destroy();
				break;
			}
		}
	}

}
