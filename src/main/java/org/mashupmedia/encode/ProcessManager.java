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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.util.WebHelper.MediaContentType;
import org.springframework.stereotype.Component;

@Component
public class ProcessManager {
	private static Logger logger = Logger.getLogger(ProcessManager.class);

	private Map<ProcessKey, ProcessContainer> processCache = new HashMap<ProcessKey, ProcessContainer>();
	

	public String callProcess(String path, long mediaItemId, MediaContentType mediaContentType) throws IOException {
		List<String> commands = new ArrayList<String>();
		commands.add(path);

		String output = callProcess(commands, mediaItemId, mediaContentType);
		return output;
	}

	public String callProcess(List<String> commands, long mediaItemId, MediaContentType mediaContentType) throws IOException {

		ProcessKey processKey = generateProcessKey(mediaItemId, mediaContentType);
		
		try {
			
			ProcessContainer processContainer = processCache.get(processKey);
			if (processContainer != null) {
				logger.info("Found ffmpeg process. Destroying.");
				processContainer.getProcess().destroy();
			}
						
			logger.info("Starting process...");
			Date startedOn = new Date();			
			
			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			
			processContainer = new ProcessContainer(process, startedOn);			
			processCache.put(processKey, processContainer);

			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			StringBuilder outputBuilder = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null) {
				logger.info(line);
				outputBuilder.append(line);
			}
			IOUtils.closeQuietly(inputStream);

			try {
				int waitForValue = process.waitFor();
				logger.info("Process waitFor value = " + waitForValue);
			} catch (InterruptedException e) {
				logger.error("Error waiting for waitFor.", e);
			}

			int exitValue = process.exitValue();
			logger.info("Process exit value = " + exitValue);

			return outputBuilder.toString();
		} finally {
			processCache.remove(processKey);
		}

	}

	private ProcessKey generateProcessKey(long mediaItemId, MediaContentType mediaContentType) {
		if (mediaItemId == 0 || mediaContentType == null) {
			return null;
		}
		
		ProcessKey processKey = new ProcessKey(mediaItemId, mediaContentType);
		return processKey;
	}

}
