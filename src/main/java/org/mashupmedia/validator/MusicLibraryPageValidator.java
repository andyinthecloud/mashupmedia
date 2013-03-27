package org.mashupmedia.validator;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.web.page.MusicLibraryPage;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class MusicLibraryPageValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(MusicLibraryPage.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MusicLibraryPage musicLibraryPage = (MusicLibraryPage) target;
		MusicLibrary musicLibrary = musicLibraryPage.getMusicLibrary();

		if (StringUtils.isBlank(musicLibrary.getName())) {
			errors.rejectValue("musicLibrary.name", "musiclibrary.error.name");
		}

		String scanMinutesIntervalValue = StringUtils.trimToEmpty(musicLibrary.getScanMinutesInterval());
		if (StringUtils.isNotEmpty(scanMinutesIntervalValue) && !NumberUtils.isDigits(scanMinutesIntervalValue)) {
			errors.rejectValue("musicLibrary.scanMinutesInterval", "error.number");
		}

		Location location = musicLibrary.getLocation();
		validateFolderLocation(location, errors);

		Set<Group> groups = musicLibrary.getGroups();
		if (groups == null || groups.isEmpty()) {
			errors.rejectValue("groups", "musiclibrary.error.groups");

		}

	}

	private void validateFolderLocation(Location location, Errors errors) {
		if (location == null) {
			errors.rejectValue("musicLibrary.location.path", "musiclibrary.error.locationtype");
			return;
		}

		if (StringUtils.isBlank(location.getPath())) {
			errors.rejectValue("musicLibrary.location.path", "musiclibrary.error.location.folder.path");
		}

	}

}
