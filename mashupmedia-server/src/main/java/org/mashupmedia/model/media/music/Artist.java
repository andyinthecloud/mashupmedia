package org.mashupmedia.model.media.music;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mashupmedia.model.account.User;
import org.mashupmedia.model.media.ExternalLink;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.social.SocialConfiguration;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "artists")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Artist{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artists_generator")
	@SequenceGenerator(name = "artists_generator", sequenceName = "artists_seq", allocationSize = 1)
	private long id;
	@Column(unique = true)
	@Size(max = 256)
	private String name;
	private Date createdOn;
	private Date updatedOn;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "artists_meta_images", joinColumns = @JoinColumn(name = "meta_image_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
	private Set<MetaImage> metaImages;
	@OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("name")
	private List<Album> albums;
	@ManyToOne
	private User user;
	@Size(max = 1024)
	private String profile;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "artists_external_links", joinColumns = @JoinColumn(name = "external_link_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
	private Set<ExternalLink> externalLinks;
	@ManyToOne(cascade = { CascadeType.ALL })
	private SocialConfiguration socialConfiguration;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Artist other = (Artist) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
