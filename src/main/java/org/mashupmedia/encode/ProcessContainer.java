package org.mashupmedia.encode;

import java.util.Date;

public class ProcessContainer {

	private Process process;
	private Date startedOn;
	
	public ProcessContainer(Process process, Date startedOn) {
		this.process = process;
		this.startedOn = startedOn;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Date getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(Date startedOn) {
		this.startedOn = startedOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessContainer [process=");
		builder.append(process);
		builder.append(", startedOn=");
		builder.append(startedOn);
		builder.append("]");
		return builder.toString();
	}

}
