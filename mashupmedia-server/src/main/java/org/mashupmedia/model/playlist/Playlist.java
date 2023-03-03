package org.mashupmedia.model.playlist;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.mashupmedia.model.User;
import org.mashupmedia.util.PlaylistHelper;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "playlists")
@Cacheable
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Playlist {

	public enum PlaylistType {
		ALL, MUSIC;

		public String getValue() {
			return toString().toLowerCase();
		}

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private long id;

	@EqualsAndHashCode.Include
	private String name;

	@OneToMany(targetEntity = PlaylistMediaItem.class,  mappedBy = "playlist", cascade = CascadeType.PERSIST, orphanRemoval = true)
	@OrderBy("ranking")
	private List<PlaylistMediaItem> playlistMediaItems;

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
    List<UserPlaylistPosition> userPlaylistPositions;

	private boolean userDefault;

	private String playlistTypeValue;

	private boolean privatePlaylist;
	private boolean editOnlyByOwner;
	
	public void setPlaylistType(PlaylistType playlistType) {
		this.playlistTypeValue = playlistType.getValue();
	}

	public PlaylistType getPlaylistType() {
		PlaylistType playlistType = PlaylistHelper.getPlaylistType(getPlaylistTypeValue());
		return playlistType;
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
