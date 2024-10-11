package org.mashupmedia.model.playlist;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.model.account.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlists")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Playlist {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlists_generator")
	@SequenceGenerator(name = "playlists_generator", sequenceName = "playlists_seq", allocationSize = 1)
	private long id;

	private String name;

	@OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<PlaylistMediaItem> playlistMediaItems = new HashSet<>();

	// @Transient
	// private List<PlaylistMediaItem> accessiblePlaylistMediaItems;

	@ManyToOne
	private User createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	@ManyToOne
	private User updatedBy;

	@OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	Set<UserPlaylistPosition> userPlaylistPositions = new HashSet<>();

	private boolean userDefault;

	private String mediaTypeValue;

	private boolean privatePlaylist;
	private boolean editOnlyByOwner;

	public void setMashupMediaType(MashupMediaType mashupMediaType) {
		this.mediaTypeValue = mashupMediaType.name();
	}

	public MashupMediaType getMashupMediaType() {
		return MashupMediaType.getMediaType(mediaTypeValue);
	}

	public List<PlaylistMediaItem> getAccessiblePlaylistMediaItems(User user) {
		return getPlaylistMediaItems()
				.stream()
				.filter(pmi -> pmi.getMediaItem().isEnabled())
				.filter(pmi -> pmi.getMediaItem().getLibrary().hasAccess(user))
				.sorted((pmi1, pmi2) -> pmi1.getRanking().compareTo(pmi2.getRanking()))
				.collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((mediaTypeValue == null) ? 0 : mediaTypeValue.hashCode());
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
		if (mediaTypeValue == null) {
			if (other.mediaTypeValue != null)
				return false;
		} else if (!mediaTypeValue.equals(other.mediaTypeValue))
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
		builder.append(", mediaTypeValue=");
		builder.append(mediaTypeValue);
		builder.append(", privatePlaylist=");
		builder.append(privatePlaylist);
		builder.append(", editOnlyByOwner=");
		builder.append(editOnlyByOwner);
		builder.append("]");
		return builder.toString();
	}

}
