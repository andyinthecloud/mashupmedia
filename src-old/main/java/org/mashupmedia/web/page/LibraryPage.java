package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;

public class LibraryPage {

	private String action;
	private List<Group> groups;
	private boolean exists;
	private Library library;

	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
	}

	public boolean isExists() {
		return exists;
	}
	
	public boolean getIsShowRemoteConfiguration() {
		// return isExists();
		// Disable this feature for now
		return false;
	}
	

	public boolean getIsExists() {
		return isExists();
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

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

}
