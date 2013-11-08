package org.mashupmedia.model.library;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.mashupmedia.model.media.VideoResolution;

@Entity
@Cacheable
public class VideoLibrary extends Library {

	private static final long serialVersionUID = -7784201711543047031L;

	public enum VideoDeriveTitleType {
		USE_FOLDER_NAME, USE_FILE_NAME
	}

	private String videoDeriveTitle;

	@ManyToMany(cascade = CascadeType.PERSIST)
	private Set<VideoResolution> videoResolutions;

	public Set<VideoResolution> getVideoResolutions() {
		return videoResolutions;
	}

	public void setVideoResolutions(Set<VideoResolution> videoResolutions) {
		this.videoResolutions = videoResolutions;
	}

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
