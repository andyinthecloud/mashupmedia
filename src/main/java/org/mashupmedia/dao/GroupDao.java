package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.Group;

public interface GroupDao {

	public void saveGroup(Group group);

	public Group getGroup(long groupId);

	public List<Group> getGroups();

	public void deleteGroup(Group group);

}
