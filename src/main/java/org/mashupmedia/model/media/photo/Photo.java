package org.mashupmedia.model.media.photo;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

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
	private String url;
	private String thumbnailUrl;
	private String contentType;
	private String metaData;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Photo [album=");
		builder.append(album);
		builder.append(", url=");
		builder.append(url);
		builder.append(", thumbnailUrl=");
		builder.append(thumbnailUrl);
		builder.append(", contentType=");
		builder.append(contentType);
		builder.append(", metaData=");
		builder.append(metaData);
		builder.append("]");
		return builder.toString();
	}

}
