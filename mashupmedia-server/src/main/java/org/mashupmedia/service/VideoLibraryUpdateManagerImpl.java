package org.mashupmedia.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dao.VideoDao;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.VideoLibrary;
import org.mashupmedia.model.library.VideoLibrary.VideoDeriveTitleType;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.repository.media.music.LIbraryRepository;
import org.mashupmedia.task.EncodeMediaItemManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaContentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@Service
@Lazy(true)
@Transactional
public class VideoLibraryUpdateManagerImpl implements VideoLibraryUpdateManager {

	private final int VIDEOS_SAVE_AMOUNT_MAX_SIZE = 20;

	@Autowired
	private VideoDao videoDao;

	@Autowired
	private LIbraryRepository libraryRepository;

	@Autowired
	private EncodeMediaItemManager encodeMediaItemManager;

	@Override
	public void updateLibrary(VideoLibrary library, File folder, Date date) {
		List<Video> videos = new ArrayList<Video>();

		VideoDeriveTitleType videoDeriveTitleType = VideoDeriveTitleType.USE_FOLDER_NAME;
		String deriveTitle = StringUtils.trimToEmpty(library.getVideoDeriveTitle());

		if (VideoDeriveTitleType.USE_FILE_NAME.name().equalsIgnoreCase(deriveTitle)) {
			videoDeriveTitleType = VideoDeriveTitleType.USE_FILE_NAME;
		} else if (VideoDeriveTitleType.USE_FOLDER_NAME.name().equalsIgnoreCase(deriveTitle)) {
			videoDeriveTitleType = VideoDeriveTitleType.USE_FOLDER_NAME;
		} else {
			videoDeriveTitleType = VideoDeriveTitleType.USE_FOLDER_AND_FILE_NAME;
		}

		processVideos(videos, folder, videoDeriveTitleType, date, null, library);
	}

	@Override
	public void deleteObsoleteVideos(long libraryId, Date date) {
		List<Video> videos = videoDao.getObsoleteVideos(libraryId, date);
		int totalDeletedVideos = videoDao.removeObsoleteVideos(libraryId, date);
		Library library = libraryRepository.getReferenceById(libraryId);
		User user = library.getUser();

		for (Video video : videos) {
			// processManager.killProcesses(video.getId());
			FileHelper.deleteProcessedVideo(user.getFolderName(), libraryId, video.getId());
		}

		log.info(totalDeletedVideos + " obsolete videos deleted.");
	}

	protected void processVideos(List<Video> videos, File file, VideoDeriveTitleType videoDeriveTitleType, Date date,
			String videoDisplayTitle, VideoLibrary library) {
		if (file.isDirectory()) {
			if (StringUtils.isEmpty(videoDisplayTitle)) {
				videoDisplayTitle = StringUtils.trimToEmpty(file.getName());
			} else {
				videoDisplayTitle = videoDisplayTitle + "/" + file.getName();
			}

			File[] files = file.listFiles();

			for (File childFile : files) {
				processVideos(videos, childFile, videoDeriveTitleType, date, videoDisplayTitle, library);
			}
		}

		String fileName = StringUtils.trimToEmpty(file.getName());
		if (!FileHelper.isSupportedVideo(fileName)) {
			return;
		}

		if (videoDeriveTitleType == VideoDeriveTitleType.USE_FOLDER_NAME) {
			videoDisplayTitle = StringUtils.trimToEmpty(file.getParentFile().getName());
		} else if (videoDeriveTitleType == VideoDeriveTitleType.USE_FILE_NAME) {
			videoDisplayTitle = fileName;
		} else {
			if (StringUtils.isNotBlank(videoDisplayTitle)) {
				videoDisplayTitle = videoDisplayTitle + " / " + fileName;
			} else {
				videoDisplayTitle = fileName;
			}
		}

		String path = file.getAbsolutePath();
		Video video = videoDao.getVideoByPath(path);

		boolean isCreateVideo = false;
		if (video == null) {
			isCreateVideo = true;
		} else {
			if (file.length() != video.getSizeInBytes()) {
				isCreateVideo = true;
			}
		}

		int totalVideosWithSameNameThreshold = 0;
		if (isCreateVideo) {
			video = new Video();
			video.setCreatedOn(date);
			String fileExtension = FileHelper.getFileExtension(fileName);
			MediaContentType mediaContentType = MediaContentHelper.getMediaContentType(fileExtension);
			Set<MediaEncoding> mediaEncodings = new HashSet<MediaEncoding>();
			MediaEncoding mediaEncoding = new MediaEncoding();
			mediaEncoding.setMediaContentType(mediaContentType);
			mediaEncoding.setOriginal(true);
			mediaEncodings.add(mediaEncoding);
			video.setMediaEncodings(mediaEncodings);

			video.setFormat(mediaContentType.name());
			video.setEnabled(true);
			video.setFileLastModifiedOn(file.lastModified());
			video.setFileName(fileName);
			video.setMashupMediaType(MashupMediaType.VIDEO);
			video.setPath(path);
			video.setSizeInBytes(file.length());

		} else {
			totalVideosWithSameNameThreshold = 1;
		}

		int totalVideosWithSameName = videoDao.getTotalVideosWithSameName(videoDisplayTitle);
		if (totalVideosWithSameName > totalVideosWithSameNameThreshold) {
			String incrementValue = String.valueOf(totalVideosWithSameName);
			videoDisplayTitle = videoDisplayTitle + "(" + incrementValue + ")";
		}
		video.setLibrary(library);
		String searchText = videoDisplayTitle;
		video.setDisplayTitle(videoDisplayTitle);
		video.setUpdatedOn(date);
		videos.add(video);

		boolean isSessionFlush = false;
		if (videos.size() == VIDEOS_SAVE_AMOUNT_MAX_SIZE) {
			isSessionFlush = true;
		}

		videoDao.saveVideo(video, isSessionFlush);

		if (!library.isEncodeVideoOnDemand()) {
			encodeMediaItemManager.processMediaItemForEncodingDuringAutomaticUpdate(video, MediaContentType.VIDEO_MP4);
		}
	}

}
