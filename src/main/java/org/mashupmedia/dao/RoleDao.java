package org.mashupmedia.dao;

import org.mashupmedia.model.Role;

public interface RoleDao {
	public void saveRole(Role role);
	public Role getRole(String idName);	

}
