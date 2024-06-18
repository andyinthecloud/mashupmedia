package org.mashupmedia.model.media.music;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.media.MetaImage;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@ToString
@Builder
public class Album {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "music_albums_generator")
	@SequenceGenerator(name = "music_albums_generator", sequenceName = "music_albums_seq", allocationSize = 1)
	private long id;
	private String name;
	private String folderName;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Artist artist;

	// @ManyToOne(cascade = { CascadeType.ALL })
	// private MetaImage albumArtImage;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "albums_meta_images", joinColumns = @JoinColumn(name = "meta_image_id"), inverseJoinColumns = @JoinColumn(name = "album_id"))
	@Builder.Default
	private Set<MetaImage> metaImages = new HashSet<>();

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
