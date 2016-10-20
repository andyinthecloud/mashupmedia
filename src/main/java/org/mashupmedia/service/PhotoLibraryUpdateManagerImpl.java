package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import org.mashupmedia.util.ImageHelper.ImageRotationType;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;

@Service
@Transactional
public class PhotoLibraryUpdateManagerImpl implements PhotoLibraryUpdateManager {

	private final int PHOTOS_SAVE_AMOUNT_MAX_SIZE = 10;
	private final String NEW_LINE = "\n";

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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateLibrary(PhotoLibrary library, File folder, Date date) {
		Long totalPhotosSaved = Long.valueOf(0);
		processPhotos(totalPhotosSaved, folder, date, null, library);
		logger.info("Total photos saved:" + totalPhotosSaved);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	protected void processPhotos(Long totalPhotosSaved, File file, Date date, String albumName, PhotoLibrary library) {

		if (file.isDirectory()) {
			if (StringUtils.isNotBlank(albumName)) {
				albumName += " / " + file.getName();
			} else {
				albumName = file.getName();
			}

			File[] files = file.listFiles();
			if (files == null) {
				return;
			}

			Arrays.sort(files);
			for (File childFile : files) {
				processPhotos(totalPhotosSaved, childFile, date, albumName, library);
			}
		}

		Album album = getAlbum(albumName, date);
		album.setUpdatedOn(date);

		String fileName = file.getName();
		String path = file.getAbsolutePath();
		Photo photo = getPhotoByAbsolutePath(path);

		if (isCreatePhoto(file, photo)) {
			photo = new Photo();
			photo.setCreatedOn(date);
			photo.setAlbum(album);
			String fileExtension = FileHelper.getFileExtension(fileName);
			MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(fileExtension);

			if (!MediaItemHelper.isCompatiblePhotoFormat(mediaContentType)) {
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

			ImageRotationType imageRotationType = null;
			try {
				Metadata metadata = ImageMetadataReader.readMetadata(file);
				String metadataText = processPhotoMetadata(metadata);
				photo.setMetadata(metadataText);
				imageRotationType = getPhotoOrientation(metadata);

			} catch (ImageProcessingException e) {
				logger.info("Unable to read image meta data for photo: " + file.getAbsolutePath(), e);
			} catch (IOException e) {
				logger.info("Unable to read image meta data for photo: " + file.getAbsolutePath(), e);
			}

			try {
				String thumbnailPath = ImageHelper.generateAndSaveImage(library.getId(), path, ImageType.THUMBNAIL,
						imageRotationType);
				photo.setThumbnailPath(thumbnailPath);

				String webOptimisedImagePath = ImageHelper.generateAndSaveImage(library.getId(), path,
						ImageType.WEB_OPTIMISED, imageRotationType);
				photo.setWebOptimisedImagePath(webOptimisedImagePath);

			} catch (Exception e) {
				logger.error("Unable to create thumbnail of photo: " + file.getAbsolutePath(), e);
			}

			photo.setLibrary(library);
			String title = StringUtils.trimToEmpty(fileName);
			photo.setDisplayTitle(title);
			photo.setSearchText(album.getName() + " " + title);
			// photos.add(photo);
			totalPhotosSaved = Long.valueOf(totalPhotosSaved++);
		}

		photo.setUpdatedOn(date);

		boolean isSessionFlush = false;
		if (totalPhotosSaved % PHOTOS_SAVE_AMOUNT_MAX_SIZE == 0) {
			isSessionFlush = true;
		}

		savePhoto(photo, isSessionFlush);

	}

	private boolean isCreatePhoto(File file, Photo photo) {

		if (photo == null) {
			return true;
		}

		if (file.lastModified() != photo.getFileLastModifiedOn()) {
			return true;
		}

		if (file.length() != photo.getSizeInBytes()) {
			return true;
		}
		
		return false;
	}

	protected ImageRotationType getPhotoOrientation(Metadata metadata) {
		Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
		if (directory == null) {
			return null;
		}

		int exifTagOrientation = 0;
		try {
			exifTagOrientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		} catch (MetadataException me) {
			logger.warn("Could not get orientation");
		}

		ImageRotationType imageRotationType = ImageHelper.getImageRotationType(exifTagOrientation);
		return imageRotationType;
	}

	protected String processPhotoMetadata(Metadata metadata) {
		StringBuilder metadataBuilder = new StringBuilder();
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				if (metadataBuilder.length() > 0) {
					metadataBuilder.append(NEW_LINE);
				}
				metadataBuilder.append(tag.toString());
			}
		}

		return metadataBuilder.toString();
	}

	protected void savePhoto(Photo photo, boolean isSessionFlush) {
		photoDao.savePhoto(photo, isSessionFlush);
	}

	protected Photo getPhotoByAbsolutePath(String path) {
		Photo photo = photoDao.getPhotoByAbsolutePath(path);
		return photo;
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
