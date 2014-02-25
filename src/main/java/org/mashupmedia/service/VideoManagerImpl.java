package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.dao.VideoDao;
import org.mashupmedia.model.media.Video;
import org.mashupmedia.model.media.VideoResolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoManagerImpl implements VideoManager {

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private SecurityManager securityManager;

	@Override
	public List<VideoResolution> getVideoResolutions() {
		List<VideoResolution> videoResolutions = videoDao.getVideoResolutions();
		if (videoResolutions == null || videoResolutions.isEmpty()) {
			initialiseVideoResolutions();
		}
		videoResolutions = videoDao.getVideoResolutions();
		return videoResolutions;
	}

	@Override
	public void saveVideoResolution(VideoResolution videoResolution) {
		videoDao.saveVideoResolution(videoResolution);
	}

	@Override
	public void initialiseVideoResolutions() {
		VideoResolution videoResolution480p = new VideoResolution();
		videoResolution480p.setName("480p");
		videoResolution480p.setWidth(720);
		videoResolution480p.setHeight(420);
		saveVideoResolution(videoResolution480p);

		VideoResolution videoResolution720p = new VideoResolution();
		videoResolution720p.setName("720p");
		videoResolution720p.setWidth(1280);
		videoResolution720p.setHeight(720);
		saveVideoResolution(videoResolution720p);

		VideoResolution videoResolution1080p = new VideoResolution();
		videoResolution1080p.setName("1080p");
		videoResolution1080p.setWidth(1920);
		videoResolution1080p.setHeight(1080);
		saveVideoResolution(videoResolution1080p);
	}

	@Override
	public VideoResolution getVideoResolution(long videoResolutionId) {
		VideoResolution videoResolution = videoDao.getVideoResolution(videoResolutionId);
		return videoResolution;
	}
	
	@Override
	public List<Video> getVideos() {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Video> videos = videoDao.getVideos(userGroupIds);
		return videos;
	}

}
