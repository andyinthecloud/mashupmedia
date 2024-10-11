package org.mashupmedia.model.media.video;

import java.net.URI;

import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.model.media.MediaItem;
import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos")
@Cacheable
public class Video extends MediaItem {

	private String remoteId;
	private boolean ignoreRemoteContent;
	private String tagline;
	private float rating;
	private double runtime;

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public double getRuntime() {
		return runtime;
	}

	public void setRuntime(double runtime) {
		this.runtime = runtime;
	}

	public boolean isIgnoreRemoteContent() {
		return ignoreRemoteContent;
	}

	public void setIgnoreRemoteContent(boolean ignoreRemoteContent) {
		this.ignoreRemoteContent = ignoreRemoteContent;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	@Override
	public MashupMediaType getMashupMediaType() {
		return MashupMediaType.PHOTO;
	}


    public URI getPath() {
        throw new UnsupportedOperationException("Unimplemented method 'getPath'");
    }

}
