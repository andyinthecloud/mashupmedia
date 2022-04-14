package org.mashupmedia.model;

import java.util.Date;
import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authorities")
@Cacheable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends Translation implements GrantedAuthority {
	private static final long serialVersionUID = -8680736152957783557L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String idName;
	private String name;
	private String authority;
	private Date createdOn;

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idName == null) ? 0 : idName.hashCode());
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
		Role other = (Role) obj;
		if (idName == null) {
			if (other.idName != null)
				return false;
		} else if (!idName.equals(other.idName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Role [id=");
		builder.append(id);
		builder.append(", idName=");
		builder.append(idName);
		builder.append(", name=");
		builder.append(name);
		builder.append(", authority=");
		builder.append(authority);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
