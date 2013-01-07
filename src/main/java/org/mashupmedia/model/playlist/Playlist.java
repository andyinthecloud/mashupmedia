package org.mashupmedia.model.playlist;

import java.io.Serializable;
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
public class Playlist implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum PlaylistType {
		ALL, MUSIC;

		public String getValue() {
			return toString().toLowerCase();
		}

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

	@OneToMany(mappedBy = "playlist")
	@OrderBy("ranking")
	private List<PlaylistMediaItem> playlistMediaItems;

	@ManyToOne
	private User createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	@ManyToOne
	private User updatedBy;

	@ManyToOne
	private Group group;

	private boolean isUserDefault;

	private String playlistTypeValue;

	public String getPlaylistTypeValue() {
		return playlistTypeValue;
	}

	public void setPlaylistTypeValue(String playlistTypeValue) {
		this.playlistTypeValue = playlistTypeValue;
	}

	public void setPlaylistType(PlaylistType playlistType) {
		this.playlistTypeValue = playlistType.getValue();
	}
	
	public PlaylistType getPlaylistType() {
		PlaylistType[] playlistTypes = PlaylistType.values();
		for (PlaylistType playlistType : playlistTypes) {
			if (playlistType.getValue().equalsIgnoreCase(getPlaylistTypeValue())) {
				return playlistType;
			}
		}
		return PlaylistType.MUSIC;
	}

	public boolean isUserDefault() {
		return isUserDefault;
	}

	public boolean getIsUserDefault() {
		return isUserDefault();
	}

	public void setUserDefault(boolean isUserDefault) {
		this.isUserDefault = isUserDefault;
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

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((playlistTypeValue == null) ? 0 : playlistTypeValue.hashCode());
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
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (playlistTypeValue == null) {
			if (other.playlistTypeValue != null)
				return false;
		} else if (!playlistTypeValue.equals(other.playlistTypeValue))
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
		builder.append(createdBy);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append(", updatedBy=");
		builder.append(updatedBy);
		builder.append(", group=");
		builder.append(group);
		builder.append(", isDefault=");
		builder.append(isUserDefault);
		builder.append(", playlistType=");
		builder.append(playlistTypeValue);
		builder.append("]");
		return builder.toString();
	}

}
