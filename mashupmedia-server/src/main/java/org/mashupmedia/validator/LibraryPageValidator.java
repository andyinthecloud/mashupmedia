package org.mashupmedia.validator;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.web.page.LibraryPage;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class LibraryPageValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(LibraryPage.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LibraryPage libraryPage = (LibraryPage) target;
		Library library = libraryPage.getLibrary();

		if (StringUtils.isBlank(library.getName())) {
			errors.rejectValue("library.name", "library.error.name");
		}

		String scanMinutesIntervalValue = StringUtils.trimToEmpty(library.getScanMinutesInterval());
		if (StringUtils.isNotEmpty(scanMinutesIntervalValue) && !NumberUtils.isDigits(scanMinutesIntervalValue)) {
			errors.rejectValue("library.scanMinutesInterval", "error.number");
		}

		Location location = library.getLocation();
		validateFolderLocation(location, errors);

		Set<Group> groups = library.getGroups();
		if (groups == null || groups.isEmpty()) {
			errors.rejectValue("groups", "library.error.groups");

		}

	}

	private void validateFolderLocation(Location location, Errors errors) {
		if (location == null) {
			errors.rejectValue("library.location.path", "library.error.locationtype");
			return;
		}

		if (StringUtils.isBlank(location.getPath())) {
			errors.rejectValue("library.location.path", "library.error.location.folder.path");
		}

	}

}
