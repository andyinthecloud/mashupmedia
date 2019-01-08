package org.mashupmedia.model.library;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.mashupmedia.model.User;

@Entity
@Cacheable
public class RemoteShare implements Serializable {

	private static final long serialVersionUID = 7908980996923387604L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String uniqueName;
	private String remoteUrl;
	private Date createdOn;
	@ManyToOne
	private User createdBy;
	private Date lastAccessed;
	private long totalPlayedMediaItems;
	private String status;
	@Transient
	private RemoteShareStatusType statusType;
	
	
	public enum RemoteShareStatusType {
		ENABLED, DISABLED, UNKNOWN
	}
	
	
	public RemoteShareStatusType getStatusType() {
		RemoteShareStatusType[] remoteShareStatusTypes = RemoteShareStatusType.values();
		for (RemoteShareStatusType remoteShareStatusType : remoteShareStatusTypes) {
			if (remoteShareStatusType.toString().equals(this.status)) {
				return remoteShareStatusType;
			}
		}
				
		return RemoteShareStatusType.UNKNOWN;
	}

	public void setStatusType(RemoteShareStatusType remoteShareStatusType) {
		this.statusType = remoteShareStatusType;
		this.status = remoteShareStatusType.toString();
	}

	public RemoteShare() {
		this.createdOn = new Date();
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

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

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
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
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", lastAccessed=");
		builder.append(lastAccessed);
		builder.append(", totalPlayedMediaItems=");
		builder.append(totalPlayedMediaItems);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}

	public void setStatusType(String remoteShareStatus) {
		RemoteShareStatusType[] remoteShareStatusTypes = RemoteShareStatusType.values();
		for (RemoteShareStatusType remoteShareStatusType : remoteShareStatusTypes) {
			if (remoteShareStatusType.toString().equalsIgnoreCase(remoteShareStatus)) {
				this.status = remoteShareStatusType.toString();
				return;
			}
		}
		
		this.status = RemoteShareStatusType.UNKNOWN.toString();
		
	}

}
