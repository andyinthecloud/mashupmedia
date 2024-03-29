package org.mashupmedia.model;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.mashupmedia.model.playlist.UserPlaylistPosition;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Cacheable
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor 
@Getter
@Setter
public class User implements UserDetails {
	
	public static String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
	
	private static final long serialVersionUID = 8897344406027907607L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String username;	
	private String password;
	@ToString.Include
	private String name;
	private boolean enabled;
	private boolean editable;
	private boolean system;
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Set<Role> roles;
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Set<Group> groups;
	private Date createdOn;
	private Date updatedOn;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserPlaylistPosition> userPlaylistPositions;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles();
	}

	@Override
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}
	
	public boolean isAdministrator() {
		Collection<Role> roles =  getRoles();
		for (Role role : roles) {
			if (role.getAuthority().equalsIgnoreCase(User.ROLE_ADMINISTRATOR)) {
				return true;
			}
		}
		
		return false;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id=");
		builder.append(id);
		builder.append(", username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", name=");
		builder.append(name);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", editable=");
		builder.append(editable);
		builder.append(", system=");
		builder.append(system);
		builder.append(", roles=");
		builder.append(roles);
		builder.append(", groups=");
		builder.append(groups);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append("]");
		return builder.toString();
	}

}
