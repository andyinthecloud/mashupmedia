package org.mashupmedia.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mashupmedia.model.media.Video;
import org.mashupmedia.model.media.VideoResolution;

public interface VideoDao {

	public List<VideoResolution> getVideoResolutions();

	public void saveVideoResolution(VideoResolution videoResolution);

	public VideoResolution getVideoResolution(long videoResolutionId);
	
	public void saveVideo(Video video, boolean isSessionFlush);
	
	public void saveVideo(Video video);

	public List<Video> getVideos(Collection<Long> groupIds);

	public int removeObsoleteVideos(long librayId, Date date);

	public List<Video> getObsoleteVideos(long librayId, Date date);

	public Video getVideoByPath(String path);
	
	
}
