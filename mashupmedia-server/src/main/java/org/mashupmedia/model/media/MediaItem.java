package org.mashupmedia.model.media;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.social.SocialConfiguration;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "media_items")
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
@ToString
public abstract class MediaItem {

	public abstract MashupMediaType getMashupMediaType();

	public final static String TITLE_SEPERATOR = " - ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_items_generator")
	@SequenceGenerator(name = "media_items_generator", sequenceName = "media_items_seq", allocationSize = 1)
	private long id;
	private String fileName;
	@ManyToOne
	private Library library;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessed;
	@ManyToOne(cascade = { CascadeType.PERSIST })
	private User lastAccessedBy;
	private String mediaTypeValue;
	@Column(length = 1000)
	private String summary;
	@Builder.Default
	private boolean enabled = true;

	@OneToMany(mappedBy = "mediaItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<MediaResource> mediaResources = new HashSet<>();

	@ToString.Exclude
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "mediaItem")
	private Set<PlaylistMediaItem> playlistMediaItems;
	@ManyToOne(cascade = { CascadeType.ALL })
	private SocialConfiguration socialConfiguration = new SocialConfiguration();

	public MediaResource getMediaResource(MediaContentType mediaContentType) {
		return getMediaResources().stream()
				.filter(mr -> mr.getMediaContentType() == mediaContentType)
				.findAny().orElse(null);
	}

	public boolean isTranscoded(MediaContentType mediaContentType) {
		Set<MediaResource> mediaResources = getMediaResources();
		if (mediaResources == null || mediaResources.isEmpty()) {
			return false;
		}

		return getMediaResources().stream()
				.anyMatch(mediaResource -> mediaResource.getMediaContentType() == mediaContentType);

	}

	public MediaResource getOriginalMediaResource() {
		Set<MediaResource> mediaResources = getMediaResources();
		if (mediaResources == null || mediaResources.isEmpty()) {
			return null;
		}

		return getMediaResources().stream()
				.filter(mediaResource -> mediaResource.isOriginal() == true)
				.findAny().orElse(null);

	}

}
