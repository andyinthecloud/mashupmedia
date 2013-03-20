package org.mashupmedia.service;

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
import org.hibernate.Hibernate;
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
import org.mashupmedia.dao.GroupDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.exception.MashupMediaException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MusicLibraryUpdateManagerImpl implements MusicLibraryUpdateManager {
	private final int BATCH_INSERT_ITEMS = 20;

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private AlbumArtManager albumArtManager;

	@Autowired
	private MusicDao musicDao;

	@Autowired
	private PlaylistDao playlistDao;

	@Autowired
	private GroupDao groupDao;

	// @Autowired
	// private MusicManager musicManager;

	private MusicLibraryUpdateManagerImpl() {
		// Disable the jaudiotagger library logging
		LogManager.getLogManager().reset();
		java.util.logging.Logger globalLogger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
		globalLogger.setLevel(java.util.logging.Level.OFF);
	}

	protected void deleteObsoleteSongs(long libraryId, Date date) {
		List<Song> songsToDelete = musicDao.getSongsToDelete(libraryId, date);
		deleteSongs(songsToDelete);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = RuntimeException.class)
	public void updateLibrary(Library library) {
		if (library instanceof MusicLibrary) {
			MusicLibrary musicLibrary = (MusicLibrary) library;
			updateMusicLibrary(musicLibrary);
		}
	}

	public void updateMusicLibrary(MusicLibrary musicLibrary) {
		if (!musicLibrary.isEnabled()) {
			logger.info("Library is disabled, will not update:" + musicLibrary.toString());
			return;
		}

		try {
			prepareFileMusicLibrary(musicLibrary);
		} catch (Exception e) {
			throw new MashupMediaException("Error updating the music library.", e);
		}

	}

	@Override
	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		List<Long> groupIds = groupDao.getGroupIds();
		long libraryId = musicLibrary.getId();
		long totalSongsSaved = 0;

		for (int i = 0; i < songs.size(); i++) {
			Song song = songs.get(i);
			song.setLibrary(musicLibrary);

			String songPath = song.getPath();
			long songSizeInBytes = song.getSizeInBytes();
			Song savedSong = musicDao.getSong(groupIds, libraryId, songPath, songSizeInBytes);

			if (savedSong != null) {
				long savedSongId = savedSong.getId();
				song.setId(savedSongId);
				savedSong.setUpdatedOn(song.getUpdatedOn());
				musicDao.saveSong(savedSong);
				logger.info("Song is already in database, updated song date.");
				continue;
			}

			Artist artist = song.getArtist();
			artist = prepareArtist(groupIds, artist);

			Album album = song.getAlbum();
			if (StringUtils.isBlank(album.getName())) {
				logger.error("Unable to save song: " + song.toString());
				continue;
			}

			album.setArtist(artist);
			album = prepareAlbum(groupIds, album);

			AlbumArtImage albumArtImage = album.getAlbumArtImage();
			if (albumArtImage == null) {
				try {
					albumArtImage = albumArtManager.getAlbumArtImage(musicLibrary, song);
				} catch (Exception e) {
					logger.info("Error processing album image", e);
				}
			}

			album.setAlbumArtImage(albumArtImage);
			song.setAlbum(album);

			song.setArtist(artist);

			Year year = song.getYear();
			year = prepareYear(year);
			song.setYear(year);

			Genre genre = song.getGenre();
			genre = prepareGenre(genre);
			song.setGenre(genre);

			boolean isSessionFlush = false;
			if (i % BATCH_INSERT_ITEMS == 0 || i == (songs.size() - 1)) {
				isSessionFlush = true;
			}

			StringBuilder searchTextBuilder = new StringBuilder();
			if (artist != null) {
				searchTextBuilder.append(" " + artist.getIndexText());
			}

			if (album != null) {
				searchTextBuilder.append(" " + album.getIndexText());
			}

			if (genre != null) {
				searchTextBuilder.append(" " + genre.getName());
			}

			if (year != null) {
				searchTextBuilder.append(" " + year.getYear());
			}

			searchTextBuilder.append(" " + song.getTitle());
			String searchText = StringUtils.trimToEmpty(searchTextBuilder.toString());
			searchText = searchText.replaceAll("\\s*\\b", " ");
			searchText = StringHelper.normaliseTextForDatabase(searchText);
			song.setSearchText(searchText);

			musicDao.saveSong(song, isSessionFlush);
			totalSongsSaved++;

		}

		logger.info("Saved " + totalSongsSaved + " songs.");

	}

	private Genre prepareGenre(Genre genre) {
		if (genre == null || StringUtils.isBlank(genre.getName())) {
			return null;
		}

		String genreName = StringHelper.normaliseTextForDatabase(genre.getName());
		Genre savedGenre = musicDao.getGenre(genreName);
		if (savedGenre != null) {
			return savedGenre;
		}

		genre.setName(genreName);
		return genre;
	}

	private Year prepareYear(Year year) {
		if (year == null || year.getYear() == 0) {
			return null;
		}

		Year savedYear = musicDao.getYear(year.getYear());
		if (savedYear == null) {
			return year;
		}

		return savedYear;
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
		saveSongs(musicLibrary, songs);
		deleteObsoleteSongs(musicLibrary.getId(), date);
		// musicManager.saveSongs(musicLibrary, songs);

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

					saveSongs(musicLibrary, songs);
					songs = new ArrayList<Song>();
					// songs.clear();

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

				StringBuilder displayTitleBuilder = new StringBuilder();
				displayTitleBuilder.append(song.getDisplayTrackNumber());
				displayTitleBuilder.append(Song.TITLE_SEPERATOR);
				displayTitleBuilder.append(song.getTitle());

				String displayTitle = prepareDisplayTitle(song);
				song.setDisplayTitle(displayTitle);

				songs.add(song);

			}
		}
	}

	private String prepareDisplayTitle(Song song) {
		StringBuilder titleBuilder = new StringBuilder();
		if (song.isReadableTag()) {
			titleBuilder.append(song.getDisplayTrackNumber());
			titleBuilder.append(Song.TITLE_SEPERATOR);
			titleBuilder.append(song.getTitle());
			return titleBuilder.toString();
		}

		String title = StringUtils.trimToEmpty(song.getTitle());
		int dotIndex = title.lastIndexOf(".");
		if (dotIndex < 0) {
			return title;
		}

		title = title.substring(0, dotIndex);
		return title;
	}

	@Override
	public void deleteSongs(List<Song> songs) {
		playlistDao.deletePlaylistMediaItems(songs);

		musicDao.deleteSongs(songs);
		logger.info("Deleted " + songs.size() + " out of date songs.");

		deleteEmpty();
		logger.info("Cleaned library.");
	}

	protected Artist prepareArtist(List<Long> userGroupIds, Artist artist) {
		Artist savedArtist = musicDao.getArtist(userGroupIds, artist.getName());
		if (savedArtist != null) {
			return savedArtist;
		}

		String artistName = artist.getName();
		String artistSearchIndexLetter = StringHelper.getSearchIndexLetter(artistName);
		artist.setIndexLetter(artistSearchIndexLetter);
		String artistSearchIndexText = StringHelper.getSearchIndexText(artistName);
		artist.setIndexText(artistSearchIndexText);
		return artist;
	}

	protected Album prepareAlbum(List<Long> userGroupIds, Album album) {
		Artist artist = album.getArtist();
		String albumName = album.getName();
		if (StringUtils.isBlank(albumName)) {
			return null;
		}

		Album savedAlbum = musicDao.getAlbum(userGroupIds, artist.getName(), albumName);
		if (savedAlbum != null) {
			return savedAlbum;
		}

		String albumIndexLetter = StringHelper.getSearchIndexLetter(albumName);
		album.setIndexLetter(albumIndexLetter);
		String albumIndexText = StringHelper.getSearchIndexText(albumName);
		album.setIndexText(albumIndexText);

		return album;

	}

	@Override
	public void deleteEmpty() {
		List<Long> groupIds = groupDao.getGroupIds();

		List<Artist> artists = getArtists();
		for (Artist artist : artists) {
			List<Album> albums = musicDao.getAlbumsByArtist(groupIds, artist.getId());
			if (albums == null || albums.isEmpty()) {
				musicDao.deleteArtist(artist);
				continue;
			}

			for (Album album : albums) {
				List<Song> songs = musicDao.getSongs(groupIds, album.getId());
				if (songs == null || songs.isEmpty()) {
					musicDao.deleteAlbum(album);
				}
			}
		}
	}

	public List<Artist> getArtists() {
		List<Long> groupIds = groupDao.getGroupIds();
		List<Artist> artists = musicDao.getArtists(groupIds);
		for (Artist artist : artists) {
			Hibernate.initialize(artist.getAlbums());
		}
		return artists;
	}

}
