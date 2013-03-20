package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.Group;
import org.mashupmedia.model.location.Location;

public abstract class LibraryPage {

	private String action;
	private Location folderLocation;
	private List<Group> groups;

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Location getFolderLocation() {
		return folderLocation;
	}

	public void setFolderLocation(Location location) {
		this.folderLocation = location;
	}

}
