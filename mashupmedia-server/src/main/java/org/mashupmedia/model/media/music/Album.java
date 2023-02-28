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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;


@Entity(name = "org.mashupmedia.model.media.music.Album")
@Table(name = "music_albums")
@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
public class Album implements Serializable {
	private static final long serialVersionUID = -6293786232429408277L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private String folderName;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@XmlTransient
	private Artist artist;
	@ManyToOne(cascade = { CascadeType.ALL })
	@XmlTransient
	private AlbumArtImage albumArtImage;
	@OneToMany(mappedBy = "album")
	@OrderBy("trackNumber")
	@XmlTransient
	private List<Track> tracks;
	private String indexText;
	private String indexLetter;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getIndexText() {
		return indexText;
	}

	public void setIndexText(String indexName) {
		this.indexText = indexName;
	}

	public String getIndexLetter() {
		return indexLetter;
	}

	public void setIndexLetter(String indexLetter) {
		this.indexLetter = indexLetter;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

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

	public void setAlbumArtImage(AlbumArtImage albumArtImage) {
		this.albumArtImage = albumArtImage;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
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
		builder.append(", folderName=");
		builder.append(folderName);
		builder.append(", artist=");
		if (artist != null) {
			builder.append(artist.getName());
		}
		builder.append(", albumArtImage=");
		builder.append(albumArtImage);
		builder.append(", tracks=");
		builder.append(tracks);
		builder.append(", indexName=");
		builder.append(indexText);
		builder.append(", indexLetter=");
		builder.append(indexLetter);
		builder.append("]");
		return builder.toString();
	}

}
