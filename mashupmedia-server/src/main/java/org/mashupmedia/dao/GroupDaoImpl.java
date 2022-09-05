package org.mashupmedia.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.mashupmedia.model.Group;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDaoImpl extends BaseDaoImpl implements GroupDao {

	@Override
	public void saveGroup(Group group) {
		saveOrMerge(group);
	}

	@Override
	public Group getGroup(long groupId) {
		Query query =  entityManager.createQuery("from Group where id = :groupId");
		query.setParameter("groupId", groupId);
		Group group = (Group) query.getSingleResult();
		return group;
	}
	
	@Override
	public List<Long> getGroupIds() {
		Query query = entityManager.createQuery("select id from Group");
		@SuppressWarnings("unchecked")
		List<Long> groupIds = (List<Long>) query.getResultList();
		return groupIds;
	}

	@Override
	public List<Group> getGroups() {
		Query query = entityManager.createQuery("from Group order by name");
		@SuppressWarnings("unchecked")
		List<Group> groups = (List<Group>) query.getResultList();
		return groups;
	}

	@Override
	public void deleteGroup(Group group) {
		deleteGroupFromLibraries(group);
		deleteGroupFromUsers(group);		
		entityManager.remove(group);
	}

	protected void deleteGroupFromUsers(Group group) {
		long groupId = group.getId();
		Query query = entityManager.createQuery("from User u inner join u.groups g where g.id = :groupId");
		query.setParameter("groupId", groupId);
		@SuppressWarnings("unchecked")
		List<User> users = query.getResultList();
		for (User user : users) {
			Set<Group> groups = user.getGroups();
			groups.remove(group);
			entityManager.merge(user);
		}		
	}

	protected void deleteGroupFromLibraries(Group group) {
		long groupId = group.getId();
		Query query = entityManager.createQuery("from Library l inner join l.groups g where g.id = :groupId");
		query.setParameter("groupId", groupId);
		@SuppressWarnings("unchecked")
		List<Library> libraries = query.getResultList();
		for (Library library : libraries) {
			Set<Group> groups = library.getGroups();
			groups.remove(group);
			entityManager.merge(library);
		}
		
	}

}
