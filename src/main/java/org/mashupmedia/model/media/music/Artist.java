package org.mashupmedia.model.media.music;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;

@Entity
@Table(name = "artists")
@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
public class Artist implements Serializable {
	private static final long serialVersionUID = -5361832134097788033L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private long id;
	@Field(analyze = Analyze.NO)
	private String name;
	private String folderName;
	@OneToMany(mappedBy = "artist")
	@OrderBy("name")
	@XmlTransient
	private List<Album> albums;
	private String indexText;
	private String indexLetter;
	private String remoteId;

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public String getIndexText() {
		return indexText;
	}

	public void setIndexText(String indexWord) {
		this.indexText = indexWord;
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

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Artist other = (Artist) obj;
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
		builder.append("Artist [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", folderName=");
		builder.append(folderName);
		builder.append(", indexText=");
		builder.append(indexText);
		builder.append(", indexLetter=");
		builder.append(indexLetter);
		builder.append(", remoteId=");
		builder.append(remoteId);
		builder.append("]");
		return builder.toString();
	}

}
