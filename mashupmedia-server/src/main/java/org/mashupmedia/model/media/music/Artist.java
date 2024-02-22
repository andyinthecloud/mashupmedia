package org.mashupmedia.model.media.music;

import java.io.Serializable;
import java.util.List;

import org.mashupmedia.model.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
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
@ToString
public class Artist implements Serializable {
	private static final long serialVersionUID = -5361832134097788033L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(unique = true)
	private String name;
	@ManyToOne(cascade = { CascadeType.ALL })
	private MusicArtImage albumArtImage;
	@OneToMany(mappedBy = "artist")
	@OrderBy("name")
	private List<Album> albums;
	@ManyToOne
	private User user;
	private String summary;
	private String link;

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
