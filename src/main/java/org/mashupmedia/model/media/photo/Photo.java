package org.mashupmedia.model.media.photo;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;

@Entity
@Indexed
@Cacheable
@XmlRootElement
public class Photo extends MediaItem {

	private static final long serialVersionUID = -2278500438492214953L;

	@IndexedEmbedded
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Album album;
	private String thumbnailPath;
	@Type(type = "text")
	private String metadata;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

}
