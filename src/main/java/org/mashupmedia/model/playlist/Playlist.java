package org.mashupmedia.model.playlist;

import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;

@Entity
@Cacheable
public class Playlist {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

	@OneToMany(mappedBy = "playlist", targetEntity = PlaylistMediaItem.class)
	@OrderBy("ranking")
	private List<PlaylistMediaItem> playlistMediaItems;

	@ManyToOne
	private User owner;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	@ManyToOne
	private User updatedBy;

	@ManyToOne
	private Group group;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessedOn;

	@ManyToOne
	private User lastAccessedBy;

	private boolean isDefault;

	private String playlistType;

	public String getPlaylistType() {
		return playlistType;
	}

	public void setPlaylistType(String playlistType) {
		this.playlistType = playlistType;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<PlaylistMediaItem> getPlaylistMediaItems() {
		return playlistMediaItems;
	}

	public void setPlaylistMediaItems(List<PlaylistMediaItem> playlistMediaItems) {
		this.playlistMediaItems = playlistMediaItems;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Date getLastAccessedOn() {
		return lastAccessedOn;
	}

	public void setLastAccessedOn(Date lastAccessed) {
		this.lastAccessedOn = lastAccessed;
	}

	public User getLastAccessedBy() {
		return lastAccessedBy;
	}

	public void setLastAccessedBy(User lastAccessedBy) {
		this.lastAccessedBy = lastAccessedBy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result
				+ ((playlistType == null) ? 0 : playlistType.hashCode());
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
		Playlist other = (Playlist) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (playlistType == null) {
			if (other.playlistType != null)
				return false;
		} else if (!playlistType.equals(other.playlistType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Playlist [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", playlistMediaItems=");
		builder.append(playlistMediaItems);
		builder.append(", owner=");
		builder.append(owner);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append(", updatedBy=");
		builder.append(updatedBy);
		builder.append(", group=");
		builder.append(group);
		builder.append(", lastAccessedOn=");
		builder.append(lastAccessedOn);
		builder.append(", lastAccessedBy=");
		builder.append(lastAccessedBy);
		builder.append(", isDefault=");
		builder.append(isDefault);
		builder.append(", playlistType=");
		builder.append(playlistType);
		builder.append("]");
		return builder.toString();
	}

}
