package org.mashupmedia.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.dao.VideoDao;
import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.model.library.VideoLibrary.VideoDeriveTitleType;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.Video;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoLibraryUpdateManagerImpl implements VideoLibraryUpdateManager {

	private final int VIDEOS_SAVE_AMOUNT_MAX_SIZE = 20;
	private Logger logger = Logger.getLogger(getClass());

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

		processVideos(videos, folder, videoDeriveTitleType, date, null, library);

		// remove obsolete videos
		removeObsoleteVideos(library, date);

	}

	protected void removeObsoleteVideos(VideoLibrary library, Date date) {
		List<Video> videos = videoDao.getObsoleteVideos(library.getId(), date);
		int totalDeletedVideos = videoDao.removeObsoleteVideos(library.getId(), date);

		for (Video video : videos) {
			FileHelper.deleteProcessedVideo(library.getId(), video.getId());
		}

		logger.info(totalDeletedVideos + " obsolete videos deleted.");

	}

	protected void processVideos(List<Video> videos, File file, VideoDeriveTitleType videoDeriveTitleType, Date date,
			String videoDisplayTitle, VideoLibrary library) {
		if (file.isDirectory()) {
			if (StringUtils.isEmpty(videoDisplayTitle)) {
				videoDisplayTitle = file.getName();
			} else {
				videoDisplayTitle = videoDisplayTitle + "/" + file.getName();
			}

			File[] files = file.listFiles();

			for (File childFile : files) {
				processVideos(videos, childFile, videoDeriveTitleType, date, videoDisplayTitle, library);
			}
		}

		if (!FileHelper.isSupportedVideo(file.getName())) {
			return;
		}

		if (videoDeriveTitleType == VideoDeriveTitleType.USE_FOLDER_NAME) {
			videoDisplayTitle = file.getParentFile().getName();
		} else if (videoDeriveTitleType == VideoDeriveTitleType.USE_FILE_NAME) {
			videoDisplayTitle = file.getName();
		} else {
			videoDisplayTitle = videoDisplayTitle + "/" + file.getName();
		}

		String path = file.getAbsolutePath();
		Video video = videoDao.getVideoByPath(path);
		String fileName = file.getName();
		if (video == null) {
			video = new Video();
			video.setFormat(MediaContentType.UNSUPPORTED.getName());
						
			video.setEnabled(true);
//			video.setEncodeStatusType(EncodeStatusType.UNPROCESSED);

			video.setFileLastModifiedOn(file.lastModified());
			video.setFileName(fileName);
			// video.setFormat(format);
			video.setMediaType(MediaType.VIDEO);
			video.setPath(path);
			video.setSizeInBytes(file.length());
			// video.setUniqueName(uniqueName);
		}

		video.setLibrary(library);

		String searchText = videoDisplayTitle;

		int totalVideosWithSameName = videoDao.getTotalVideosWithSameName(videoDisplayTitle);
		if (totalVideosWithSameName > 0) {
			String incrementValue = String.valueOf(totalVideosWithSameName);
			videoDisplayTitle = videoDisplayTitle + "(" + incrementValue + ")";
		}
		video.setDisplayTitle(videoDisplayTitle);
		video.setSearchText(searchText);
		video.setUpdatedOn(date);
		videos.add(video);

		boolean isSessionFlush = false;
		if (videos.size() == VIDEOS_SAVE_AMOUNT_MAX_SIZE) {
			isSessionFlush = true;
		}

		videoDao.saveVideo(video, isSessionFlush);
	}

}
