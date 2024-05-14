package org.mashupmedia.model.media;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mashupmedia.comparator.MediaEncodingComparator;
import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "media_items")
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Setter
@Getter
@NoArgsConstructor
public class MediaItem {

	public final static String TITLE_SEPERATOR = " - ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_items_generator")
	@SequenceGenerator(name = "media_items_generator", sequenceName = "media_items_seq", allocationSize = 1)
	private long id;
	private String fileName;
	private String path;
	@ManyToOne
	private Library library;
	private long sizeInBytes;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
	private String format;
	private int vote;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessed;
	@ManyToOne(cascade = { CascadeType.PERSIST })
	private User lastAccessedBy;
	private String mediaTypeValue;
	@Column(length = 1000)
	private String summary;
	private String displayTitle;
	private boolean enabled = true;
	private Long fileLastModifiedOn;
	private boolean publicAccess;
	private String uniqueName;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdOn")
	private List<Comment> comments;
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Tag> tags;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<MediaEncoding> mediaEncodings;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "mediaItem")
	private Set<PlaylistMediaItem> playlistMediaItems;

	public boolean isEncodedForWeb() {
		return false;
	}

	public MashupMediaType getMashupMediaType() {
		return MashupMediaType.getMediaType(mediaTypeValue);
	}

	public void setMashupMediaType(MashupMediaType mediaType) {
		mediaTypeValue = StringHelper.normaliseTextForDatabase(mediaType
				.toString());
	}

	public MediaEncoding getBestMediaEncoding() {
		if (mediaEncodings == null || mediaEncodings.isEmpty()) {
			return null;
		}

		List<MediaEncoding> mediaEncodingsList = new ArrayList<MediaEncoding>(
				mediaEncodings);
		Collections.sort(mediaEncodingsList, new MediaEncodingComparator());
		return mediaEncodingsList.get(0);
	}

	public File getStreamingFile() {
		MediaEncoding mediaEncoding = getBestMediaEncoding();
		if (mediaEncoding.isOriginal()) {
			return new File(getPath());
		}
		return FileHelper.getEncodedMediaFile(this, mediaEncoding.getMediaContentType());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		MediaItem other = (MediaItem) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MediaItem [id=");
		builder.append(id);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", path=");
		builder.append(path);
		builder.append(", library=");
		builder.append(library);
		builder.append(", sizeInBytes=");
		builder.append(sizeInBytes);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append(", format=");
		builder.append(format);
		builder.append(", vote=");
		builder.append(vote);
		builder.append(", lastAccessed=");
		builder.append(lastAccessed);
		builder.append(", lastAccessedBy=");
		builder.append(lastAccessedBy);
		builder.append(", mediaTypeValue=");
		builder.append(mediaTypeValue);
		builder.append(", summary=");
		builder.append(summary);
		builder.append(", displayTitle=");
		builder.append(displayTitle);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", fileLastModifiedOn=");
		builder.append(fileLastModifiedOn);
		builder.append(", publicAccess=");
		builder.append(publicAccess);
		builder.append(", uniqueName=");
		builder.append(uniqueName);
		builder.append(", comments=");
		builder.append(comments);
		builder.append(", tags=");
		builder.append(tags);
		builder.append("]");
		return builder.toString();
	}

}
