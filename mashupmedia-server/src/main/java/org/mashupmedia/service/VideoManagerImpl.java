package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.dao.VideoDao;
import org.mashupmedia.model.media.video.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoManagerImpl implements VideoManager {

	@Autowired
	private VideoDao videoDao;

	@Autowired
	private MashupMediaSecurityManager securityManager;

	@Override
	public List<Video> getVideos() {
		List<Long> userGroupIds = securityManager.getLoggedInUserGroupIds();
		List<Video> videos = videoDao.getVideos(userGroupIds);
		return videos;
	}

	@Override
	public Video getVideo(long videoId) {
		Video video = videoDao.getVideo(videoId);
		return video;
	}

	@Override
	public void saveVideo(Video video) {
		videoDao.saveVideo(video);
	}
}
