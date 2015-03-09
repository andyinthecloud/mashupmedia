package org.mashupmedia.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.mashupmedia.comparator.FileSizeComparator;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlbumArtManagerImpl implements AlbumArtManager {
	private Logger logger = Logger.getLogger(getClass());

	public static final String DEFAULT_MIME_TYPE = "jpg";

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	MusicManager musicManager;

	@Override
	public AlbumArtImage getAlbumArtImage(MusicLibrary musicLibrary, Song song) throws Exception {
		AlbumArtImage albumArtImage = getAlbumArtImage(song);
		if (!isAlbumArtImageEmpty(albumArtImage)) {
			return albumArtImage;
		}

		albumArtImage = getLocalAlbumArtImage(musicLibrary, song);
		return albumArtImage;
	}

	private AlbumArtImage getAlbumArtImage(Song song) {
		Artist artist = song.getArtist();
		if (artist == null) {
			return null;
		}

		Album album = song.getAlbum();
		if (album == null) {
			return null;
		}

		String artistName = artist.getName();
		String albumName = album.getName();

		AlbumArtImage albumArtImage = null;

		Album savedAlbum = musicManager.getAlbum(artistName, albumName);
		if (savedAlbum != null) {
			albumArtImage = savedAlbum.getAlbumArtImage();
			if (!isAlbumArtImageEmpty(albumArtImage)) {
				return albumArtImage;
			}
		}

		albumArtImage = album.getAlbumArtImage();
		return albumArtImage;
	}

	private boolean isAlbumArtImageEmpty(AlbumArtImage albumArtImage) {
		if (albumArtImage == null) {
			return true;
		}

		if (StringUtils.isBlank(albumArtImage.getUrl())) {
			return true;
		}

		return false;
	}

	private AlbumArtImage getLocalAlbumArtImage(MusicLibrary musicLibrary, Song song) throws Exception {

		if (musicLibrary.isRemote()) {
			return null;
		}

		File musicFile = new File(song.getPath());
		String imagePath = null;
		String albumArtFileName = MashUpMediaConstants.COVER_ART_DEFAULT_NAME;
		Artwork artwork = getArtwork(musicFile);

		// AudioFile audioFile = AudioFileIO.read(musicFile);
		// Tag tag = audioFile.getTag();
		//
		// Artwork artwork = tag.getFirstArtwork();
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
			File albumArtFile = FileHelper.createAlbumArtFile(musicLibrary.getId());
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

		AlbumArtImage albumArtImage = new AlbumArtImage();
		albumArtImage.setName(albumArtFileName);
		albumArtImage.setUrl(imagePath);
		albumArtImage.setContentType(contentType);

		String thumbnailUrl = imagePath;
		try {
			thumbnailUrl = ImageHelper.generateAndSaveThumbnail(musicLibrary.getId(), imagePath);
		} catch (Exception e) {
			logger.error("Error converting album art image to thumbnail, using original image.", e);
		}

		albumArtImage.setThumbnailUrl(thumbnailUrl);

		return albumArtImage;
	}

	protected File getAlbumArtImageFile(File albumFolder, final String albumArtImagePattern) {
		File[] imageFiles = albumFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String fileName) {
				if (FileHelper.isSupportedImage(fileName) && FileHelper.isMatchingFileNamePattern(fileName, albumArtImagePattern)) {
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
