package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.jaudiotagger.tag.datatype.Artwork;
import org.mashupmedia.comparator.FileComparator;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.exception.MashupMediaException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.util.EncryptionHelper;
import org.mashupmedia.util.FileHelper;
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

	@Override
	public void updateLibrary(Library library) {
		if (library instanceof MusicLibrary) {
			MusicLibrary musicLibrary = (MusicLibrary) library;
			updateMusicLibrary(musicLibrary);
		}

	}

	@Override
	public void updateMusicLibrary(MusicLibrary musicLibrary) {
		if (!musicLibrary.isEnabled()) {
			logger.info("Library is disabled, will not update:"
					+ musicLibrary.toString());
			return;
		}

		try {
			Location location = musicLibrary.getLocation();
			if (location instanceof FtpLocation) {
				prepareFtpLibrary(musicLibrary, (FtpLocation) location);
			} else {
				prepareFileLibrary(musicLibrary);
			}
		} catch (Exception e) {
			throw new MashupMediaException("Error updating the music library.",
					e);
		}

	}

	private void prepareFtpLibrary(Library library, FtpLocation ftpLocation) {
		FTPClient ftpClient = null;
		try {
			String decryptedPassword = EncryptionHelper.decryptText(ftpLocation
					.getPassword());
			ftpLocation.setPassword(decryptedPassword);
			ftpClient = connectionManager.connectToFtp(ftpLocation);
		} catch (Exception e) {
			throw new MashupMediaException("Unable to connect to ftp server", e);
		}

		if (ftpClient == null) {
			logger.error("Unable to prepare music library, ftp client is null.");
			return;
		}

		List<Artist> artists = connectionManager.getFtpArtists(ftpLocation);
		musicManager.saveArtists(library, artists);

	}

	private void prepareFileLibrary(MusicLibrary musicLibrary)
			throws CannotReadException, IOException, TagException,
			ReadOnlyFileException, InvalidAudioFrameException {
		Location location = musicLibrary.getLocation();
		File musicFolder = new File(location.getPath());
		if (!musicFolder.isDirectory()) {
			logger.error("Media library points to a file not a directory, exiting...");
			return;
		}

		List<Song> songs = new ArrayList<Song>();
		prepareSongs(songs, musicFolder, musicLibrary, null, null);

	}

	protected void prepareSongs(List<Song> songs, File folder,
			MusicLibrary musicLibrary, String artistName, String albumName)
			throws CannotReadException, IOException, TagException,
			ReadOnlyFileException, InvalidAudioFrameException {
		if (folder.isFile()) {
			return;
		}

		File[] files = folder.listFiles();
		Arrays.sort(files, new FileComparator());

		int musicFileCount = 0;

		for (File file : files) {
			String fileName = StringUtils.trimToEmpty(file.getName());

			if (file.isDirectory()) {
				artistName = StringUtils.trimToEmpty(artistName);

				if (StringUtils.isEmpty(artistName)) {
					artistName = fileName;
				} else {
					if (StringUtils.isBlank(albumName)) {
						albumName = fileName;
					} else {
						albumName += " - " + fileName;
					}
				}

				prepareSongs(songs, file, musicLibrary, artistName, albumName);
			}

			if (FileHelper.isSupportedSong(fileName)) {
				musicFileCount++;
				AudioFile audioFile = AudioFileIO.read(file);
				AudioHeader audioHeader = audioFile.getAudioHeader();
				long bitRate = audioHeader.getBitRateAsNumber();
				String format = audioHeader.getFormat();
				int trackLength = audioHeader.getTrackLength();

				Tag tag = audioFile.getTag();
				String songTitle = StringUtils.trimToEmpty(tag
						.getFirst(FieldKey.TITLE));
				int trackNumber = NumberUtils.toInt(tag
						.getFirst(FieldKey.TRACK));
				String artistNameValue = StringUtils.trimToEmpty(tag
						.getFirst(FieldKey.ALBUM_ARTIST));
				String albumNameValue = StringUtils.trimToEmpty(tag
						.getFirst(FieldKey.ALBUM));
				String genreValue = StringUtils.trimToEmpty(tag
						.getFirst(FieldKey.GENRE));
				int yearValue = NumberUtils.toInt(tag.getFirst(FieldKey.YEAR));

				Song song = new Song();

				if (trackNumber == 0) {
					trackNumber = musicFileCount;
				}
				song.setTrackNumber(trackNumber);

				if (StringUtils.isEmpty(songTitle)) {
					songTitle = file.getName();
				}
				song.setTitle(songTitle);
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
				if (StringUtils.isNotEmpty(albumNameValue)) {
					albumName = albumNameValue;
				}
				album.setName(albumName);
				if (musicFileCount == 1) {
					AlbumArtImage albumArtImage = getAlbumArtImage(musicLibrary, file, album);
					album.setAlbumArtImage(albumArtImage);									
				}
				
				song.setAlbum(album);

				Artist artist = new Artist();
				if (StringUtils.isEmpty(artistNameValue)) {
					artistName = file.getParentFile().getParentFile().getName();
				}
				artist.setName(artistName);
				album.setArtist(artist);

				songs.add(song);

			}

		}

	}

	protected AlbumArtImage getAlbumArtImage(MusicLibrary musicLibrary,
			File musicFile, Album album) throws CannotReadException,
			IOException, TagException, ReadOnlyFileException,
			InvalidAudioFrameException {
		String imagePath = null;
		String albumArtName = MashUpMediaConstants.COVER_ART_DEFAULT_NAME;
		AudioFile audioFile = AudioFileIO.read(musicFile);
		Tag tag = audioFile.getTag();
		Artwork artwork = tag.getFirstArtwork();
		if (artwork != null) {
			String mimeType = artwork.getMimeType();
			byte[] bytes = artwork.getBinaryData();
			if (bytes == null || bytes.length == 0) {
				return null;
			}
			imagePath = FileHelper.writeAlbumArt(musicLibrary.getId(),
					mimeType, bytes);
		} else {
			File albumFolder = musicFile.getParentFile();
			File[] imageFiles = albumFolder.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File file, String fileName) {
					if (FileHelper.isSupportedImage(fileName)) {
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
			albumArtName = albumArtFile.getName();

		}

		AlbumArtImage albumArtImage = new AlbumArtImage();
		albumArtImage.setAlbum(album);
		albumArtImage.setLibrary(musicLibrary);
		albumArtImage.setName(albumArtName);
		albumArtImage.setUrl(imagePath);
		return albumArtImage;
	}

}
