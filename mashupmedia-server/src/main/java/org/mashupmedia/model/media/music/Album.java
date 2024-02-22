package org.mashupmedia.model.media.music;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity(name = "org.mashupmedia.model.media.music.Album")
@Table(name = "music_albums")
@Cacheable
@Setter
@Getter
@NoArgsConstructor
@ToString
public class Album  {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private String folderName;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Artist artist;
	@ManyToOne(cascade = { CascadeType.ALL })
	private MusicArtImage albumArtImage;
	@OneToMany(mappedBy = "album")
	@OrderBy("trackNumber")
	private List<Track> tracks;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Album other = (Album) obj;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
