package org.mashupmedia.model.media.photo;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.mashupmedia.model.media.MediaItem;

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
	private String webOptimisedImagePath;
	private int orientation;
	private long takenOn;

	@Transient
	private Photo previousPhoto;

	@Transient
	private Photo nextPhoto;

	public long getTakenOn() {
		return takenOn;
	}

	public void setTakenOn(long takenOn) {
		this.takenOn = takenOn;
	}

	public Photo getPreviousPhoto() {
		return previousPhoto;
	}

	public void setPreviousPhoto(Photo previousPhoto) {
		this.previousPhoto = previousPhoto;
	}

	public Photo getNextPhoto() {
		return nextPhoto;
	}

	public void setNextPhoto(Photo nextPhoto) {
		this.nextPhoto = nextPhoto;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getWebOptimisedImagePath() {
		return webOptimisedImagePath;
	}

	public void setWebOptimisedImagePath(String webOptimisedImagePath) {
		this.webOptimisedImagePath = webOptimisedImagePath;
	}

}
