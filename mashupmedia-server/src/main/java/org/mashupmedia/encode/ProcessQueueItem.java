package org.mashupmedia.encode;

import java.util.Date;
import java.util.List;

import org.mashupmedia.util.MediaItemHelper.MediaContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class ProcessQueueItem {
	private final long mediaItemId;
	private final MediaContentType mediaContentType;
	private final List<String> commands;
	private final Date createdOn = new Date();

	private Process process;
	private Date processStartedOn;

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
