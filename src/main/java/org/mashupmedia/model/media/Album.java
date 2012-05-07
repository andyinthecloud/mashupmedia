package org.mashupmedia.model.media;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
@Cacheable
public class Album {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	@ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Artist artist;
	@ManyToOne (cascade = {CascadeType.REMOVE})
	private AlbumArtImage albumArtImage;
	@OneToMany(mappedBy = "album")
	@OrderBy("trackNumber")
	private List<Song> songs;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public AlbumArtImage getAlbumArtImage() {
		return albumArtImage;
	}

	public void setAlbumArtImage(AlbumArtImage image) {
		this.albumArtImage = image;
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Album [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", artist=");
		builder.append(artist);
		builder.append(", image=");
		builder.append(albumArtImage);
		builder.append(", songs=");
		builder.append(songs);
		builder.append("]");
		return builder.toString();
	}

}