package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.mashupmedia.dao.GroupDao;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Song;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.repository.media.music.MusicAlbumRepository;
import org.mashupmedia.repository.media.music.SongRepository;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MusicLibraryUpdateManagerImpl implements MusicLibraryUpdateManager {
	private final int BATCH_INSERT_ITEMS = 20;


	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private AlbumArtManager albumArtManager;

//	@Autowired
//	private MapperManager mapperManager;

	@Autowired
	private MusicDao musicDao;

	@Autowired
	private PlaylistDao playlistDao;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private ProcessManager processManager;

	@Autowired
	private LibraryManager libraryManager;

	@Autowired
	private MediaDao mediaDao;

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private MusicAlbumRepository musicAlbumRepository;


	public MusicLibraryUpdateManagerImpl() {
		// Disable the jaudiotagger library logging
		// LogManager.getLogManager().reset();
		// java.util.logging.Logger globalLogger = java.util.logging.Logger
		// 		.getLogger(java.util.logging.log.GLOBAL_LOGGER_NAME);
		// globallog.setLevel(java.util.logging.Level.OFF);
	}

	@Override
	public void deleteObsoleteSongs(long libraryId, Date date) {
		List<Song> songsToDelete = musicDao.getSongsToDelete(libraryId, date);
		for (Song song : songsToDelete) {
			deleteSong(song);
		}

		log.info("Deleted or disabled " + songsToDelete.size() + " out of date songs.");
		if (songsToDelete.isEmpty()) {
			return;
		}

		deleteEmpty();
		log.info("Cleaned library.");
	}

	@Override
	public void deleteFile(MusicLibrary library, File file) {

		String path = file.getPath();
		Song song = musicDao.getSong(path);
		if (song == null) {
			return;
		}

		deleteSong(song);
		deleteEmpty();
	}

	private void deleteSong(Song song) {
		playlistDao.deletePlaylistMediaItem(song);
		musicDao.deleteObsoleteSong(song);
	}

	@Override
	public void updateLibrary(MusicLibrary library, File folder, Date date) {
		try {
			prepareMusicLibrary(library, folder, date);
		} catch (Exception e) {
			throw new MashupMediaRuntimeException("Error updating the music library.", e);
		}
	}

	@Override
	public void updateRemoteLibrary(MusicLibrary musicLibrary) throws Exception {
		Location location = musicLibrary.getLocation();
		String remoteLibraryUrl = location.getPath();
		String libraryXml = connectionManager.proceessRemoteLibraryConnection(remoteLibraryUrl);
//		mapperManager.saveXmltoSongs(musicLibrary, libraryXml);
	}

	@Override
	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs, Date date) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

//		List<Long> groupIds = getGroupIds();

		long libraryId = musicLibrary.getId();
		long totalSongsSaved = 0;

		for (int i = 0; i < songs.size(); i++) {
			Song song = songs.get(i);

			song.setLibrary(musicLibrary);

			String songPath = song.getPath();
			long fileLastModifiedOn = song.getFileLastModifiedOn();
//			Song savedSong = getSavedSong(groupIds, libraryId, songPath, fileLastModifiedOn);
			Optional<Song> optionalSong = songRepository.findByLibraryIdAndPathAndLastModifiedOn(libraryId, songPath, fileLastModifiedOn);

			if (optionalSong.isPresent()) {
				Song savedSong = optionalSong.get();
				long savedSongId = savedSong.getId();
				song.setId(savedSongId);
				savedSong.setUpdatedOn(song.getUpdatedOn());
				savedSong.setEnabled(true);
				musicDao.saveSong(savedSong, false);
				log.info("Song is already in database, updated song date.");				
//				writeSongToXml(libraryId, savedSong);
				// encodeMediaItemTaskManager.processMediaItemForEncodingDuringAutomaticUpdate(savedSong,
				// MediaContentType.MP3);
				continue;
			}

			String fileName = song.getFileName();
			String fileExtension = FileHelper.getFileExtension(fileName);

			MediaEncoding mediaEncoding = new MediaEncoding();
			mediaEncoding.setOriginal(true);
			MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(fileExtension);
			mediaEncoding.setMediaContentType(mediaContentType);
			Set<MediaEncoding> mediaEncodings = song.getMediaEncodings();
			if (mediaEncodings == null) {
				mediaEncodings = new HashSet<MediaEncoding>();
				song.setMediaEncodings(mediaEncodings);
			}
			mediaEncodings.add(mediaEncoding);

			Artist artist = song.getArtist();
			artist = prepareArtist(artist);

			Album album = song.getAlbum();
			if (StringUtils.isBlank(album.getName())) {
				log.error("Unable to save song: " + song.toString());
				continue;
			}

			album.setArtist(artist);
			album = prepareAlbum(album);
			album.setUpdatedOn(date);

			AlbumArtImage albumArtImage = album.getAlbumArtImage();
			if (albumArtImage == null) {
				try {
					albumArtImage = albumArtManager.getAlbumArtImage(musicLibrary, song);
				} catch (Exception e) {
					log.info("Error processing album image", e);
				}
			}

			album.setAlbumArtImage(albumArtImage);
			song.setAlbum(album);

			song.setCreatedOn(date);
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
//			writeSongToXml(libraryId, song);

			// encodeMediaItemTaskManager.processMediaItemForEncodingDuringAutomaticUpdate(song,
			// MediaContentType.MP3);

			totalSongsSaved++;

		}

		log.info("Saved " + totalSongsSaved + " songs.");

	}

	private List<Long> getGroupIds() {
		List<Long> groupIds = groupDao.getGroupIds();
		return groupIds;
	}

	private Song getSavedSong(List<Long> groupIds, long libraryId, String songPath, long fileLastModifiedOn) {
		Song savedSong = musicDao.getSong(groupIds, libraryId, songPath, fileLastModifiedOn);
		return savedSong;
	}

//	protected void writeSongToXml(long libraryId, Song song) {
//		try {
////			mapperManager.writeSongToXml(libraryId, song);
//		} catch (Exception e) {
//			log.error("Error writing song to xml", e);
//		}
//
//	}

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

	private void prepareMusicLibrary(MusicLibrary musicLibrary, File folder, Date date) throws Exception {
		List<Song> songs = new ArrayList<Song>();
		prepareSongs(date, songs, folder, musicLibrary, null, null);
	}

	@Override
	public void saveFile(MusicLibrary library, File file, Date date) {

		String fileName = file.getName();

		if (!FileHelper.isSupportedSong(fileName)) {
			log.info("File is not a supported format: " + fileName);
			return;
		}

		// Clean up
		List<MediaItem> duplicateMediaItems = mediaDao.getMediaItems(file.getPath());
		for (MediaItem duplicateMediaItem : duplicateMediaItems) {
			if (duplicateMediaItem.getMediaType().equals(MediaType.SONG)) {
				Song duplicateSong = (Song) duplicateMediaItem;
				deleteSong(duplicateSong);
			}
		}

		File libraryFolder = new File(library.getLocation().getPath());
		List<File> relativeFolders = LibraryHelper.getRelativeFolders(libraryFolder, file);

		String folderArtistName = getFolderArtist(relativeFolders, file);
		String folderAlbumName = getFolderAlbum(relativeFolders, file);
		Song song = prepareSong(file, date, 1, library, folderArtistName, folderAlbumName);

		List<Song> songs = new ArrayList<>();
		songs.add(song);
		saveSongs(library, songs, date);
	}

	private String getFolderAlbum(List<File> relativeFolders, File musicFile) {
		String musicFileName = musicFile.getName();
		if (relativeFolders == null || relativeFolders.isEmpty()) {
			return musicFileName;
		}

		if (relativeFolders.size() < 2) {
			return musicFileName;
		}

		StringBuilder albumNameBuilder = new StringBuilder();
		for (File relativeFolder : relativeFolders) {
			if (albumNameBuilder.length() > 0) {
				albumNameBuilder.append(" - ");
			}
			albumNameBuilder.append(relativeFolder.getName());
		}

		return albumNameBuilder.toString();
	}

	private String getFolderArtist(List<File> relativeFolders, File musicFile) {
		String musicFileName = musicFile.getName();
		if (relativeFolders == null || relativeFolders.isEmpty()) {
			return musicFileName;
		}

		File artistFolder = relativeFolders.get(0);
		return artistFolder.getName();
	}

	protected void prepareSongs(Date date, List<Song> songs, File file, MusicLibrary musicLibrary,
			String folderArtistName, String folderAlbumName)
			throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {

		int musicFileCount = 0;

		String fileName = StringUtils.trimToEmpty(file.getName());

		if (file.isDirectory()) {
			musicFileCount = 0;
			folderArtistName = StringUtils.trimToEmpty(folderArtistName);

			if (StringUtils.isEmpty(folderArtistName)) {
				folderArtistName = fileName;
				File[] files = file.listFiles();
				for (File childFile : files) {
					prepareSongs(date, songs, childFile, musicLibrary, folderArtistName, folderAlbumName);
				}
				saveSongs(musicLibrary, songs, date);
				songs.clear();
				libraryManager.saveMediaItemLastUpdated(musicLibrary.getId());
				folderArtistName = "";
			} else {
				if (StringUtils.isBlank(folderAlbumName)) {
					folderAlbumName = fileName;
				} else {
					folderAlbumName += " - " + fileName;
				}
				File[] files = file.listFiles();
				for (File childFile : files) {
					prepareSongs(date, songs, childFile, musicLibrary, folderArtistName, folderAlbumName);
				}
				folderAlbumName = "";
			}

		}

		if (FileHelper.isSupportedSong(fileName)) {
			musicFileCount++;
			Song song = prepareSong(file, date, musicFileCount, musicLibrary, folderArtistName, folderAlbumName);
			songs.add(song);

		}
	}

	private Song prepareSong(File file, Date date, int musicFileCount, MusicLibrary musicLibrary,
			String folderArtistName, String folderAlbumName) {

		String fileName = file.getName();

		Tag tag = null;
		long bitRate = 0;
		String format = FileHelper.getFileExtension(fileName);
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
			log.info("prepare song", e);
		}

		if (tag != null) {
			tagSongTitle = StringUtils.trimToEmpty(tag.getFirst(FieldKey.TITLE));
			trackNumber = NumberUtils.toInt(tag.getFirst(FieldKey.TRACK));
			tagArtistName = StringUtils.trimToEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST));
			tagAlbumName = processAlbumName(tag);
			genreValue = StringUtils.trimToEmpty(tag.getFirst(FieldKey.GENRE));
			yearValue = NumberUtils.toInt(tag.getFirst(FieldKey.YEAR));
		} else {
			log.info("Unable to read tag info for music file: " + file.getAbsolutePath());
		}

		Song song = new Song();
		song.setUpdatedOn(date);

		if (trackNumber == 0) {
			trackNumber = processTrackNumber(fileName, musicFileCount);
			// trackNumber = musicFileCount;
		}
		song.setTrackNumber(trackNumber);

		if (StringUtils.isEmpty(tagSongTitle)) {
			tagSongTitle = file.getName();
		} else {
			log.debug("Found song title for music file: " + file.getAbsolutePath());
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
		song.setFileLastModifiedOn(file.lastModified());

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
			log.info("Unable to add song to the library. No album found for artist = " + folderArtistName
					+ ", song title = " + tagSongTitle);
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
		artist.setAlbums(new ArrayList<Album>());
		album.setArtist(artist);
		song.setArtist(artist);

		StringBuilder displayTitleBuilder = new StringBuilder();
		displayTitleBuilder.append(song.getDisplayTrackNumber());
		displayTitleBuilder.append(Song.TITLE_SEPERATOR);
		displayTitleBuilder.append(song.getTitle());

		String displayTitle = prepareDisplayTitle(song);
		song.setDisplayTitle(displayTitle);

		return song;
	}

	private int processTrackNumber(String fileName, int musicFileCount) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(fileName);
		String trackNumberValue = null;		
		try {
			trackNumberValue = matcher.group(1);
		} catch (IllegalStateException e) {
			log.info("Unable to get track number from file name: " + fileName, e);
		}
		
		int trackNumber = NumberUtils.toInt(trackNumberValue, musicFileCount);
		return trackNumber;
	}

	private String processAlbumName(Tag tag) {
		String albumName = StringUtils.trimToEmpty(tag.getFirst(FieldKey.ALBUM));
		if (StringUtils.isEmpty(albumName)) {
			return null;
		}

		int discNumber = NumberUtils.toInt(tag.getFirst(FieldKey.DISC_NO));
		// Only use the disc number if greater than 1
		if (discNumber < 2) {
			return albumName;
		}

		albumName += " CD" + discNumber;
		return albumName;
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

		for (Song song : songs) {
			processManager.killProcesses(song.getId());
			playlistDao.deletePlaylistMediaItem(song);
			musicDao.deleteSong(song);
		}

		// playlistDao.deletePlaylistMediaItems(songs);
		// musicDao.deleteSongs(songs);

		log.info("Deleted " + songs.size() + " out of date songs.");

		deleteEmpty();
		log.info("Cleaned library.");
	}

	private Artist prepareArtist(Artist artist) {
//		Artist savedArtist = musicDao.getArtist(userGroupIds, artist.getName());
		Optional<Artist> artistOptional = artistRepository.findArtistByNameIgnoreCase(artist.getName());

		if (artistOptional.isPresent()) {
			return artistOptional.get();
		}

		String artistName = artist.getName();
		String artistSearchIndexLetter = StringHelper.getSearchIndexLetter(artistName);
		artist.setIndexLetter(artistSearchIndexLetter);
		String artistSearchIndexText = StringHelper.getSearchIndexText(artistName);
		artist.setIndexText(artistSearchIndexText);
		return artist;
	}

	private Album prepareAlbum(Album album) {
		Artist artist = album.getArtist();
		String albumName = album.getName();
		if (StringUtils.isBlank(albumName)) {
			return null;
		}

		String url = null;
		String thumbnailUrl = null;
		AlbumArtImage albumArtImage = album.getAlbumArtImage();
		if (albumArtImage != null) {
			url = albumArtImage.getUrl();
			thumbnailUrl = albumArtImage.getThumbnailUrl();
		}

//		Album savedAlbum = musicDao.getAlbum(userGroupIds, artist.getName(), albumName);
		Optional<Album> optionalAlbum = musicAlbumRepository.findByArtistNameAndAlbumNameIgnoreCase(artist.getName(), albumName);

		if (optionalAlbum.isPresent()) {
			Album savedAlbum = optionalAlbum.get();
			AlbumArtImage savedAlbumArtImage = savedAlbum.getAlbumArtImage();
			if (savedAlbumArtImage == null) {
				savedAlbum.setAlbumArtImage(albumArtImage);
			} else {
				if (StringUtils.isBlank(savedAlbumArtImage.getUrl())) {
					savedAlbumArtImage.setUrl(url);
					savedAlbumArtImage.setThumbnailUrl(thumbnailUrl);
				}
			}

			return savedAlbum;
		}

		String albumIndexLetter = StringHelper.getSearchIndexLetter(albumName);
		album.setIndexLetter(albumIndexLetter);
		String albumIndexText = StringHelper.getSearchIndexText(albumName);
		album.setIndexText(albumIndexText);
		album.setAlbumArtImage(albumArtImage);

		return album;

	}

	@Override
	public void deleteEmpty() {
		musicDao.deleteEmptyAlbums();
		musicDao.deleteEmptyArtists();
	}

}
