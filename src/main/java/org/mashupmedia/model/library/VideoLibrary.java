package org.mashupmedia.model.library;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable
public class VideoLibrary extends Library {

	private static final long serialVersionUID = -7784201711543047031L;

	public enum VideoDeriveTitleType {
		USE_FOLDER_NAME, USE_FILE_NAME, USE_FOLDER_AND_FILE_NAME
	}

	private String videoDeriveTitle;

	@Override
	public LibraryType getLibraryType() {
		return LibraryType.VIDEO;
	}

	public String getVideoDeriveTitle() {
		return videoDeriveTitle;
	}

	public void setVideoDeriveTitle(String videoDeriveTitle) {
		this.videoDeriveTitle = videoDeriveTitle;
	}

}
