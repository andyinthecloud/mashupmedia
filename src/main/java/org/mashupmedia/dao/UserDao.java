package org.mashupmedia.dao;

import org.mashupmedia.model.User;

public interface UserDao {

	public void saveUser(User user);

	public User getUser(String username);

	public int getTotalUsers();

	public User getUser(long userId);

}
