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
import java.util.List;

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

	private List<ProcessQueueItem> processQueueItems = new ArrayList<ProcessQueueItem>();

	public List<ProcessQueueItem> getProcessQueueItems() {
		return processQueueItems;
	}

	public String callProcess(String path) throws IOException {
		List<String> commands = new ArrayList<String>();
		commands.add(path);

		String textOutput = callProcess(commands);
		return textOutput;
	}

	public void callProcess(List<String> commands, long mediaItemId, MediaContentType mediaContentType)
			throws IOException {

		ProcessQueueItem processQueueItem = generateProcessQueueItem(mediaItemId, mediaContentType, commands);
		destroyProcessIfAlreadyStarted(processQueueItem);
		processQueueItems.add(processQueueItem);

		int totalFfMpegProcesses = NumberUtils.toInt(
				configurationManager.getConfigurationValue(KEY_TOTAL_FFMPEG_PROCESSES), DEFAULT_TOTAL_FFMPEG_PROCESSES);

		if (processQueueItems.size() <= totalFfMpegProcesses) {
			startProcess(processQueueItem);
		}
	}

	protected ProcessQueueItem generateProcessQueueItem(long mediaItemId, MediaContentType mediaContentType,
			List<String> commands) {

		ProcessQueueItem processQueueItem = new ProcessQueueItem(mediaItemId, mediaContentType, commands);
		return processQueueItem;
	}

	protected void destroyProcessIfAlreadyStarted(ProcessQueueItem processQueueItem) {
		if (processQueueItems == null || processQueueItems.isEmpty()) {
			return;
		}

		boolean isDeleted = processQueueItems.remove(processQueueItem);
		logger.info("Process: " + processQueueItem.toString() + " deleted: " + isDeleted);

	}

	protected void startProcess(ProcessQueueItem processQueueItem) throws IOException {

		try {
			logger.info("Starting process...");
			List<String> commands = processQueueItem.getCommands();

			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			processQueueItem.setProcessStartedOn(new Date());
			processQueueItem.setProcess(process);

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
			processQueueItems.remove(processQueueItem);

			ProcessQueueItem nextProcessQueueItem = getNextProcessQueueItem();
			if (nextProcessQueueItem != null) {
				startProcess(nextProcessQueueItem);
			}
		}

	}

	protected ProcessQueueItem getNextProcessQueueItem() {
		if (processQueueItems == null || processQueueItems.isEmpty()) {
			return null;
		}

		return processQueueItems.get(0);
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

	public boolean isInProcessQueue(long mediaItemId, MediaContentType mediaContentType) {

		ProcessQueueItem processQueueItem = getProcessQueueItem(mediaItemId, mediaContentType);
		if (processQueueItem != null) {
			return true;
		}

		return false;
	}

	private ProcessQueueItem getProcessQueueItem(long mediaItemId, MediaContentType mediaContentType) {
		if (processQueueItems == null || processQueueItems.isEmpty()) {
			return null;
		}

		for (ProcessQueueItem processQueueItem : processQueueItems) {
			if (processQueueItem.getMediaItemId() == mediaItemId
					&& processQueueItem.getMediaContentType() == mediaContentType) {
				return processQueueItem;
			}

		}

		return null;

	}

	public boolean killProcess(long mediaItemId, MediaContentType mediaContentType) {

		ProcessQueueItem processQueueItem = getProcessQueueItem(mediaItemId, mediaContentType);
		if (processQueueItem == null) {
			return false;
		}

		Process process = processQueueItem.getProcess();
		if (process != null) {
			process.destroy();
		}

		processQueueItems.remove(processQueueItem);
		return true;
	}

	public boolean moveProcess(int index, long mediaItemId, MediaContentType mediaContentType) {
		
		if (processQueueItems == null || processQueueItems.isEmpty()) {
			return false;
		}
		
		if (index < 0 || (index > processQueueItems.size() - 1)) {
			return false;
		}		

		ProcessQueueItem processQueueItem = getProcessQueueItem(mediaItemId, mediaContentType);
		if (processQueueItem == null) {
			return false;
		}

		processQueueItems.remove(processQueueItem);
		processQueueItems.add(index, processQueueItem);
		return true;
	}

}
