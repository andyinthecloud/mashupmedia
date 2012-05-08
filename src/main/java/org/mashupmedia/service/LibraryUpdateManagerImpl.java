package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;

import java.io.File;
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
			logger.info("Library is disabled, will not update:" + musicLibrary.toString());
			return;
		}
		Location location = musicLibrary.getLocation();
		if (location instanceof FtpLocation) {
			prepareFtpLibrary(musicLibrary, (FtpLocation) location);
		} else {
			prepareFileLibrary(location);
		}

	}

	private void prepareFtpLibrary(Library library, FtpLocation ftpLocation) {
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

		List<Artist> artists = connectionManager.getFtpArtists(ftpLocation);
		musicManager.saveArtists(library, artists);

	}

	private void prepareFileLibrary(Location location) {
		File musicFolder = new File(location.getPath());
		if (!musicFolder.isDirectory()) {
			logger.error("Media library points to a file not a directory, exiting...");
			return;
		}

		List<Song> songs = new ArrayList<Song>();
		prepareSongs(songs, musicFolder);

	}

	protected void prepareArtists(List<Song> songs, File folder) throws CannotReadException, IOException, TagException, ReadOnlyFileException,
			InvalidAudioFrameException {
		if (folder.isFile()) {
			return;
		}

		File[] files = folder.listFiles();
		Arrays.sort(files, new FileComparator());
		

		int musicFileCount = 0;
		
		
		for (File file : files) {
			if (file.isDirectory()) {
				prepareArtists(songs, file);
			}

			if (FileHelper.isSupportedSong(file.getName())) {
				musicFileCount++;
				AudioFile audioFile = AudioFileIO.read(file);
				AudioHeader audioHeader =  audioFile.getAudioHeader();
				long bitRate = audioHeader.getBitRateAsNumber();
				String format = audioHeader.getFormat();
				int trackLength = audioHeader.getTrackLength();
				
				Tag tag = audioFile.getTag();
				String songTitle = StringUtils.trimToEmpty(tag.getFirst(FieldKey.TITLE));
				int trackNumber = NumberUtils.toInt(tag.getFirst(FieldKey.TRACK));
				String artistName = StringUtils.trimToEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST));
				String albumName = StringUtils.trimToEmpty(tag.getFirst(FieldKey.ALBUM));
				String genreValue = StringUtils.trimToEmpty(tag.getFirst(FieldKey.GENRE));
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
				song.setLibrary(library);
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
				if (StringUtils.isEmpty(albumName)) {
					albumName = file.getParentFile().getName();
				}
				album.setName(albumName);
				song.setAlbum(album);
				
				Artist artist = new Artist();
				if (StringUtils.isEmpty(artistName)) {
					artistName = file.getParentFile().getParentFile().getName();
				}
				artist.setName(artistName);
				album.setArtist(artist);
				
				songs.add(song);
				continue;
				
			}
			
			if (FileHelper.isSupportedImage(file.getName())) {
				
			}
		}

	}

//
//	protected Artist getArtist(List<Artist> artists, String artistValue) {
//		artistValue = StringUtils.trimToEmpty(artistValue);
//		if (StringUtils.isEmpty(artistValue)) {
//			return null;
//		}
//		
//		
//		for (Artist artist : artists) {
//			String artistName = artist.getName();
//			if (artistName.equalsIgnoreCase(artistValue)) {
//				return artist;
//			}
//		}
//		
//		Artist artist = new Artist();
//		artist.setName(artistValue);
//		artists.add(artist);
//		return artist;
//	}

}
