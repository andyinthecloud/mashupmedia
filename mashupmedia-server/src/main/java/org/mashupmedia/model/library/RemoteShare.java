package org.mashupmedia.model.library;

import java.io.Serializable;
import java.util.Date;

import org.mashupmedia.model.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "remote_shares")
@Cacheable
@NoArgsConstructor
@Getter
@Setter
public class RemoteShare implements Serializable {

	private static final long serialVersionUID = 7908980996923387604L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String uniqueName;
	private String remoteUrl;
	private Date createdOn = new Date();
	@ManyToOne
	private User createdBy;
	private Date lastAccessed;
	private long totalPlayedMediaItems;
	private String status;
	@ManyToOne
	private Library library;

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
