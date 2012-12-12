package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.mashupmedia.comparator.FileComparator;
import org.mashupmedia.exception.MashupMediaException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.service.ConnectionManager.LocationType;
import org.mashupmedia.util.EncryptionHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LibraryUpdateManagerImpl implements LibraryUpdateManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private MusicManager musicManager;

	private LibraryUpdateManagerImpl() {
		// Disable the jaudiotagger library logging
		LogManager.getLogManager().reset();
		java.util.logging.Logger globalLogger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
		globalLogger.setLevel(java.util.logging.Level.OFF);
	}

	@Override
	public void updateLibrary(Library library) {
		if (library instanceof MusicLibrary) {
			MusicLibrary musicLibrary = (MusicLibrary) library;
			updateMusicLibrary(musicLibrary);
		}
	}

	@Override
	@Transactional(noRollbackFor = RuntimeException.class)
	public void updateMusicLibrary(MusicLibrary musicLibrary) {
		if (!musicLibrary.isEnabled()) {
			logger.info("Library is disabled, will not update:" + musicLibrary.toString());
			return;
		}

		try {
			Location location = musicLibrary.getLocation();
			LocationType locationType = LibraryHelper.getLocationType(location);
			if (locationType == LocationType.FTP) {
				prepareFtpMusicLibrary(musicLibrary, (FtpLocation) location);
			} else if (locationType == LocationType.LOCAL) {
				prepareFileMusicLibrary(musicLibrary);
			} 
			
		} catch (Exception e) {
			throw new MashupMediaException("Error updating the music library.", e);
		}

	}

	private void prepareFtpMusicLibrary(MusicLibrary musicLibrary, FtpLocation ftpLocation) {
		FTPClient ftpClient = null;
		try {
			String decryptedPassword = EncryptionHelper.decryptText(ftpLocation.getPassword());
			ftpLocation.setPassword(decryptedPassword);
			ftpClient = connectionManager.connectToFtp(ftpLocation);
		} catch (Exception e) {
			throw new MashupMediaException("Unable to connect to ftp server", e);
		}

		if (ftpClient == null) {
			logger.error("Unable to prepare music library, ftp client is null.");
			return;
		}
		
		Date date = new Date();
		List<Song> songs = connectionManager.getFtpSongs(musicLibrary, date);
		musicManager.saveSongs(musicLibrary, songs);
		musicManager.deleteObsoleteSongs(musicLibrary.getId(), date);

	}

	private void prepareFileMusicLibrary(MusicLibrary musicLibrary) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
			InvalidAudioFrameException {
		Location location = musicLibrary.getLocation();
		File musicFolder = new File(location.getPath());
		if (!musicFolder.isDirectory()) {
			logger.error("Media library points to a file not a directory, exiting...");
			return;
		}

		Date date = new Date();
		List<Song> songs = new ArrayList<Song>();
		prepareSongs(date, songs, musicFolder, musicLibrary, null, null);
		musicManager.saveSongs(musicLibrary, songs);
		musicManager.deleteObsoleteSongs(musicLibrary.getId(), date);
//		musicManager.saveSongs(musicLibrary, songs);

	}

	protected void prepareSongs(Date date, List<Song> songs, File folder, MusicLibrary musicLibrary, String folderArtistName, String folderAlbumName)
			throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		if (folder.isFile()) {
			return;
		}

		File[] files = folder.listFiles();
		Arrays.sort(files, new FileComparator());

		int musicFileCount = 0;

		for (File file : files) {
			String fileName = StringUtils.trimToEmpty(file.getName());

			if (file.isDirectory()) {
				folderArtistName = StringUtils.trimToEmpty(folderArtistName);

				if (StringUtils.isEmpty(folderArtistName)) {
					folderArtistName = fileName;
					prepareSongs(date, songs, file, musicLibrary, folderArtistName, folderAlbumName);
					
					musicManager.saveSongs(musicLibrary, songs);
					songs = new ArrayList<Song>();
//					songs.clear();
					
					folderArtistName = "";
				} else {
					if (StringUtils.isBlank(folderAlbumName)) {
						folderAlbumName = fileName;
					} else {
						folderAlbumName += " - " + fileName;
					}
					prepareSongs(date, songs, file, musicLibrary, folderArtistName, folderAlbumName);
					folderAlbumName = "";
				}

			}

			if (FileHelper.isSupportedSong(fileName)) {
				musicFileCount++;

				Tag tag = null;
				long bitRate = 0;
				String format = null;
				int trackLength = 0;
				String tagSongTitle = null;
				int trackNumber = 0;
				String tagArtistName = null;
				String tagAlbumName = null;
				String genreValue = null;
				int yearValue = 0;

				try {
					AudioFile audioFile = AudioFileIO.read(file);
					AudioHeader audioHeader = audioFile.getAudioHeader();
					bitRate = audioHeader.getBitRateAsNumber();
					format = audioHeader.getFormat();
					trackLength = audioHeader.getTrackLength();
					tag = audioFile.getTag();

				} catch (Exception e) {
					logger.info(e);
				}

				if (tag != null) {
					tagSongTitle = StringUtils.trimToEmpty(tag.getFirst(FieldKey.TITLE));
					trackNumber = NumberUtils.toInt(tag.getFirst(FieldKey.TRACK));
					tagArtistName = StringUtils.trimToEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST));
					tagAlbumName = StringUtils.trimToEmpty(tag.getFirst(FieldKey.ALBUM));
					genreValue = StringUtils.trimToEmpty(tag.getFirst(FieldKey.GENRE));
					yearValue = NumberUtils.toInt(tag.getFirst(FieldKey.YEAR));
				} else {
					logger.info("Unable to read tag info for music file: " + file.getAbsolutePath());
				}

				Song song = new Song();
				song.setUpdatedOn(date);

				if (trackNumber == 0) {
					trackNumber = musicFileCount;
				}
				song.setTrackNumber(trackNumber);

				if (StringUtils.isEmpty(tagSongTitle)) {
					tagSongTitle = file.getName();
				} else {
					logger.debug("Found song title for music file: " + file.getAbsolutePath());
					song.setReadableTag(true);
				}

				song.setTitle(tagSongTitle);
				song.setFormat(format);
				song.setTrackLength(trackLength);
				song.setBitRate(bitRate);
				song.setFileName(file.getName());
				song.setPath(file.getAbsolutePath());
				song.setLibrary(musicLibrary);
				song.setSizeInBytes(file.length());

				if (yearValue > 0) {
					Year year = new Year();
					year.setYear(yearValue);
					song.setYear(year);
				}

				if (StringUtils.isNotEmpty(genreValue)) {
					Genre genre = new Genre();
					genre.setName(genreValue);
					song.setGenre(genre);
				}

				Album album = new Album();
				if (StringUtils.isEmpty(tagAlbumName)) {
					tagAlbumName = folderAlbumName;
				}
				if (StringUtils.isBlank(folderAlbumName)) {
					logger.info("Unable to add song to the library. No album found for artist = " + folderArtistName + ", song title = "
							+ tagSongTitle);
				}
				album.setName(tagAlbumName);
				album.setFolderName(folderAlbumName);
				song.setAlbum(album);

				Artist artist = new Artist();
				if (StringUtils.isEmpty(tagArtistName)) {
					tagArtistName = folderArtistName;
				}
				artist.setName(tagArtistName);
				artist.setFolderName(folderArtistName);

				// if (musicFileCount == 1) {
				// try {
				// AlbumArtImage albumArtImage =
				// processAlbumArtImage(musicLibrary, file, folderAlbumName);
				// album.setAlbumArtImage(albumArtImage);
				// } catch (Exception e) {
				// logger.info("Unable to read the album art...", e);
				// }
				// }

				album.setArtist(artist);
				song.setArtist(artist);

				songs.add(song);

			}

		}

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

	// protected AlbumArtImage processAlbumArtImage(MusicLibrary musicLibrary,
	// File musicFile, String albumName) throws CannotReadException,
	// IOException, TagException, ReadOnlyFileException,
	// InvalidAudioFrameException {
	//
	// String imagePath = null;
	// String albumArtFileName = MashUpMediaConstants.COVER_ART_DEFAULT_NAME;
	// AudioFile audioFile = AudioFileIO.read(musicFile);
	// Tag tag = audioFile.getTag();
	// Artwork artwork = tag.getFirstArtwork();
	// final String albumArtImagePattern =
	// musicLibrary.getAlbumArtImagePattern();
	// String contentType = null;
	// if (artwork != null) {
	// contentType = prepareMimeType(artwork.getMimeType());
	// byte[] bytes = artwork.getBinaryData();
	// if (bytes == null || bytes.length == 0) {
	// return null;
	// }
	// // imagePath = FileHelper.writeAlbumArt(musicLibrary.getId(), bytes);
	// File albumArtFile = FileHelper.createAlbumArtFile(musicLibrary.getId());
	// FileUtils.writeByteArrayToFile(albumArtFile, bytes);
	// imagePath = albumArtFile.getAbsolutePath();
	//
	//
	// } else {
	// File albumFolder = musicFile.getParentFile();
	// File[] imageFiles = albumFolder.listFiles(new FilenameFilter() {
	//
	// @Override
	// public boolean accept(File file, String fileName) {
	// if (FileHelper.isSupportedImage(fileName) &&
	// FileHelper.isMatchingFileNamePattern(fileName, albumArtImagePattern)) {
	// return true;
	// }
	// return false;
	// }
	// });
	//
	// if (imageFiles == null || imageFiles.length == 0) {
	// return null;
	// }
	//
	// File albumArtFile = imageFiles[0];
	// imagePath = albumArtFile.getAbsolutePath();
	// albumArtFileName = albumArtFile.getName();
	// contentType = FileHelper.getFileExtension(albumArtFileName);
	// }
	//
	// AlbumArtImage albumArtImage = new AlbumArtImage();
	// albumArtImage.setLibrary(musicLibrary);
	// albumArtImage.setName(albumArtFileName);
	// albumArtImage.setUrl(imagePath);
	// albumArtImage.setContentType(contentType);
	// return albumArtImage;
	// }

}
