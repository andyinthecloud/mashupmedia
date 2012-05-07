package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.Group;
import org.mashupmedia.model.Role;
import org.mashupmedia.model.User;

public interface AdminManager {

	public User getUser(String username);

	public void saveUser(User user);

	public int getTotalUsers();

	public void saveRole(Role role);

	public void updatePassword(String defaultUsername, String defaultPassword);

	public void saveGroup(Group group);

	public List<Group> getGroups();

	public Group getGroup(String idName);
	

}
