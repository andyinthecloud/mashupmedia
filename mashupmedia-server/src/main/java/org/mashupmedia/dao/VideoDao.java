package org.mashupmedia.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mashupmedia.model.media.video.Video;

public interface VideoDao {

	public void saveVideo(Video video, boolean isSessionFlush);

	public void saveVideo(Video video);

	public List<Video> getVideos(Collection<Long> groupIds);

	public int removeObsoleteVideos(long librayId, Date date);

	public List<Video> getObsoleteVideos(long librayId, Date date);

	public Video getVideoByPath(String path);

	public int getTotalVideosWithSameName(String videoDisplayTitle);

	public Video getVideo(long videoId);

}
