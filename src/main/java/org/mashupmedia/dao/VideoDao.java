package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.media.VideoResolution;

public interface VideoDao {

	public List<VideoResolution> getVideoResolutions();

	public void saveVideoResolution(VideoResolution videoResolution);

}
