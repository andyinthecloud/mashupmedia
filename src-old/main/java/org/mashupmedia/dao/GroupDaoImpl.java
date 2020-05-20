package org.mashupmedia.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDaoImpl extends BaseDaoImpl implements GroupDao {

	@Override
	public void saveGroup(Group group) {
		sessionFactory.getCurrentSession().saveOrUpdate(group);
	}

	@Override
	public Group getGroup(long groupId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Group where id = :groupId");
		query.setLong("groupId", groupId);
		query.setCacheable(true);
		Group group = (Group) query.uniqueResult();
		return group;
	}
	
	@Override
	public List<Long> getGroupIds() {
		Query query = sessionFactory.getCurrentSession().createQuery("select id from Group");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Long> groupIds = (List<Long>) query.list();
		return groupIds;
	}

	@Override
	public List<Group> getGroups() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Group order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Group> groups = (List<Group>) query.list();
		return groups;
	}

	@Override
	public void deleteGroup(Group group) {
		deleteGroupFromLibraries(group);
		deleteGroupFromUsers(group);
		sessionFactory.getCurrentSession().delete(group);
	}

	protected void deleteGroupFromUsers(Group group) {
		long groupId = group.getId();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from User u inner join u.groups g where g.id = :groupId");
		query.setLong("groupId", groupId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<User> users = query.list();
		for (User user : users) {
			Set<Group> groups = user.getGroups();
			groups.remove(group);
			session.merge(user);
		}		
	}

	protected void deleteGroupFromLibraries(Group group) {
		long groupId = group.getId();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Library l inner join l.groups g where g.id = :groupId");
		query.setLong("groupId", groupId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = query.list();
		for (Library library : libraries) {
			Set<Group> groups = library.getGroups();
			groups.remove(group);
			session.merge(library);
		}
		
	}

}
