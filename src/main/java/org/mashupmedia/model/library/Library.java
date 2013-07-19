package org.mashupmedia.model.library;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.search.annotations.IndexedEmbedded;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.mashupmedia.model.location.Location;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
public abstract class Library implements Serializable {

	private static final long serialVersionUID = 4337414530802373218L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private long id;
	private String name;
	@ManyToOne(cascade = CascadeType.ALL)
	private Location location;
	private Date createdOn;
	@ManyToOne
	private User createdBy;
	private Date updatedOn;
	@ManyToOne
	private User updatedBy;
	private boolean enabled;
	private String scanMinutesInterval;
	@IndexedEmbedded
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Group> groups;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdOn")
	private List<RemoteShare> remoteShares;
	private boolean remote;
	private String status;
	private boolean updating;

	public boolean isUpdating() {
		return updating;
	}

	public void setUpdating(boolean updating) {
		this.updating = updating;
	}

	public enum LibraryStatusType {
		NONE, WORKING, ERROR, UNABLE_TO_CONNECT_TO_REMOTE_LIBRARY;
	}

	public LibraryStatusType getLibraryStatusType() {
		if (this.status == null) {
			return LibraryStatusType.NONE;
		}

		LibraryStatusType[] libraryStatusTypes = LibraryStatusType.values();
		for (LibraryStatusType libraryStatusType : libraryStatusTypes) {
			if (libraryStatusType.toString().equalsIgnoreCase(this.status)) {
				return libraryStatusType;
			}
		}

		return LibraryStatusType.NONE;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(LibraryStatusType libraryStatusType) {
		this.status = libraryStatusType.toString();
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	public List<RemoteShare> getRemoteShares() {
		return remoteShares;
	}

	public void setRemoteShares(List<RemoteShare> remoteShares) {
		this.remoteShares = remoteShares;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
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

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date lastModifiedOn) {
		this.updatedOn = lastModifiedOn;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User lastModifiedBy) {
		this.updatedBy = lastModifiedBy;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getScanMinutesInterval() {
		return scanMinutesInterval;
	}

	public void setScanMinutesInterval(String scanMinutesInterval) {
		this.scanMinutesInterval = scanMinutesInterval;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Library other = (Library) obj;
		if (id != other.id)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Library [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", location=");
		builder.append(location);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append(", updatedBy=");
		builder.append(updatedBy);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", scanMinutesInterval=");
		builder.append(scanMinutesInterval);
		builder.append(", groups=");
		builder.append(groups);
		builder.append(", remoteShares=");
		builder.append(remoteShares);
		builder.append("]");
		return builder.toString();
	}

}
