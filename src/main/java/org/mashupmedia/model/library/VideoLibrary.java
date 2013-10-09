package org.mashupmedia.model.library;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable
public class VideoLibrary extends Library {

	private static final long serialVersionUID = -7784201711543047031L;

	public enum VideoScanMethodType {
		USE_FOLDER_NAMES, USE_FILE_NAMES
	}

	private String videoScanMethod;

	public String getVideoScanMethod() {
		return videoScanMethod;
	}

	public void setVideoScanMethod(String videoScanMethod) {
		this.videoScanMethod = videoScanMethod;
	}

}
