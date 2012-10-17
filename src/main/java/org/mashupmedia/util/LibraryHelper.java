package org.mashupmedia.util;

import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.ConnectionManager.LocationType;

public class LibraryHelper {

	public static LocationType getLocationType(Location location) {
		if (location instanceof FtpLocation) {
			return LocationType.FTP;
		}

		return LocationType.LOCAL;
	}
}
