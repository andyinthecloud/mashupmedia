package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.account.User;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository
public class UserDaoImpl extends BaseDaoImpl implements UserDao {


	@Override
	public void saveUser(User user) {
		saveOrMerge(user);
	}

	@Override
	public User getUser(String username) {
		TypedQuery<User> query = entityManager.createQuery("from User where username = :username", User.class);
		query.setParameter("username", username);
		User user = getUniqueResult(query);
		return user;
	}

	@Override
	public User getUser(long userId) {
		TypedQuery<User> query = entityManager.createQuery("from User where id = :userId", User.class);
		query.setParameter("userId", userId);
		User user = getUniqueResult(query);
		return user;
	}

	@Override
	public int getTotalUsers() {
		TypedQuery<Long> query = entityManager.createQuery("select count(u.id) from User u", Long.class);
		Number total = getUniqueResult(query);
		return total.intValue();
	}

	@Override
	public List<User> getUsers() {
		Query query = entityManager.createQuery("from User where system = false order by name");
		@SuppressWarnings("unchecked")
		List<User> users = query.getResultList();
		return users;
	}

	@Override
	public void deleteUser(User user) {

		// Clean up user
		Long userId = user.getId();

		Query updateMediaItemQuery = entityManager.createQuery(
				"update MediaItem set lastAccessedBy = null where lastAccessedBy.id = :userId");
		updateMediaItemQuery.setParameter("userId", userId);
		updateMediaItemQuery.executeUpdate();

		Query updatePlaylistUpdatedByQuery = entityManager.createQuery(
				"update Playlist set updatedBy = null where updatedBy.id = :userId");
		updatePlaylistUpdatedByQuery.setParameter("userId", userId);
		updatePlaylistUpdatedByQuery.executeUpdate();

		entityManager.remove(user);
	}
}
