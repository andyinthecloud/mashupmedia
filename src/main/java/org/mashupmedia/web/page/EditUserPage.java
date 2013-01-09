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

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.User;

public class EditUserPage {

	private String action;
	private User user;
	private String repeatPassword;
	private boolean administrator;

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean isAdministrator) {
		this.administrator = isAdministrator;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
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

	public boolean getHasPassword() {
		if (user == null) {
			return false;
		}

		String password = user.getPassword();
		if (StringUtils.isBlank(password)) {
			return false;
		}

		return true;

	}

}
