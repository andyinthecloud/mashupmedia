package org.mashupmedia.model.media;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
public class MediaItem implements Serializable {
	private static final long serialVersionUID = -6694717782091959485L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String fileName;
	private String path;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Library library;
	private long sizeInBytes;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
	private String format;
	private int vote;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessed;
	@ManyToOne
	private User lastAccessedBy;

	public User getLastAccessedBy() {
		return lastAccessedBy;
	}

	public void setLastAccessedBy(User lastAccessedBy) {
		this.lastAccessedBy = lastAccessedBy;
	}

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public int getVote() {
		return vote;
	}

	public void setVote(int votes) {
		this.vote = votes;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date lastModified) {
		this.updatedOn = lastModified;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((library == null) ? 0 : library.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (int) (sizeInBytes ^ (sizeInBytes >>> 32));
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
		MediaItem other = (MediaItem) obj;
		if (library == null) {
			if (other.library != null)
				return false;
		} else if (!library.equals(other.library))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (sizeInBytes != other.sizeInBytes)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Media [id=");
		builder.append(id);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", path=");
		builder.append(path);
		builder.append(", library=");
		builder.append(library);
		builder.append(", sizeInBytes=");
		builder.append(sizeInBytes);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append(", format=");
		builder.append(format);
		builder.append("]");
		return builder.toString();
	}

}
