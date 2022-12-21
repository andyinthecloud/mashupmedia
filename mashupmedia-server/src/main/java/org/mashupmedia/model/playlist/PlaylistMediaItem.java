package org.mashupmedia.model.playlist;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.mashupmedia.model.User;
import org.mashupmedia.model.media.MediaItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlist_media_items")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
public class PlaylistMediaItem implements Serializable {

	private static final long serialVersionUID = 7640930812327773777L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private int ranking;
	
	@Transient
	private boolean playing;
	
	@ManyToOne
	private Playlist playlist;
	
	@ManyToOne
	private MediaItem mediaItem;

	@Transient
	private boolean isFirst;

	@Transient
	private boolean isLast;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mediaItem == null) ? 0 : mediaItem.hashCode());
		result = prime * result + ((playlist == null) ? 0 : playlist.hashCode());
		result = prime * result + ranking;
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
		PlaylistMediaItem other = (PlaylistMediaItem) obj;
		if (mediaItem == null) {
			if (other.mediaItem != null)
				return false;
		} else if (!mediaItem.equals(other.mediaItem))
			return false;
		if (playlist == null) {
			if (other.playlist != null)
				return false;
		} else if (!playlist.equals(other.playlist))
			return false;
		if (ranking != other.ranking)
			return false;
		return true;
	}

}
