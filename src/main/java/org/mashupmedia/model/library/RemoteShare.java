package org.mashupmedia.model.library;

import java.io.Serializable;
import java.util.Date;

public class RemoteShare implements Serializable {

	private static final long serialVersionUID = 7908980996923387604L;

	private long id;
	private String uniqueName;
	private String remoteUrl;
	private Date lastAcccessed;
	private long totalPlayedMediaItems;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public Date getLastAcccessed() {
		return lastAcccessed;
	}

	public void setLastAcccessed(Date lastAcccessed) {
		this.lastAcccessed = lastAcccessed;
	}

	public long getTotalPlayedMediaItems() {
		return totalPlayedMediaItems;
	}

	public void setTotalPlayedMediaItems(long totalPlayedMediaItems) {
		this.totalPlayedMediaItems = totalPlayedMediaItems;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((uniqueName == null) ? 0 : uniqueName.hashCode());
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
		RemoteShare other = (RemoteShare) obj;
		if (id != other.id)
			return false;
		if (uniqueName == null) {
			if (other.uniqueName != null)
				return false;
		} else if (!uniqueName.equals(other.uniqueName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemoteShare [id=");
		builder.append(id);
		builder.append(", uniqueName=");
		builder.append(uniqueName);
		builder.append(", remoteUrl=");
		builder.append(remoteUrl);
		builder.append(", lastAcccessed=");
		builder.append(lastAcccessed);
		builder.append(", totalPlayedMediaItems=");
		builder.append(totalPlayedMediaItems);
		builder.append("]");
		return builder.toString();
	}

}
