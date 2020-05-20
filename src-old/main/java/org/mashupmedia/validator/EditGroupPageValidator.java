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
import org.mashupmedia.model.Group;
import org.mashupmedia.web.page.EditGroupPage;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EditGroupPageValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(EditGroupPage.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		EditGroupPage editUserPage = (EditGroupPage) target;
		Group group = editUserPage.getGroup();
		if (StringUtils.isBlank(group.getName())) {
			errors.rejectValue("group.name", "configuration.administration.edit-group.error.name");
		}
		
	}

}
