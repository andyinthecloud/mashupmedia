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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessManager {
	private static Logger logger = Logger.getLogger(ProcessManager.class);

	public final static String KEY_TOTAL_FFMPEG_PROCESSES = "totalFfMpegProcesses";
	public final static int DEFAULT_TOTAL_FFMPEG_PROCESSES = 0;

	@Autowired
	private ConfigurationManager configurationManager;

	private Map<ProcessKey, ProcessContainer> processCache = new LinkedHashMap<ProcessKey, ProcessContainer>();

	public Map<ProcessKey, ProcessContainer> getProcessCache() {
		return processCache;
	}

	public String callProcess(String path) throws IOException {
		List<String> commands = new ArrayList<String>();
		commands.add(path);

		String textOutput = callProcess(commands);
		return textOutput;
	}

	public void callProcess(List<String> commands, long mediaItemId, MediaContentType mediaContentType)
			throws IOException {

		ProcessKey processKey = generateProcessKey(mediaItemId, mediaContentType);
		ProcessContainer processContainer = processCache.get(processKey);
		if (processContainer != null) {
			logger.info("Found ffmpeg process. Destroying.");
			processContainer.getProcess().destroy();
		}

		processContainer = new ProcessContainer(commands);

		processCache.put(processKey, processContainer);

		int totalFfMpegProcesses = NumberUtils.toInt(
				configurationManager.getConfigurationValue(KEY_TOTAL_FFMPEG_PROCESSES), DEFAULT_TOTAL_FFMPEG_PROCESSES);
		Set<ProcessKey> processKeys = processCache.keySet();
		if (processKeys.size() <= totalFfMpegProcesses) {
			startProcess(processKey, processContainer);
		}
	}

	protected void startProcess(ProcessKey processKey, ProcessContainer processContainer) throws IOException {

		try {
			logger.info("Starting process...");
			List<String> commands = processContainer.getCommands();

			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			processContainer.setProcessStartedOn(new Date());
			processContainer.setProcess(process);

			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				logger.info(line);
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

		} finally {
			processCache.remove(processKey);

			ProcessKey nextProcessKeyInQueue = getNextProcessKeyInQueue();
			if (nextProcessKeyInQueue != null) {
				ProcessContainer nextProcessContainerInQueue = processCache.get(nextProcessKeyInQueue);
				startProcess(nextProcessKeyInQueue, nextProcessContainerInQueue);
			}
		}

	}

	protected ProcessKey getNextProcessKeyInQueue() {
		if (processCache == null || processCache.isEmpty()) {
			return null;
		}

		Set<ProcessKey> processKeys = processCache.keySet();
		return processKeys.iterator().next();
	}

	public String callProcess(List<String> commands) throws IOException {

		logger.info("Starting process...");

		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();

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

	}

	private ProcessKey generateProcessKey(long mediaItemId, MediaContentType mediaContentType) {
		if (mediaItemId == 0 || mediaContentType == null) {
			return null;
		}

		ProcessKey processKey = new ProcessKey(mediaItemId, mediaContentType);
		return processKey;
	}

	public boolean isInProcessQueue(long mediaItemId, MediaContentType mediaContentType) {
		ProcessKey processKey = generateProcessKey(mediaItemId, mediaContentType);
		if (processCache.containsKey(processKey)) {
			return true;
		}

		return false;
	}

	public boolean killProcess(long mediaItemId, MediaContentType mediaContentType) {
		ProcessKey processKey = generateProcessKey(mediaItemId, mediaContentType);
		if (processKey == null) {
			return false;
		}

		ProcessContainer processContainer = processCache.get(processKey);
		if (processContainer == null) {
			return false;
		}

		Process process = processContainer.getProcess();
		if (process != null) {
			process.destroy();
		}

		processCache.remove(processKey);
		return true;
	}

	public boolean moveProcess(int index, long mediaItemId, MediaContentType mediaContentType) {
		ProcessKey processKey = generateProcessKey(mediaItemId, mediaContentType);
		if (processKey == null) {
			return false;
		}

		ProcessContainer processContainer = processCache.get(processKey);
		if (processContainer == null) {
			return false;
		}

		Process process = processContainer.getProcess();
		if (process != null) {
			return false;
		}

		List<ProcessKey> processKeys = new ArrayList<ProcessKey>(processCache.keySet());
		processKeys.remove(processKey);
		processKeys.add(index, processKey);

		for (ProcessKey pk : processKeys) {
			processContainer = processCache.get(pk);
			processCache.remove(pk);
			processCache.put(pk, processContainer);
		}

		return true;
	}

}
