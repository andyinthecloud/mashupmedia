package org.mashupmedia.service;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlbumArtManagerImpl implements AlbumArtManager{
	public static final String DEFAULT_MIME_TYPE = "jpg";

	@Autowired
	private ConnectionManager connectionManager;
	
	@Autowired
	MusicManager musicManager;

	
	@Override
	public AlbumArtImage getAlbumArtImage(MusicLibrary musicLibrary, Song song) throws Exception {		
		AlbumArtImage savedAlbumArtImage = getAlbumArtImage(song);
		if (savedAlbumArtImage != null) {
			return savedAlbumArtImage;
		}
		
		AlbumArtImage albumArtImage = getLocalAlbumArtImage(musicLibrary, song);
		
		if (albumArtImage == null) {
			return null;
		}
		String thumbnailUrl = ImageHelper.generateAndSaveThumbnail(musicLibrary.getId(), albumArtImage.getUrl());
		albumArtImage.setThumbnailUrl(thumbnailUrl);
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
		
		album = musicManager.getAlbum(artistName, albumName);
		if (album == null) {
			return null;
		}
		
		AlbumArtImage albumArtImage = album.getAlbumArtImage();
		return albumArtImage;		
	}

	private AlbumArtImage getLocalAlbumArtImage(MusicLibrary musicLibrary, Song song) throws Exception {
		
		if (musicLibrary.isRemote()) {
			return null;
		}

		File musicFile = new File(song.getPath());
		String imagePath = null;
		String albumArtFileName = MashUpMediaConstants.COVER_ART_DEFAULT_NAME;
		AudioFile audioFile = AudioFileIO.read(musicFile);
		Tag tag = audioFile.getTag();
		Artwork artwork = tag.getFirstArtwork();
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
			File[] imageFiles = albumFolder.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File file, String fileName) {
					if (FileHelper.isSupportedImage(fileName) && FileHelper.isMatchingFileNamePattern(fileName, albumArtImagePattern)) {
						return true;
					}
					return false;
				}
			});

			if (imageFiles == null || imageFiles.length == 0) {
				return null;
			}

			File albumArtFile = imageFiles[0];
			imagePath = albumArtFile.getAbsolutePath();
			albumArtFileName = albumArtFile.getName();
			contentType = FileHelper.getFileExtension(albumArtFileName);
		}

		AlbumArtImage albumArtImage = new AlbumArtImage();				
		albumArtImage.setName(albumArtFileName);
		albumArtImage.setUrl(imagePath);
		albumArtImage.setContentType(contentType);
		return albumArtImage;
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
