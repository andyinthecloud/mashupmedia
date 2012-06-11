package org.mashupmedia.model.playlist;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import org.mashupmedia.model.media.MediaItem;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
public abstract class PlaylistMediaItem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private int ranking;
	
	@ManyToOne
	private Playlist playlist;

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public abstract MediaItem getMediaItem();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		if (id != other.id)
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PlaylistMediaItem [id=");
		builder.append(id);
		builder.append(", ranking=");
		builder.append(ranking);
		builder.append(", playlist=");
		builder.append(playlist);
		builder.append("]");
		return builder.toString();
	}

}
