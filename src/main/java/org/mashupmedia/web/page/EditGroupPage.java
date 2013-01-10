/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.web.page;

import java.util.List;

import org.mashupmedia.model.Group;
import org.mashupmedia.web.WebOption;

public class EditGroupPage {
	private String action;
	private Group group;
	private List<WebOption> selectedLibraries;

	public List<WebOption> getSelectedLibraries() {
		return selectedLibraries;
	}

	public void setSelectedLibraries(List<WebOption> selectedLibraries) {
		this.selectedLibraries = selectedLibraries;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}
