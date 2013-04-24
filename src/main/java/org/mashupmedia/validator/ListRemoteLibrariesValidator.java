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

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.web.page.EditRemoteLibraryPage;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ListRemoteLibrariesValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(EditRemoteLibraryPage.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		EditRemoteLibraryPage listRemoteLibrariesPage = (EditRemoteLibraryPage) target;
		Library remoteLibrary = listRemoteLibrariesPage.getRemoteLibrary();
		String name = remoteLibrary.getName();
		if (StringUtils.isBlank(name)) {
			errors.rejectValue("remoteLibrary.name", "configuration.edit-remote-library.errors.empty.name");
		}

		Location location = remoteLibrary.getLocation();
		String path = location.getPath();
		if (StringUtils.isBlank(path)) {
			errors.rejectValue("remoteLibrary.location.path", "configuration.edit-remote-library.errors.empty.url");
		}
		
		Collection<Group> groups = remoteLibrary.getGroups();
		if (groups == null || groups.isEmpty()) {
			errors.rejectValue("remoteLibrary.groups", "configuration.edit-remote-library.errors.empty.groups");			
		}
		
	}

}
