package org.mashupmedia.web.page;

import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.util.MediaItemHelper.MediaItemSequenceType;

public class PhotoPage {

	private MediaItemSequenceType mediaItemSequenceType;
	private Photo photo;

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public MediaItemSequenceType getMediaItemSequenceType() {
		return mediaItemSequenceType;
	}

	public void setMediaItemSequenceType(MediaItemSequenceType mediaItemSequenceType) {
		this.mediaItemSequenceType = mediaItemSequenceType;
	}

}
