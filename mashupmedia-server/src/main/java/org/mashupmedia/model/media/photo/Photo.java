package org.mashupmedia.model.media.photo;

import java.util.Date;

import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.model.media.MediaItem;
import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "photos")
@Cacheable
public class Photo extends MediaItem {


	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Album album;
	private String webOptimisedImagePath;
	private int orientation;
	@Temporal(TemporalType.TIMESTAMP)
	private Date takenOn;

	@Transient
	private Photo previousPhoto;

	@Transient
	private Photo nextPhoto;

	public Date getTakenOn() {
		return takenOn;
	}

	public void setTakenOn(Date takenOn) {
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

	public String getWebOptimisedImagePath() {
		return webOptimisedImagePath;
	}

	public void setWebOptimisedImagePath(String webOptimisedImagePath) {
		this.webOptimisedImagePath = webOptimisedImagePath;
	}

	@Override
	public MashupMediaType getMashupMediaType() {
		return MashupMediaType.PHOTO;
	}

}
