package org.mashupmedia.model.playlist;

import org.mashupmedia.dto.media.playlist.TranscodeStatusType;
import org.mashupmedia.model.media.MediaItem;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlist_media_items")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
public class PlaylistMediaItem {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_media_items_generator")
	@SequenceGenerator(name = "playlist_media_items_generator", sequenceName = "playlist_media_items_seq", allocationSize = 1)
	private long id;

	private Integer ranking;

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

	@Transient
	private TranscodeStatusType encoderStatusType;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ranking;
		result = prime * result + ((playlist == null) ? 0 : playlist.hashCode());
		result = prime * result + ((mediaItem == null) ? 0 : mediaItem.hashCode());
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
		if (ranking != other.ranking)
			return false;
		if (playlist == null) {
			if (other.playlist != null)
				return false;
		} else if (!playlist.equals(other.playlist))
			return false;
		if (mediaItem == null) {
			if (other.mediaItem != null)
				return false;
		} else if (!mediaItem.equals(other.mediaItem))
			return false;
		return true;
	}

	
}
