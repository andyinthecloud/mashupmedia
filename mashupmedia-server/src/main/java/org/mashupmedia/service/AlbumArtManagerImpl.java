package org.mashupmedia.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.mashupmedia.comparator.FileSizeComparator;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.MetaEntity;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.FileHelper.FileType;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.MetaEntityHelper;
import org.mashupmedia.util.ImageHelper.ImageType;
import org.mashupmedia.util.StringHelper;
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


		File musicFile = new File(track.getPath());
		String imagePath = null;
		String albumArtFileName = MashUpMediaConstants.COVER_ART_DEFAULT_NAME;
		Artwork artwork = null;
		try {
			artwork = getArtwork(musicFile);
		} catch (Exception e) {
			log.info("Error reading music file artwork: " + musicFile.getAbsolutePath(), e);
		}

		final String albumArtImagePattern = musicLibrary.getAlbumArtImagePattern();
		String contentType = null;
		if (artwork != null) {
			contentType = prepareMimeType(artwork.getMimeType());
			byte[] bytes = artwork.getBinaryData();
			if (bytes == null || bytes.length == 0) {
				return null;
			}
			// imagePath = FileHelper.writeAlbumArt(musicLibrary.getId(),
			// bytes);
			User user = musicLibrary.getUser();
			File albumArtFile = FileHelper.createMediaItemFile(user.getFolderName(), musicLibrary.getId(), FileType.ALBUM_ART_THUMBNAIL);
			FileUtils.writeByteArrayToFile(albumArtFile, bytes);
			imagePath = albumArtFile.getAbsolutePath();

		} else {
			File albumFolder = musicFile.getParentFile();
			File albumArtFile = getAlbumArtImageFile(albumFolder, albumArtImagePattern);
			if (albumArtFile == null) {
				return null;
			}

			imagePath = albumArtFile.getAbsolutePath();
			albumArtFileName = albumArtFile.getName();
			contentType = FileHelper.getFileExtension(albumArtFileName);
		}

		MetaImage albumArtImage = new MetaImage();
		albumArtImage.setName(albumArtFileName);
		albumArtImage.setUrl(imagePath);
		albumArtImage.setContentType(contentType);

		String thumbnailUrl = imagePath;
		User user = musicLibrary.getUser();
		try {
			thumbnailUrl = ImageHelper.generateAndSaveMusicAlbumArtThumbnail(user.getFolderName(), musicLibrary.getId(), imagePath);
		} catch (Exception e) {
			log.error("Error converting album art image to thumbnail, using original image.", e);
		}

		albumArtImage.setThumbnailUrl(thumbnailUrl);

		return albumArtImage;
	}

	protected File getAlbumArtImageFile(File albumFolder, final String albumArtImagePattern) {
		File[] imageFiles = albumFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String fileName) {
				if (FileHelper.isSupportedImage(fileName)
						&& FileHelper.isMatchingFileNamePattern(fileName, albumArtImagePattern)) {
					return true;
				}
				return false;
			}
		});

		if (imageFiles != null && imageFiles.length > 0) {
			Arrays.sort(imageFiles, new FileSizeComparator());
			return imageFiles[imageFiles.length - 1];
		}

		imageFiles = albumFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				FileHelper.getFileExtension(name);
				if (FileHelper.isSupportedImage(name)) {
					return true;
				}
				return false;
			}
		});

		if (imageFiles != null && imageFiles.length > 0) {
			Arrays.sort(imageFiles, new FileSizeComparator());
			return imageFiles[0];
		}

		return null;

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

	private String prepareMimeType(String mimeType) {
		mimeType = StringUtils.trimToEmpty(mimeType);
		String extension = DEFAULT_MIME_TYPE;
		if (StringUtils.isNotEmpty(mimeType)) {
			extension = StringHelper.find(mimeType, "/.*").toLowerCase();
			extension = extension.replaceFirst("/", "");
		}
		return mimeType;
	}

}
