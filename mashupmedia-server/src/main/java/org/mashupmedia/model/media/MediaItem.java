package org.mashupmedia.model.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "media_items")
@Indexed
@AnalyzerDef(name = "customanalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
		@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
				@Parameter(name = "language", value = "English") }) })
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MediaItem implements Serializable {
	private static final long serialVersionUID = -6694717782091959485L;

	public final static String TITLE_SEPERATOR = " - ";

	public enum MashupMediaType {
		TRACK, VIDEO, PHOTO;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private long id;
	private String fileName;
	@EqualsAndHashCode.Include
	private String path;
	@IndexedEmbedded
	@ManyToOne
	@XmlTransient
	@EqualsAndHashCode.Include
	private Library library;
	@EqualsAndHashCode.Include
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

	public MashupMediaType getMashupMediaType() {
		MashupMediaType mediaType = MediaItemHelper.getMediaType(mediaTypeValue);
		return mediaType;
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
