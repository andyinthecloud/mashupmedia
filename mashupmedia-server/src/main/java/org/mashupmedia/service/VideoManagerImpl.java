package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.dao.VideoDao;
import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.util.AdminHelper;
import org.springframework.beans.factory.annotation.Autowired;
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
		Long userId = AdminHelper.getLoggedInUser().getId();
		List<Video> videos = videoDao.getVideos(userId);
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
