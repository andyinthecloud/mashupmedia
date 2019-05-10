package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.mashupmedia.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends BaseDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void saveUser(User user) {
		long userId = user.getId();
		if (userId == 0) {
			sessionFactory.getCurrentSession().save(user);
		} else {
			sessionFactory.getCurrentSession().merge(user);
		}
	}

	@Override
	public User getUser(String username) {
		Query<User> query = sessionFactory.getCurrentSession().createQuery("from User where username = :username");
		query.setParameter("username", username, StringType.INSTANCE); 
		query.setCacheable(true);
		User user = query.uniqueResult();
		return user;
	}

	@Override
	public User getUser(long userId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from User where id = :userId");
		query.setLong("userId", userId);
		query.setCacheable(true);
		User user = (User) query.uniqueResult();
		return user;
	}

	@Override
	public int getTotalUsers() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
		criteria.setCacheable(true);
		criteria.setProjection(Projections.rowCount());
		Number total = (Number) criteria.uniqueResult();
		return total.intValue();
	}

	@Override
	public List<User> getUsers() {
		Query query = sessionFactory.getCurrentSession().createQuery("from User where system = false order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<User> users = query.list();
		return users;
	}

	@Override
	public void deleteUser(User user) {

		// Clean up user
		Long userId = user.getId();

		Query updateMediaItemQuery = sessionFactory.getCurrentSession().createQuery(
				"update MediaItem set lastAccessedBy = null where lastAccessedBy.id = :userId");
		updateMediaItemQuery.setLong("userId", userId);
		updateMediaItemQuery.executeUpdate();

		Query updatePlaylistUpdatedByQuery = sessionFactory.getCurrentSession().createQuery(
				"update Playlist set updatedBy = null where updatedBy.id = :userId");
		updatePlaylistUpdatedByQuery.setLong("userId", userId);
		updatePlaylistUpdatedByQuery.executeUpdate();

		sessionFactory.getCurrentSession().delete(user);
	}
}
