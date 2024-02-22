package org.mashupmedia.model.library;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.mashupmedia.model.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "libraries")
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
public abstract class Library implements Serializable {

	private static final long serialVersionUID = 4337414530802373218L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@EqualsAndHashCode.Include
	private String name;
	// @ManyToOne
	// private Location location;

	@EqualsAndHashCode.Include
	private String path;
	@EqualsAndHashCode.Include
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LocationType locationType = LocationType.LOCAL_DEFAULT;
	private Date createdOn;
	@ManyToOne
	private User user;
	private Date updatedOn;
	@ManyToOne
	private User updatedBy;
	private boolean enabled;
	private String scanMinutesInterval;
	private Date lastSuccessfulScanOn;
	private String status;
	@ManyToMany
	@JoinTable(name = "libraries_share_users", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "library_id"))
	private Set<User> shareUsers;
	private boolean privateAccess;

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


	public boolean hasAccess(User user) {
		if (this.user == null || user == null) {
			return false;
		}		

		if (this.user.equals(user)) {
			return true;
		}

		return getShareUsers().stream().anyMatch(u -> u.equals(user));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Library [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", path=");
		builder.append(path);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", user=");
		builder.append(user);
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
