package org.mashupmedia.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dao.VideoDao;
import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.model.library.VideoLibrary.VideoDeriveTitleType;
import org.mashupmedia.model.media.MediaItem.EncodeStatusType;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoLibraryUpdateManagerImpl implements VideoLibraryUpdateManager {

	private final int VIDEOS_SAVE_AMOUNT_MAX_SIZE = 20;

	@Autowired
	private VideoDao videoDao;

	@Override
	public void updateLibrary(VideoLibrary library, File folder, Date date) {
		List<Video> videos = new ArrayList<Video>();

		VideoDeriveTitleType videoDeriveTitleType = VideoDeriveTitleType.USE_FOLDER_NAME;
		String deriveTitle = StringUtils.trimToEmpty(library.getVideoDeriveTitle());

		if (VideoDeriveTitleType.USE_FILE_NAME.name().equalsIgnoreCase(deriveTitle)) {
			videoDeriveTitleType = VideoDeriveTitleType.USE_FILE_NAME;
		}

		processVideos(videos, folder, videoDeriveTitleType, date, null);
	}

	protected void processVideos(List<Video> videos, File file, VideoDeriveTitleType videoDeriveTitleType, Date date,
			String videoName) {
		if (file.isDirectory()) {
			if (StringUtils.isEmpty(videoName)) {
				videoName = file.getName();
			} else {
				videoName = videoName + "/" + file.getName();
			}

			File[] files = file.listFiles();

			for (File childFile : files) {
				processVideos(videos, childFile, videoDeriveTitleType, date, videoName);
			}
		}

		if (videoDeriveTitleType == VideoDeriveTitleType.USE_FOLDER_NAME) {
			videoName = file.getParentFile().getName();
		} else if (videoDeriveTitleType == VideoDeriveTitleType.USE_FILE_NAME) {
			videoName = file.getName();
		} else {
			videoName = videoName + "/" + file.getName();
		}

		Video video = new Video();
		video.setDisplayTitle(videoName);
		video.setEnabled(true);
		video.setEncodeStatusType(EncodeStatusType.UNPROCESSED);

		video.setFileLastModifiedOn(file.lastModified());
		video.setFileName(file.getName());
		// video.setFormat(format);
		video.setMediaType(MediaType.VIDEO);
		video.setPath(file.getAbsolutePath());
		video.setSearchText(videoName);
		video.setSizeInBytes(file.length());
		// video.setUniqueName(uniqueName);
		video.setUpdatedOn(date);
		videos.add(video);

		boolean isSessionFlush = false;
		if (videos.size() == VIDEOS_SAVE_AMOUNT_MAX_SIZE) {
			isSessionFlush = true;
		}

		videoDao.saveVideo(video, isSessionFlush);
	}

}
