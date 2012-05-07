package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.Group;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDaoImpl extends BaseDaoImpl implements GroupDao {

	@Override
	public void saveGroup(Group group) {
		sessionFactory.getCurrentSession().saveOrUpdate(group);
	}

	@Override
	public Group getGroup(String idName) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Group where idName = :idName");
		query.setString("idName", idName);
		query.setCacheable(true);
		Group group = (Group) query.uniqueResult();
		return group;
	}

	@Override
	public List<Group> getGroups() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Group order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Group> groups = (List<Group>) query.list();
		return groups;
	}

}
