package org.mashupmedia.model.playlist;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlists")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
public class Playlist {

	public enum PlaylistType {
		ALL, MUSIC;

		public String getValue() {
			return toString().toLowerCase();
		}

		static public PlaylistType getPlaylistType(String value) {
			for (PlaylistType playlistType : PlaylistType.values()) {
				if (playlistType.getValue().equalsIgnoreCase(value)) {
					return playlistType;
				}
			}
			return PlaylistType.MUSIC;
		}

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

	@OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PlaylistMediaItem> playlistMediaItems = new HashSet<>();

	@Transient
	private List<PlaylistMediaItem> accessiblePlaylistMediaItems;

	@ManyToOne
	private User createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	@ManyToOne
	private User updatedBy;

	@OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<UserPlaylistPosition> userPlaylistPositions = new HashSet<>();

	private boolean userDefault;

	private String playlistTypeValue;

	private boolean privatePlaylist;
	private boolean editOnlyByOwner;
	
	public void setPlaylistType(PlaylistType playlistType) {
		this.playlistTypeValue = playlistType.getValue();
	}

	public PlaylistType getPlaylistType() {
		PlaylistType playlistType =  PlaylistType.getPlaylistType(getPlaylistTypeValue());
		return playlistType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		builder.append(", accessiblePlaylistMediaItems=");
		builder.append(accessiblePlaylistMediaItems);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append(", updatedBy=");
		builder.append(updatedBy);
		builder.append(", userDefault=");
		builder.append(userDefault);
		builder.append(", playlistTypeValue=");
		builder.append(playlistTypeValue);
		builder.append(", privatePlaylist=");
		builder.append(privatePlaylist);
		builder.append(", editOnlyByOwner=");
		builder.append(editOnlyByOwner);
		builder.append("]");
		return builder.toString();
	}

}
