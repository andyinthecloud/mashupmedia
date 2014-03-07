package org.mashupmedia.model.media;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
@Cacheable
@XmlRootElement
public class Video extends MediaItem {
	private static final long serialVersionUID = 8105872585865313104L;

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

}
