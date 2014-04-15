package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.Video;

public interface VideoManager {

	public List<Video> getVideos();

	public Video getVideo(long videoId);

	public void saveVideo(Video video);

}
