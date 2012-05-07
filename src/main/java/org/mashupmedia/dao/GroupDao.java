package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.Group;

public interface GroupDao {

	public void saveGroup(Group group);

	public Group getGroup(String idName);

	public List<Group> getGroups();

}
