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

package org.mashupmedia.validator;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.User;
import org.mashupmedia.web.page.EditUserPage;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EditUserPageValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(EditUserPage.class);
	}

	@Override
	public void validate(Object target, Errors errors) {

		EditUserPage editUserPage = (EditUserPage) target;
		User user = editUserPage.getUser();
		if (StringUtils.isBlank(user.getName())) {
			errors.rejectValue("user.name", "configuration.administration.edit-user.error.name");
		}

		if (StringUtils.isBlank(user.getUsername())) {
			errors.rejectValue("user.username", "configuration.administration.edit-user.error.username");
		}

		validateChangePassword(editUserPage, errors);
	}

	protected void validateChangePassword(EditUserPage editUserPage, Errors errors) {
		User user = editUserPage.getUser();
		
		StringBuilder newPasswordBuilder = new StringBuilder();
		String newPassword = editUserPage.getNewPassword();
		if (newPassword == null) {
			newPassword = "";
		}
		newPasswordBuilder.append(newPassword);
		
		String newRepeatPassword = editUserPage.getNewRepeatPassword();
		if (newRepeatPassword == null) {
			newRepeatPassword = "";
		}		
		newPasswordBuilder.append(newRepeatPassword);
		
		String password = user.getPassword();
		
		if (StringUtils.isNotEmpty(password) && newPasswordBuilder.length() == 0) {
			return;
		}
		
		if (StringUtils.isEmpty(newPassword)) {
			errors.rejectValue("user.password", "configuration.administration.edit-user.error.password");
			return;
		}

		if (!newPassword.equals(newRepeatPassword)) {
			errors.rejectValue("user.password", "configuration.administration.edit-user.error.password-not-same");
			return;
		}
		
		user.setPassword(newPassword);
	}

}
