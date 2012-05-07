package org.mashupmedia.web.page;

import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;

public abstract class LibraryPage {

	private String action;
	private String locationType;
	private Location folderLocation;
	private FtpLocation ftpLocation;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Location getFolderLocation() {
		return folderLocation;
	}

	public void setFolderLocation(Location location) {
		this.folderLocation = location;
	}

	public FtpLocation getFtpLocation() {
		return ftpLocation;
	}

	public void setFtpLocation(FtpLocation ftpLocation) {
		this.ftpLocation = ftpLocation;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

}