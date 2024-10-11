package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.mashupmedia.component.TranscodeConfigurationComponent;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.service.storage.StorageManager;
import org.mashupmedia.service.transcode.TranscodeImageManager;
import org.mashupmedia.util.AdminHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.MetaEntityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AlbumArtManagerImpl implements AlbumArtManager {

	public static final String DEFAULT_MIME_TYPE = "jpg";

	private final MusicManager musicManager;
	private final ConnectionManager connectionManager;
	private final StorageManager storageManager;
	private final TranscodeImageManager transcodeImageManager;
	private final TranscodeConfigurationComponent transcodeConfigurationComponent;

	@Override
	public MetaImage getMetaImage(MusicLibrary musicLibrary, Track track) throws Exception {
		MetaImage albumArtImage = getPreferredImage(track);
		if (!isAlbumArtImageEmpty(albumArtImage)) {
			return albumArtImage;
		}

		albumArtImage = getLocalAlbumArtImage(musicLibrary, track);
		return albumArtImage;
	}

	private MetaImage getPreferredImage(Track track) {
		Artist artist = track.getArtist();
		if (artist == null) {
			return null;
		}

		Album album = track.getAlbum();
		if (album == null) {
			return null;
		}

		String artistName = artist.getName();
		String albumName = album.getName();

		MetaImage metaImage = null;

		Album savedAlbum = musicManager.getAlbum(artistName, albumName);
		MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();

		if (savedAlbum != null) {
			metaImage = metaImageHelper.getDefaultEntity(savedAlbum.getMetaImages());

			if (!isAlbumArtImageEmpty(metaImage)) {
				return metaImage;
			}
		}

		metaImage = metaImageHelper.getDefaultEntity(album.getMetaImages());
		return metaImage;
	}

	private boolean isAlbumArtImageEmpty(MetaImage albumArtImage) {
		if (albumArtImage == null) {
			return true;
		}

		if (StringUtils.isBlank(albumArtImage.getUrl())) {
			return true;
		}

		try {
			byte[] imageBytes = connectionManager.getAlbumArtImageBytes(albumArtImage, ImageType.ORIGINAL);
			if (imageBytes != null && imageBytes.length > 0) {
				return false;
			}
		} catch (IOException e) {
			log.error("Error getting album art image", e);
		}

		return true;
	}

	private MetaImage getLocalAlbumArtImage(MusicLibrary musicLibrary, Track track) throws Exception {

		MediaResource mediaResource = track.getOriginalMediaResource();
		// InputStream inputStream =
		// storageManager.getInputStream(mediaResource.getPath());
		if (mediaResource == null) {
			return null;
		}

		File musicFile = new File(mediaResource.getPath());

		// String imagePath = null;
		String albumArtFileName = MashUpMediaConstants.COVER_ART_DEFAULT_NAME;
		Artwork artwork = null;
		try {
			artwork = getArtwork(musicFile);
		} catch (Exception e) {
			log.info("Error reading music file artwork: " + musicFile.getAbsolutePath(), e);
		}

		// final String albumArtImagePattern = musicLibrary.getAlbumArtImagePattern();
		// String contentType = null;
		// InputStream imageInputStream = null;
		// InputStream thumbnailInputStream = null;
		User user = AdminHelper.getLoggedInUser();
		Path tempAlbumArtPath = user.createTempResourcePath();
		// Path tempAlbumThumbnailArtPath = user.createTempResourcePath();

		Path tempProcessedAlbumArtPath = null;
		Path tempProcessedAlbumThumbnailArtPath = null;

		if (artwork != null) {
			// contentType = prepareMimeType(artwork.getMimeType());
			byte[] bytes = artwork.getBinaryData();
			if (bytes == null || bytes.length == 0) {
				return null;
			}

			Files.write(tempAlbumArtPath, bytes);

			// imagePath = FileHelper.writeAlbumArt(musicLibrary.getId(),
			// bytes);
			// User user = musicLibrary.getUser();
			// imageInputStream = new ByteArrayInputStream(bytes);
			// thumbnailInputStream = new ByteArrayInputStream(bytes);
			// albumArtPath = transcodeImageManager.processImage(inputStream,
			// track.getTranscodeImageMediaContentType());
			// albumThumbnailArtPath = transcodeImageManager.processThumbnail(inputStream,
			// track.getTranscodeImageMediaContentType());
			// storageManager.store(albumArtPath);
			// IOUtils.closeQuietly(inputStream);
			// Files.delete(albumArtPath);

			// File albumArtFile = FileHelper.createMediaItemFile(user.getFolderName(),
			// musicLibrary.getId(), FileType.ALBUM_ART_THUMBNAIL);
			// FileUtils.writeByteArrayToFile(albumArtFile, bytes);
			// imagePath = albumArtFile.getAbsolutePath();

		} else {
			File albumFolder = musicFile.getParentFile();
			String albumArtImagePattern = musicLibrary.getAlbumArtImagePattern();
			Path originalAlbumArtPath = getAlbumArtImageFile(albumFolder.toPath(), albumArtImagePattern);
			if (originalAlbumArtPath == null) {
				return null;
			}

			Files.copy(originalAlbumArtPath, tempAlbumArtPath);

			// byte[] bytes = FileUtils.readFileToByteArray(albumArtFile);
			// if (albumArtFile == null) {
			// return null;
			// }

			// imageInputStream = new FileInputStream(albumArtFile);
			// thumbnailInputStream = new ByteArrayInputStream(bytes);

			// imagePath = albumArtFile.getAbsolutePath();
			// albumArtFileName = albumArtFile.getName();
			// contentType = FileHelper.getFileExtension(albumArtFileName);
		}

		MediaContentType imageMediaContentType = transcodeConfigurationComponent.getTranscodeImageMediaContentType();

		tempProcessedAlbumArtPath = transcodeImageManager.processImage(
				tempAlbumArtPath,
				imageMediaContentType);
		String albumArtPath = storageManager.store(tempProcessedAlbumArtPath);

		tempProcessedAlbumThumbnailArtPath = transcodeImageManager.processThumbnail(
				tempAlbumArtPath,
				imageMediaContentType);
		String albumArtThumbnailPath = storageManager.store(tempProcessedAlbumArtPath);

		// IOUtils.closeQuietly(imageInputStream);
		// IOUtils.closeQuietly(thumbnailInputStream);

		Files.delete(tempAlbumArtPath);
		Files.delete(tempProcessedAlbumArtPath);
		Files.delete(tempProcessedAlbumThumbnailArtPath);

		MetaImage albumArtImage = new MetaImage();
		albumArtImage.setName(albumArtFileName);
		albumArtImage.setUrl(albumArtPath);
		albumArtImage.setThumbnailUrl(albumArtThumbnailPath);
		albumArtImage.setMimeType(imageMediaContentType.getMimeType());

		// String thumbnailUrl = imagePath;
		// User user = musicLibrary.getUser();
		// try {
		// thumbnailUrl =
		// ImageHelper.generateAndSaveMusicAlbumArtThumbnail(user.getFolderName(),
		// musicLibrary.getId(), imagePath);
		// } catch (Exception e) {
		// log.error("Error converting album art image to thumbnail, using original
		// image.", e);
		// }

		// albumArtImage.setThumbnailUrl(thumbnailUrl);

		return albumArtImage;
	}

	protected Path getAlbumArtImageFile(Path albumFolder, final String albumArtImagePattern) throws IOException {

		// Stream<Path> stream = Files.list(albumFolder)
		// .filter(file -> !Files.isDirectory(file))
		// .collect(Collectors.toSet())

		// ;

		// https://www.baeldung.com/java-list-directory-files

		List<Path> imagePaths = new ArrayList<>();
		imagePaths = Files.list(albumFolder)
				.filter(path -> !Files.isDirectory(path))
				.filter(path -> FileHelper.isSupportedImage(path.getFileName()))
				.filter(path -> FileHelper.isMatchingFileNamePattern(path.toFile().getName(), albumArtImagePattern))
				.sorted(Comparator.comparing(path -> FileHelper.getPathSize(path)))
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());

		// File[] imageFiles = albumFolder.listFiles(new FilenameFilter() {

		// @Override
		// public boolean accept(File file, String fileName) {
		// if (FileHelper.isSupportedImage(fileName)
		// && FileHelper.isMatchingFileNamePattern(fileName, albumArtImagePattern)) {
		// return true;
		// }
		// return false;
		// }
		// });

		// if (imagePaths.isEmpty() imageFiles != null && imageFiles.length > 0) {
		// Arrays.sort(imageFiles, new FileSizeComparator());
		// return imageFiles[imageFiles.length - 1];
		// }

		// imageFiles = albumFolder.listFiles(new FilenameFilter() {
		// @Override
		// public boolean accept(File dir, String name) {
		// FileHelper.getFileExtension(name);
		// if (FileHelper.isSupportedImage(name)) {
		// return true;
		// }
		// return false;
		// }
		// });

		// if (imageFiles != null && imageFiles.length > 0) {
		// Arrays.sort(imageFiles, new FileSizeComparator());
		// return imageFiles[0];
		// }

		return imagePaths.isEmpty() ? null : imagePaths.get(0);

	}

	protected Artwork getArtwork(File musicFile) throws Exception {
		AudioFile audioFile = AudioFileIO.read(musicFile);
		Tag tag = audioFile.getTag();
		if (tag == null) {
			return null;
		}

		Artwork artwork = tag.getFirstArtwork();
		return artwork;
	}

	// private String prepareMimeType(String mimeType) {
	// mimeType = StringUtils.trimToEmpty(mimeType);
	// String extension = DEFAULT_MIME_TYPE;
	// if (StringUtils.isNotEmpty(mimeType)) {
	// extension = StringHelper.find(mimeType, "/.*").toLowerCase();
	// extension = extension.replaceFirst("/", "");
	// }
	// return mimeType;
	// }

}
