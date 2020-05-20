package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.dao.PhotoDao;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.model.library.PhotoLibrary;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.ImageHelper.ImageRotationType;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

@Service
@Transactional
@Slf4j
public class PhotoLibraryUpdateManagerImpl implements PhotoLibraryUpdateManager {


	@Autowired
	private PhotoDao photoDao;

	@Autowired
	private ProcessManager processManager;

	@Autowired
	private LibraryDao libraryDao;

	@Autowired
	private LibraryManager libraryManager;

	private final int BATCH_INSERT_ITEMS = 20;

	@Override
	public void deleteObsoletePhotos(long libraryId, Date date) {
		List<Photo> photos = photoDao.getObsoletePhotos(libraryId, date);
		int totalDeletedPhotos = photoDao.removeObsoletePhotos(libraryId, date);

		for (Photo photo : photos) {
			deletePhoto(photo);
		}

		log.info(totalDeletedPhotos + " obsolete photos deleted.");
	}
	
	
	private void deletePhoto(Photo photo) {
		processManager.killProcesses(photo.getId());
		FileHelper.deleteFile(photo.getWebOptimisedImagePath());		
	}
	
	
	@Override
	public void deleteFile(PhotoLibrary library, File file) {		
		String path = file.getAbsolutePath();
		Photo photo = photoDao.getPhotoByAbsolutePath(path);
		if (photo == null) {
			return;
		}
		
		deletePhoto(photo);
	}
	

	@Override
	public void updateLibrary(PhotoLibrary library, File folder, Date date) {
		List<Photo> photos = new ArrayList<Photo>();
		processPhotos(folder, date, null, library, photos);

		long totalMediaItems = libraryDao.getTotalMediaItemsFromLibrary(library.getId());
		log.info("Total photos saved:" + totalMediaItems);
	}

	protected void processPhotos(File file, Date date, String albumName, PhotoLibrary library, List<Photo> photos) {

		String fileName = file.getName();

		if (file.isDirectory()) {

			if (fileName.startsWith(".")) {
				return;
			}

			if (StringUtils.isNotBlank(albumName) && StringUtils.isNotBlank(fileName)) {
				albumName += " / " + fileName;
			} else {
				albumName = fileName;
			}

			File[] files = file.listFiles();
			if (files == null) {
				return;
			}

			Arrays.sort(files);
			for (File childFile : files) {
				processPhotos(childFile, date, albumName, library, photos);
			}

			savePhotos(photos);

			photos.clear();

			libraryManager.saveMediaItemLastUpdated(library.getId());

		}
		
		if (StringUtils.isEmpty(albumName)) {
			saveFile(library, file, date);
			return;
		}

		Photo photo = preparePhoto(file, library, albumName, date);
		if (photo != null) {
			photos.add(photo);	
		}
	}

	@Override
	public void saveFile(PhotoLibrary library, File file, Date date) {
		
		File libraryFolder = new File(library.getLocation().getPath());
		List<File> relativeFolders = LibraryHelper.getRelativeFolders(libraryFolder, file);
		if (relativeFolders == null || relativeFolders.isEmpty()) {
			relativeFolders.add(libraryFolder);
		}
		
		StringBuilder albumNameBuilder = new StringBuilder();
		for (File relativeFolder : relativeFolders) {
			if (albumNameBuilder.length() > 0) {
				albumNameBuilder.append(" / ");
			}
			albumNameBuilder.append(relativeFolder.getName());
			
		}
				
		Photo photo = preparePhoto(file, library, albumNameBuilder.toString(), date);
		List<Photo> photos = new ArrayList<>();
		if (photo != null) {
			photos.add(photo);	
		}
		
		savePhotos(photos);
	}
	
	
	public Photo preparePhoto(File file, PhotoLibrary library, String albumName, Date date) {
		
		String fileName = file.getName();
		

		String path = file.getAbsolutePath();
		Photo photo = getPhotoByAbsolutePath(path);

		if (isCreatePhoto(file, photo)) {
			if (photo == null) {
				photo = new Photo();
				photo.setCreatedOn(new Date());

				Album album = new Album();
				album.setName(albumName);
				album.setUpdatedOn(date);
				photo.setAlbum(album);
			}

			String fileExtension = FileHelper.getFileExtension(fileName);
			MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(fileExtension);

			if (!MediaItemHelper.isCompatiblePhotoFormat(mediaContentType)) {
				return null;
			}

			Set<MediaEncoding> mediaEncodings = photo.getMediaEncodings();
			if (mediaEncodings == null) {
				mediaEncodings = new HashSet<MediaEncoding>();
			}
			mediaEncodings.clear();
			MediaEncoding mediaEncoding = new MediaEncoding();
			mediaEncoding.setMediaContentType(mediaContentType);
			mediaEncoding.setOriginal(true);
			mediaEncodings.add(mediaEncoding);
			photo.setMediaEncodings(mediaEncodings);

			photo.setFormat(mediaContentType.getName());
			photo.setEnabled(true);
			long lastModified = file.lastModified();
			photo.setFileLastModifiedOn(lastModified);

			// Default taken on to last modified, override later to get the
			// actual taken on
			// from the meta data if possible
			photo.setTakenOn(new Date(lastModified));

			photo.setFileName(fileName);
			photo.setMediaType(MediaType.PHOTO);
			photo.setPath(path);
			photo.setSizeInBytes(file.length());

			try {
				processPhotoMetadata(file, photo);
			} catch (ImageProcessingException e) {
				log.info("Unable to read image meta data for photo: " + file.getAbsolutePath(), e);
			} catch (IOException e) {
				log.info("Unable to read image meta data for photo: " + file.getAbsolutePath(), e);
			} catch (MetadataException e) {
				log.info("Unable to read image meta data for photo: " + file.getAbsolutePath(), e);
			}

			int orientation = photo.getOrientation();
			ImageRotationType imageRotationType = ImageHelper.getImageRotationType(orientation);

			try {
				String webOptimisedImagePath = ImageHelper.generateAndSaveImage(library.getId(), path,
						ImageType.WEB_OPTIMISED, imageRotationType);
				photo.setWebOptimisedImagePath(webOptimisedImagePath);

			} catch (Exception e) {
				log.error("Will not save photo, unable to create thumbnail: " + file.getAbsolutePath(), e);
				return null;
			}

			photo.setLibrary(library);
			String title = StringUtils.trimToEmpty(fileName);
			photo.setDisplayTitle(title);
			photo.setSearchText(albumName + " " + title);

		}

		photo.setUpdatedOn(date);
		
		return photo;		
	}
	

	private void savePhotos(List<Photo> photos) {

		if (photos == null || photos.isEmpty()) {
			return;
		}

		int totalPhotos = photos.size();

		for (int i = 0; i < totalPhotos; i++) {
			Photo photo = photos.get(i);

			Album album = photo.getAlbum();
			String albumName = album.getName();
			Date updatedOn = album.getUpdatedOn();
			album = getAlbum(albumName, updatedOn);
			photo.setAlbum(album);

			boolean isSessionFlush = false;
			if (i % BATCH_INSERT_ITEMS == 0 || (i == totalPhotos - 1)) {
				isSessionFlush = true;
			}

			photoDao.savePhoto(photo, isSessionFlush);

		}

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

	protected void processPhotoMetadata(File file, Photo photo)
			throws ImageProcessingException, IOException, MetadataException {

		Metadata metadata = ImageMetadataReader.readMetadata(file);

		Date takenOn = getTakenOnDatefromMeta(file, metadata);
		if (takenOn != null) {
			photo.setTakenOn(takenOn);
		}

		int orientation = getOrientatonFromMeta(metadata);
		photo.setOrientation(orientation);
	}

	protected int getOrientatonFromMeta(Metadata metadata) throws MetadataException {
		int orientation = 0;
		ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
		if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
			orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		}

		return orientation;

	}

	protected Date getTakenOnDatefromMeta(File file, Metadata metadata) {
		ExifIFD0Directory exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
		if (exifIFD0Directory != null) {
			Date date = exifIFD0Directory.getDate(ExifIFD0Directory.TAG_DATETIME);
			if (date != null) {
				return date;
			}
		}

		ExifSubIFDDirectory subDir = metadata.getDirectory(ExifSubIFDDirectory.class);
		if (subDir != null) {
			Date date = subDir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			if (date != null) {
				return date;
			}
		}

		return null;

	}

	protected Photo getPhotoByAbsolutePath(String path) {
		Photo photo = photoDao.getPhotoByAbsolutePath(path);
		return photo;
	}

	protected Album getAlbum(String albumName, Date date) {
		albumName = StringUtils.trimToEmpty(albumName);

		Album savedAlbum = getAlbum(albumName);
		if (savedAlbum != null) {
			return savedAlbum;
		}

		Album album = createAlbum(albumName, date);
		return album;
	}

	protected Album getAlbum(String albumName) {
		List<Album> albums = photoDao.getAlbums(albumName);
		if (albums != null && !albums.isEmpty()) {
			return albums.get(0);
		}
		return null;
	}

	protected Album createAlbum(String albumName, Date date) {
		Album album = new Album();
		album.setCreatedOn(date);
		album.setName(albumName);
		return album;

	}
}
