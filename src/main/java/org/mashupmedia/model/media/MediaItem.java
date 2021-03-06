package org.mashupmedia.model.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.mashupmedia.comparator.MediaEncodingComparator;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.StringHelper;

@Entity
@Table(name = "media_items")
@Indexed
@AnalyzerDef(name = "customanalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
		@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = { @Parameter(name = "language", value = "English") }) })
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
public class MediaItem implements Serializable {
	private static final long serialVersionUID = -6694717782091959485L;

	public final static String TITLE_SEPERATOR = " - ";

	public enum MediaType {
		SONG, VIDEO, PHOTO;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private long id;
	private String fileName;
	private String path;
	@IndexedEmbedded
//	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@ManyToOne
	@XmlTransient
	private Library library;
	private long sizeInBytes;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
	private String format;
	@Field(analyze = Analyze.NO)
	private int vote;
	@Temporal(TemporalType.TIMESTAMP)
	@Field(analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.SECOND)
	private Date lastAccessed;
	@ManyToOne(cascade = { CascadeType.PERSIST })
	@XmlTransient
	private User lastAccessedBy;
	@Field
	@Column(length = 1000)
	private String searchText;
	@Field(analyze = Analyze.NO)
	private String mediaTypeValue;
	@Column(length = 1000)
	private String summary;
	@Field(analyze = Analyze.NO)
	private String displayTitle;
	@Field(analyze = Analyze.NO)
	private boolean enabled;
	private long fileLastModifiedOn;
	@Field(analyze = Analyze.NO)
	private boolean publicAccess;
	@Field(analyze = Analyze.NO)
	private String uniqueName;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdOn")
	@XmlTransient
	private List<Comment> comments;
	@ManyToMany(fetch = FetchType.EAGER)
	@XmlTransient
	private Set<Tag> tags;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@XmlTransient
	private Set<MediaEncoding> mediaEncodings;

	public MediaItem() {
		this.enabled = true;
	}

	public Set<MediaEncoding> getMediaEncodings() {
		return mediaEncodings;
	}

	public void setMediaEncodings(Set<MediaEncoding> mediaEncodings) {
		this.mediaEncodings = mediaEncodings;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public boolean isPublicAccess() {
		return publicAccess;
	}

	public void setPublicAccess(boolean publicAccess) {
		this.publicAccess = publicAccess;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public long getFileLastModifiedOn() {
		return fileLastModifiedOn;
	}

	public void setFileLastModifiedOn(long fileLastModifiedOn) {
		this.fileLastModifiedOn = fileLastModifiedOn;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}

	public String getMediaTypeValue() {
		return mediaTypeValue;
	}

	public void setMediaTypeValue(String mediaTypeValue) {
		this.mediaTypeValue = mediaTypeValue;
	}

	public MediaType getMediaType() {
		MediaType mediaType = MediaItemHelper.getMediaType(mediaTypeValue);
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		mediaTypeValue = StringHelper.normaliseTextForDatabase(mediaType
				.toString());
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public User getLastAccessedBy() {
		return lastAccessedBy;
	}

	public void setLastAccessedBy(User lastAccessedBy) {
		this.lastAccessedBy = lastAccessedBy;
	}

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public int getVote() {
		return vote;
	}

	public void setVote(int votes) {
		this.vote = votes;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((library == null) ? 0 : library.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (int) (sizeInBytes ^ (sizeInBytes >>> 32));
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
		if (library == null) {
			if (other.library != null)
				return false;
		} else if (!library.equals(other.library))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (sizeInBytes != other.sizeInBytes)
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
		builder.append(", searchText=");
		builder.append(searchText);
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
