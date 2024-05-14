package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.account.Role;
import org.mashupmedia.model.account.User;

public interface AdminManager {

	public User getUser(String username);

	public User getUser(long userId);

	public void saveUser(User user);

	public int getTotalUsers();

	public void saveRole(Role role);

	public void updatePassword(String defaultUsername, String defaultPassword);

	public List<User> getUsers();

	public List<Role> getRoles();

	public Role getRole(String idName);

	public void deleteUser(long userId);
	
	public void updateUser(User user);
	public void initialiseAdminUser();

	public void initialiseSystemUser();

	public User getSystemUser();

}
