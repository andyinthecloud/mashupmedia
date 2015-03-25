package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.dao.PhotoDao;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.model.library.PhotoLibrary;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

@Service
@Transactional
public class PhotoLibraryUpdateManagerImpl implements PhotoLibraryUpdateManager {

	private final int PHOTOS_SAVE_AMOUNT_MAX_SIZE = 20;

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private PhotoDao photoDao;

	private ProcessManager processManager;

	@Override
	public void deleteObsoletePhotos(long libraryId, Date date) {
		List<Photo> photos = photoDao.getObsoletePhotos(libraryId, date);
		int totalDeletedPhotos = photoDao.removeObsoletePhotos(libraryId, date);

		for (Photo photo : photos) {
			processManager.killProcesses(photo.getId());
			photo.getThumbnailPath();
			FileHelper.deletePhotoThumbnail(photo.getThumbnailPath());
		}

		logger.info(totalDeletedPhotos + " obsolete photos deleted.");
	}

	@Override
	public void updateLibrary(PhotoLibrary library, File folder, Date date) {
		List<Photo> photos = new ArrayList<Photo>();
		processPhotos(photos, folder, date, null, library);
	}

	protected void processPhotos(List<Photo> photos, File file, Date date,
			String albumName, PhotoLibrary library) {

		if (file.isDirectory()) {
			if (StringUtils.isNotBlank(albumName)) {
				albumName += " / " + file.getName();
			}

			File[] files = file.listFiles();
			for (File childFile : files) {
				processPhotos(photos, childFile, date, albumName, library);
			}
		}

		if (albumName == null) {
			File parentFolder = file.getParentFile();
			albumName = parentFolder.getName();
		}

		Album album = getAlbum(albumName, date);
		album.setUpdatedOn(date);

		String fileName = file.getName();
		String path = file.getAbsolutePath();
		Photo photo = photoDao.getPhotoByAbsolutePath(path);

		boolean isCreatePhoto = false;
		if (photo == null) {
			isCreatePhoto = true;
		} else {
			if (file.length() != photo.getSizeInBytes()) {
				isCreatePhoto = true;
			}
		}

		if (isCreatePhoto) {
			photo = new Photo();
			String fileExtension = FileHelper.getFileExtension(fileName);
			MediaContentType mediaContentType = MediaItemHelper
					.getMediaContentType(fileExtension);
			if (mediaContentType == MediaContentType.UNSUPPORTED) {
				return;
			}

			Set<MediaEncoding> mediaEncodings = new HashSet<MediaEncoding>();
			MediaEncoding mediaEncoding = new MediaEncoding();
			mediaEncoding.setMediaContentType(mediaContentType);
			mediaEncoding.setOriginal(true);
			mediaEncodings.add(mediaEncoding);
			photo.setMediaEncodings(mediaEncodings);

			photo.setFormat(mediaContentType.getName());
			photo.setEnabled(true);
			photo.setFileLastModifiedOn(file.lastModified());
			photo.setFileName(fileName);
			photo.setMediaType(MediaType.PHOTO);
			photo.setPath(path);
			photo.setSizeInBytes(file.length());

			StringBuilder metadataBuilder = new StringBuilder();
			try {
				Metadata metadata = ImageMetadataReader.readMetadata(file);
				for (Directory directory : metadata.getDirectories()) {
					for (Tag tag : directory.getTags()) {
						metadataBuilder.append(tag.toString());
					}
				}

			} catch (ImageProcessingException e) {
				logger.info(
						"Unable to read image meta data for photo: "
								+ file.getAbsolutePath(), e);
			} catch (IOException e) {
				logger.info(
						"Unable to read image meta data for photo: "
								+ file.getAbsolutePath(), e);
			}
			photo.setMetadata(metadataBuilder.toString());

			try {
				String thumbnailPath = ImageHelper
						.generateAndSavePhotoThumbnail(library.getId(),
								file.getAbsolutePath());
				photo.setThumbnailPath(thumbnailPath);
			} catch (IOException e) {
				logger.error(
						"Unable to create thumbnail of photo: "
								+ file.getAbsolutePath(), e);
			}

		}

		photo.setLibrary(library);
		String title = StringUtils.trimToEmpty(fileName);
		photo.setDisplayTitle(title);
		photo.setSearchText(album.getName() + " " + title);
		photo.setUpdatedOn(date);
		photos.add(photo);

		boolean isSessionFlush = false;
		if (photos.size() == PHOTOS_SAVE_AMOUNT_MAX_SIZE) {
			isSessionFlush = true;
		}

		photoDao.savePhoto(photo, isSessionFlush);
	}

	protected Album getAlbum(String albumName, Date date) {
		albumName = StringUtils.trimToEmpty(albumName);

		Album album = null;
		if (StringUtils.isEmpty(albumName)) {
			album = createAlbum(albumName, date);
			return album;
		}

		List<Album> albums = photoDao.getAlbums(albumName);
		if (albums != null && !albums.isEmpty()) {
			return albums.get(0);
		}

		album = createAlbum(albumName, date);
		return album;
	}

	protected Album createAlbum(String albumName, Date date) {
		Album album = new Album();
		album.setCreatedOn(date);
		album.setName(albumName);
		return album;

	}
}
