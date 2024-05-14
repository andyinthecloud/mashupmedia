package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.account.User;

public interface UserDao {

	public void saveUser(User user);

	public User getUser(String username);

	public int getTotalUsers();

	public User getUser(long userId);

	public List<User> getUsers();

	public void deleteUser(User user);

}
