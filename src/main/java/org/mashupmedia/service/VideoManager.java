package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.VideoResolution;

public interface VideoManager {
	
	public List<VideoResolution> getVideoResolutions();

	public void saveVideoResolution(VideoResolution videoResolution);

	public void initialiseVideoResolutions();

	public VideoResolution getVideoResolution(long videoResolutionId);
	

}
