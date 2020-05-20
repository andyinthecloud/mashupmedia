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

import org.mashupmedia.model.User;

public class EditUserPage {

	private String action;
	private User user;
	private String newPassword;
	private String newRepeatPassword;
	private boolean administrator;
	private boolean showAdministrator;

	public boolean isShowAdministrator() {
		return showAdministrator;
	}

	public void setShowAdministrator(boolean showAdministrator) {
		this.showAdministrator = showAdministrator;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public boolean getIsAdministrator() {
		return isAdministrator();
	}

	public void setAdministrator(boolean isAdministrator) {
		this.administrator = isAdministrator;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewRepeatPassword() {
		return newRepeatPassword;
	}

	public void setNewRepeatPassword(String newRepeatPassword) {
		this.newRepeatPassword = newRepeatPassword;
	}

}
