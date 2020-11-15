package org.mashupmedia.model.media.photo;

import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;
import org.mashupmedia.model.media.MediaItem;

@Entity
@Table(name = "photos")
@Indexed
@Cacheable
@XmlRootElement
public class Photo extends MediaItem {

	private static final long serialVersionUID = -2278500438492214953L;

	@IndexedEmbedded
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Album album;
	private String webOptimisedImagePath;
	private int orientation;
	@Temporal(TemporalType.TIMESTAMP)
	@Field(analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.MILLISECOND)
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

}
