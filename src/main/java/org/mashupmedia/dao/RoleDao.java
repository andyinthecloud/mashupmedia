package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.Role;

public interface RoleDao {
	public void saveRole(Role role);

	public Role getRole(String idName);

	public List<Role> getRoles();

}
