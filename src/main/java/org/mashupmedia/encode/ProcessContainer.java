package org.mashupmedia.encode;

import java.util.Date;
import java.util.List;

public class ProcessContainer {

	private Process process;
	private Date startedOn;
	private List<String> commands;

	// public ProcessContainer(Process process, Date startedOn) {
	// this.process = process;
	// this.startedOn = startedOn;
	// }

	public Process getProcess() {
		return process;
	}

	public ProcessContainer(List<String> commands) {
		super();
		this.commands = commands;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commands == null) ? 0 : commands.hashCode());
		result = prime * result + ((startedOn == null) ? 0 : startedOn.hashCode());
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
		ProcessContainer other = (ProcessContainer) obj;
		if (commands == null) {
			if (other.commands != null)
				return false;
		} else if (!commands.equals(other.commands))
			return false;
		if (startedOn == null) {
			if (other.startedOn != null)
				return false;
		} else if (!startedOn.equals(other.startedOn))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessContainer [process=");
		builder.append(process);
		builder.append(", startedOn=");
		builder.append(startedOn);
		builder.append(", commands=");
		builder.append(commands);
		builder.append("]");
		return builder.toString();
	}

}
