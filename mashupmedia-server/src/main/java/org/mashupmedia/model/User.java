package org.mashupmedia.model;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;

import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Cacheable
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
	
	public static String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
	
	private static final long serialVersionUID = 8897344406027907607L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String username;
	private String password;
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
	private long playlistMediaItemId;
	@Transient
	private PlaylistMediaItem playlistMediaItem;

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public long getPlaylistMediaItemId() {
		return playlistMediaItemId;
	}

	public void setPlaylistMediaItemId(long playlistMediaItemId) {
		this.playlistMediaItemId = playlistMediaItemId;
	}

	public PlaylistMediaItem getPlaylistMediaItem() {
		return playlistMediaItem;
	}

	public void setPlaylistMediaItem(PlaylistMediaItem playlistMediaItem) {
		this.playlistMediaItem = playlistMediaItem;
	}

	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setUpdatedOn(Date lastModifiedOn) {
		this.updatedOn = lastModifiedOn;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

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
		builder.append(", playlistMediaItemId=");
		builder.append(playlistMediaItemId);
		builder.append(", playlistMediaItem=");
		builder.append(playlistMediaItem);
		builder.append("]");
		return builder.toString();
	}

}
