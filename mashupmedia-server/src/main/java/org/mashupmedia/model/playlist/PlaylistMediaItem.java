package org.mashupmedia.model.playlist;

import org.mashupmedia.model.media.MediaItem;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "playlist_media_items")
@Cacheable
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlaylistMediaItem {

	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	private long id;

	@EqualsAndHashCode.Include
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

}
