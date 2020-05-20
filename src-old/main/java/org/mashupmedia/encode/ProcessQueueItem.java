package org.mashupmedia.encode;

import java.util.Date;
import java.util.List;

import org.mashupmedia.util.MediaItemHelper.MediaContentType;

public class ProcessQueueItem {
	private long mediaItemId;
	private MediaContentType mediaContentType;
	private Process process;
	private Date createdOn;
	private Date processStartedOn;
	private List<String> commands;


	public ProcessQueueItem(long mediaItemId, MediaContentType mediaContentType, List<String> commands) {
		this.mediaItemId = mediaItemId;
		this.mediaContentType = mediaContentType;
		this.commands = commands;
		this.createdOn = new Date();
	}

	public long getMediaItemId() {
		return mediaItemId;
	}

	public void setMediaItemId(long mediaItemId) {
		this.mediaItemId = mediaItemId;
	}

	public MediaContentType getMediaContentType() {
		return mediaContentType;
	}

	public void setMediaContentType(MediaContentType mediaContentType) {
		this.mediaContentType = mediaContentType;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Date getProcessStartedOn() {
		return processStartedOn;
	}

	public void setProcessStartedOn(Date processStartedOn) {
		this.processStartedOn = processStartedOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mediaContentType == null) ? 0 : mediaContentType.hashCode());
		result = prime * result + (int) (mediaItemId ^ (mediaItemId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessQueueItem other = (ProcessQueueItem) obj;
		if (mediaContentType != other.mediaContentType)
			return false;
		if (mediaItemId != other.mediaItemId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessQueueItem [mediaItemId=");
		builder.append(mediaItemId);
		builder.append(", mediaContentType=");
		builder.append(mediaContentType);
		builder.append(", process=");
		builder.append(process);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", processStartedOn=");
		builder.append(processStartedOn);
		builder.append(", commands=");
		builder.append(commands);
		builder.append("]");
		return builder.toString();
	}

}
