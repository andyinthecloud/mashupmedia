package org.mashupmedia.encode;

import java.util.Date;
import java.util.List;

public class ProcessContainer {
	private Process process;
	private Date createdOn;
	private Date processStartedOn;
	private List<String> commands;

	public ProcessContainer(List<String> commands) {
		this.createdOn = new Date();
		this.commands = commands;
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
		result = prime * result + ((commands == null) ? 0 : commands.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((processStartedOn == null) ? 0 : processStartedOn.hashCode());
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
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (processStartedOn == null) {
			if (other.processStartedOn != null)
				return false;
		} else if (!processStartedOn.equals(other.processStartedOn))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessContainer [process=");
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
