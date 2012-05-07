package org.mashupmedia.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.mashupmedia.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void saveUser(User user) {
		sessionFactory.getCurrentSession().saveOrUpdate(user);
	}

	@Override
	public User getUser(String username) {
		Query query = sessionFactory.getCurrentSession().createQuery("from User where username = :username");
		query.setString("username", username);
		query.setCacheable(true);
		User user = (User) query.uniqueResult();
		return user;
	}
	
	@Override
	public int getTotalUsers() {
		Criteria criteria =  sessionFactory.getCurrentSession().createCriteria(User.class);
		criteria.setCacheable(true);
		criteria.setProjection(Projections.rowCount());
		Number total = (Number) criteria.uniqueResult();
		return total.intValue();
	}

}
