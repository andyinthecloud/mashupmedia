package org.mashupmedia.model.media.music;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;

@Entity(name = "org.mashupmedia.model.media.music.Album")
@Table(name = "music_albums")
@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
public class Album implements Serializable {
	private static final long serialVersionUID = -6293786232429408277L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Field(analyze = Analyze.NO)
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
	private List<Song> songs;
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
		builder.append(", folderName=");
		builder.append(folderName);
		builder.append(", artist=");
		if (artist != null) {
			builder.append(artist.getName());
		}
		builder.append(", albumArtImage=");
		builder.append(albumArtImage);
		builder.append(", songs=");
		builder.append(songs);
		builder.append(", indexName=");
		builder.append(indexText);
		builder.append(", indexLetter=");
		builder.append(indexLetter);
		builder.append("]");
		return builder.toString();
	}

}
