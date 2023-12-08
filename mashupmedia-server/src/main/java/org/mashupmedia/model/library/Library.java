package org.mashupmedia.model.library;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.User;
import org.mashupmedia.model.location.Location;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "libraries")
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Setter
@Getter
@NoArgsConstructor
public abstract class Library implements Serializable {

	private static final long serialVersionUID = 4337414530802373218L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private long id;
	private String name;
	@ManyToOne
	private Location location;
	private Date createdOn;
	@ManyToOne
	private User createdBy;
	private Date updatedOn;
	@ManyToOne
	private User updatedBy;
	private boolean enabled;
	private String scanMinutesInterval;
	private Date lastSuccessfulScanOn;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "library")
	@OrderBy("createdOn")
	private List<RemoteShare> remoteShares;
	private boolean remote;
	private String status;
	@ManyToMany
	@JoinTable(name = "libraries_users_access", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "library_id"))
	private Set<User> users;

	public enum LibraryType {
		ALL, MUSIC, VIDEO, PHOTO
	}

	public enum LibraryStatusType {
		NONE, WORKING, ERROR, UNABLE_TO_CONNECT_TO_REMOTE_LIBRARY, OK;
	}

	public abstract LibraryType getLibraryType();

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

	public void setLibraryStatusType(LibraryStatusType libraryStatusType) {
		this.status = libraryStatusType.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}

	public boolean hasAccess(User user) {
		if (user == null) {
			return false;
		}

		if (getCreatedBy().equals(user)) {
			return true;
		}

		return getUsers().stream().anyMatch(u -> u.equals(user));
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
		builder.append(", lastSuccessfulScanOn=");
		builder.append(lastSuccessfulScanOn);
		builder.append(", remoteShares=");
		builder.append(remoteShares);
		builder.append(", remote=");
		builder.append(remote);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}

	public String getLibraryTypeValue() {
		LibraryType libraryType = getLibraryType();
		return libraryType.toString().toLowerCase();
	}
}
