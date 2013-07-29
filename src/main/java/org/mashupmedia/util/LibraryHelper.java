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

package org.mashupmedia.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.service.LibraryManager.LibraryType;

public class LibraryHelper {

	public static String createUniqueName() {
		String uniqueName = RandomStringUtils.randomAlphanumeric(14);
		return uniqueName;
	}

	public static LibraryType getRemoteLibraryType(String remoteLibraryUrl) {
		if (StringUtils.isBlank(remoteLibraryUrl)) {
			return null;
		}

		remoteLibraryUrl = remoteLibraryUrl.replaceFirst(".*/connect/", "");
		remoteLibraryUrl = remoteLibraryUrl.replaceFirst("/.*", "");

		LibraryType[] libraryTypes = LibraryType.values();
		for (LibraryType libraryType : libraryTypes) {
			if (libraryType.toString().toLowerCase().equals(remoteLibraryUrl)) {
				return libraryType;
			}
		}

		return null;

	}

	private static String getRemotePath(String remotePath, String contextPath) {
		remotePath = StringUtils.trimToEmpty(remotePath);
		if (StringUtils.isEmpty(remotePath)) {
			return null;
		}

		if (!remotePath.contains("/connect/")) {
			return null;
		}

		remotePath = remotePath.replaceFirst("/connect/.*", "/" + contextPath);
		return remotePath;
	}

	public static String getRemoteStreamingPath(String remotePath) {
		remotePath = getRemotePath(remotePath, "stream");
		return remotePath;
	}

	public static String getRemoteAlbumArtPath(String remotePath) {
		remotePath = getRemotePath(remotePath, "album-art");
		return null;
	}

}
