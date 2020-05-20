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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.service.ConfigurationManager;
import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessManager {

	public final static String KEY_TOTAL_FFMPEG_PROCESSES = "totalFfMpegProcesses";
	public final static int DEFAULT_TOTAL_FFMPEG_PROCESSES = 3;

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	private List<ProcessQueueItem> processQueueItems = new CopyOnWriteArrayList<ProcessQueueItem>();

	public Iterator<ProcessQueueItem> getProcessQueueItemsIterator() {
		if (processQueueItems == null) {
			return null;
		}

		return processQueueItems.iterator();
	}

	public String callProcess(String path) throws IOException {
		List<String> commands = new ArrayList<String>();
		commands.add(path);

		String textOutput = callProcess(commands);
		return textOutput;
	}

	public int getMaximumConcurrentProcesses() {
		int totalFfMpegProcesses = NumberUtils
				.toInt(configurationManager.getConfigurationValue(KEY_TOTAL_FFMPEG_PROCESSES));
		return totalFfMpegProcesses;
	}

	public void addProcessToQueue(List<String> commands, long mediaItemId,
			MediaContentType mediaContentType) {
		ProcessQueueItem processQueueItem =
				generateProcessQueueItem(mediaItemId, mediaContentType, commands);
		if (processQueueItems.contains(processQueueItem)) {
			log.info("Media is already queued for encoding: " + processQueueItem.toString());
			return;
		}

		processQueueItems.add(processQueueItem);
	}

	protected ProcessQueueItem generateProcessQueueItem(long mediaItemId,
			MediaContentType mediaContentType, List<String> commands) {

		ProcessQueueItem processQueueItem =
				new ProcessQueueItem(mediaItemId, mediaContentType, commands);
		return processQueueItem;
	}

	public void startProcess(ProcessQueueItem processQueueItem) throws IOException {

		try {
			log.info("Starting process...");
			List<String> commands = processQueueItem.getCommands();

			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			// The started on date should have already been set
			Date startedOn = processQueueItem.getProcessStartedOn();
			if (startedOn == null) {
				processQueueItem.setProcessStartedOn(new Date());
			}

			processQueueItem.setProcess(process);

			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				log.info(line);
			}

			inputStream.close();

			try {
				int waitForValue = process.waitFor();
				log.info("Process waitFor value = " + waitForValue);
			} catch (InterruptedException e) {
				log.error("Error waiting for waitFor.", e);
			}

			int exitValue = process.exitValue();
			log.info("Process exit value = " + exitValue);

		} finally {
			processQueueItems.remove(processQueueItem);
			encodeMediaItemTaskManager.processQueue();
		}

	}

	protected ProcessQueueItem getNextProcessQueueItem() {
		if (processQueueItems == null || processQueueItems.isEmpty()) {
			return null;
		}

		return processQueueItems.get(0);
	}

	public String callProcess(List<String> commands) throws IOException {

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

	// public boolean isEncoding(long mediaItemId, MediaContentType
	// mediaContentType) {
	//
	// ProcessQueueItem processQueueItem = getProcessQueueItem(mediaItemId,
	// mediaContentType);
	// if (processQueueItem == null) {
	// return false;
	// }
	// // if (processQueueItem != null) {
	// // return true;
	// // }
	//
	// Process process = processQueueItem.getProcess();
	// if (process == null) {
	// return false;
	// }
	//
	// return true;
	// }

	private ProcessQueueItem getProcessQueueItem(long mediaItemId,
			MediaContentType mediaContentType) {
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

		boolean isRemoved = processQueueItems.remove(processQueueItem);
		return isRemoved;
	}

	public void killProcesses(long mediaItemId) {

		List<ProcessQueueItem> processQueueItemsToBeDeleted = getProcessQueueItems(mediaItemId);
		if (processQueueItemsToBeDeleted == null || processQueueItemsToBeDeleted.isEmpty()) {
			return;
		}

		for (ProcessQueueItem processQueueItem : processQueueItemsToBeDeleted) {
			Process process = processQueueItem.getProcess();
			if (process != null) {
				process.destroy();
			}

			processQueueItemsToBeDeleted.remove(processQueueItem);
		}
	}

	protected List<ProcessQueueItem> getProcessQueueItems(long mediaItemId) {

		if (mediaItemId == 0) {
			return null;
		}
		List<ProcessQueueItem> matchingProcessQueueItems = new ArrayList<ProcessQueueItem>();

		if (processQueueItems == null || processQueueItems.isEmpty()) {
			return matchingProcessQueueItems;
		}

		for (ProcessQueueItem processQueueItem : processQueueItems) {
			if (processQueueItem.getMediaItemId() == mediaItemId) {
				matchingProcessQueueItems.add(processQueueItem);
			}
		}

		return matchingProcessQueueItems;
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
