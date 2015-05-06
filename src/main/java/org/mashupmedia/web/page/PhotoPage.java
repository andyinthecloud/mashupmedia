package org.mashupmedia.web.page;

import org.mashupmedia.model.media.photo.Photo;

public class PhotoPage {

	private Photo photo;
	private Photo previousPhoto;
	private Photo nextPhoto;

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
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

}
