package org.mashupmedia.model.location;

import java.io.Serializable;
import java.util.List;

import org.mashupmedia.model.library.Library;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@NoArgsConstructor
@Getter
@Setter
public class Location implements Serializable {
	private static final long serialVersionUID = -6003017428642508314L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String path;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "location")
	private List<Library> libraries;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Location other = (Location) obj;
		if (id != other.id)
			return false;
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
		builder.append("Location [id=");
		builder.append(id);
		builder.append(", path=");
		builder.append(path);
		builder.append("]");
		return builder.toString();
	}

}
