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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.springframework.util.Assert;

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

		String uniqueName = remotePath.replaceFirst(".*/", "");

		StringBuilder remoteUrlBuilder = new StringBuilder(remotePath.replaceFirst("/connect/.*", ""));
		remoteUrlBuilder.append("/" + contextPath);
		remoteUrlBuilder.append("/" + uniqueName);
		return remoteUrlBuilder.toString();
	}

	public static String getRemoteStreamingPath(String remotePath) {
		remotePath = getRemotePath(remotePath, "stream");
		return remotePath;
	}

	public static String getRemoteAlbumArtPath(String remotePath) {
		remotePath = getRemotePath(remotePath, "album-art");
		return remotePath;
	}

	public static String getConfigurationLastUpdatedKey(long id) {
		return "lastUpdated_" + Library.class.getName() + "_" + id;
	}

	
	public static List<File> getRelativeFolders(File libraryFolder, File file) {
		
		List<File> folders = new ArrayList<>();
		if (libraryFolder == null || file == null) {
			return folders;
		}		
		
		File parentFolder = file.getParentFile();
		
		while(parentFolder != null && !libraryFolder.equals(parentFolder)) {
			folders.add(parentFolder);
			parentFolder = parentFolder.getParentFile();
		}
		
		Collections.reverse(folders);		
		return folders;
	}

	public static String getLibraryFolderName(String userLibraryFolderName, String libraryName) {
		Assert.hasText(libraryName, "Expecting text for name");
		String processedLibraryName = libraryName.replaceAll("\\W", "").toLowerCase();
		File libraryFolder = new File(FileHelper.getUserUploadPath(userLibraryFolderName).toFile(), processedLibraryName);
		return libraryFolder.getAbsolutePath();
	}


}
