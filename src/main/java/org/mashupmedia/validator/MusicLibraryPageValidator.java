package org.mashupmedia.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
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
		
		String locationType = StringUtils.trimToEmpty(musicLibraryPage.getLocationType());
		if (StringUtils.isEmpty(locationType)) {
			errors.rejectValue("locationType", "musiclibrary.error.locationtype");
		}

		if (locationType.equalsIgnoreCase("ftp")) {
			validateFtpLocation(musicLibraryPage.getFtpLocation(), errors);
		} else {
			validateFolderLocation(musicLibraryPage.getFolderLocation(), errors);
		}

	}

	private void validateFolderLocation(Location location, Errors errors) {
		if (location == null) {
			errors.rejectValue("folderLocation", "musiclibrary.error.locationtype");
			return;
		}

		if (StringUtils.isBlank(location.getPath())) {
			errors.rejectValue("folderLocation.path", "musiclibrary.error.location.folder.path");
		}

	}

	private void validateFtpLocation(FtpLocation ftpLocation, Errors errors) {
		if (ftpLocation == null) {
			errors.rejectValue("ftpLocation", "musiclibrary.error.locationtype");
			return;
		}
		
		if (StringUtils.isBlank(ftpLocation.getHost())) {
			errors.rejectValue("ftpLocation.host", "musiclibrary.error.location.ftp.host");
		}

	}

}
