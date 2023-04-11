package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.encode.ProcessManager;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaEncoding;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MashupMediaType;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.MediaRepository;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.repository.media.music.MusicAlbumRepository;
import org.mashupmedia.repository.playlist.PlaylistMediaItemRepository;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.mashupmedia.util.StringHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MusicLibraryUpdateManagerImpl implements MusicLibraryUpdateManager {
	private final int BATCH_INSERT_ITEMS = 20;

	private final ConnectionManager connectionManager;

	private final AlbumArtManager albumArtManager;

	// @Autowired
	// private MapperManager mapperManager;

	private final MusicDao musicDao;

	private final PlaylistDao playlistDao;

	private final ProcessManager processManager;

	private final LibraryManager libraryManager;

	private final MediaDao mediaDao;

	private final ArtistRepository artistRepository;

	private final MusicAlbumRepository musicAlbumRepository;

	private final MediaRepository mediaRepository;

	private final PlaylistMediaItemRepository playlistMediaItemRepository;

	@PostConstruct
	private void postConstruct() {
		Logger[] loggers = new Logger[] { Logger.getLogger("org.jaudiotagger") };

		for (Logger logger : loggers) {
			logger.setLevel(Level.OFF);
		}

	}


	@Override
	public void deleteObsoleteTracks(long libraryId, Date date) {
		List<Track> tracksToDelete = musicDao.getTracksToDelete(libraryId, date);
		for (Track track : tracksToDelete) {
			deleteTrack(track);
		}

		log.info("Deleted or disabled " + tracksToDelete.size() + " out of date tracks.");
		if (tracksToDelete.isEmpty()) {
			return;
		}

		cleanUp();
		log.info("Cleaned library.");
	}

	@Override
	public void deleteFile(MusicLibrary library, File file) {

		String path = file.getPath();
		Track track = musicDao.getTrack(path);
		if (track == null) {
			return;
		}

		deleteTrack(track);
		cleanUp();
	}

	private void deleteTrack(Track track) {
		playlistDao.deletePlaylistMediaItem(track);
		mediaRepository.delete(track);
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
		// mapperManager.saveXmltoTracks(musicLibrary, libraryXml);
	}

	@Override
	public void saveTracks(MusicLibrary musicLibrary, List<Track> tracks, Date date) {
		if (tracks == null || tracks.isEmpty()) {
			return;
		}

		// List<Long> groupIds = getGroupIds();

		// long libraryId = musicLibrary.getId();
		long totalTracksSaved = 0;

		for (int i = 0; i < tracks.size(); i++) {
			Track track = tracks.get(i);

			track.setLibrary(musicLibrary);

			// String trackPath = track.getPath();
			// long fileLastModifiedOn = track.getFileLastModifiedOn();
			// // Track savedTrack = getSavedTrack(groupIds, libraryId, trackPath,
			// // fileLastModifiedOn);
			// Optional<Track> optionalTrack =
			// trackRepository.findByLibraryIdAndPathAndLastModifiedOn(libraryId, trackPath,
			// fileLastModifiedOn);

			// if (optionalTrack.isPresent()) {
			// Track savedTrack = optionalTrack.get();
			// long savedTrackId = savedTrack.getId();
			// track.setId(savedTrackId);
			// savedTrack.setUpdatedOn(track.getUpdatedOn());
			// savedTrack.setEnabled(true);
			// musicDao.saveTrack(savedTrack, false);
			// log.info("Track is already in database, updated track date.");
			// // writeTrackToXml(libraryId, savedTrack);
			// //
			// encodeMediaItemTaskManager.processMediaItemForEncodingDuringAutomaticUpdate(savedTrack,
			// // MediaContentType.MP3);
			// continue;
			// }

			String fileName = track.getFileName();
			String fileExtension = FileHelper.getFileExtension(fileName);

			MediaEncoding mediaEncoding = new MediaEncoding();
			mediaEncoding.setOriginal(true);
			MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(fileExtension);
			mediaEncoding.setMediaContentType(mediaContentType);
			Set<MediaEncoding> mediaEncodings = track.getMediaEncodings();
			if (mediaEncodings == null) {
				mediaEncodings = new HashSet<MediaEncoding>();
				track.setMediaEncodings(mediaEncodings);
			}
			mediaEncodings.add(mediaEncoding);

			Artist artist = track.getArtist();
			artist = prepareArtist(artist);

			Album album = track.getAlbum();
			if (StringUtils.isBlank(album.getName())) {
				log.error("Unable to save track: " + track.toString());
				continue;
			}

			album.setArtist(artist);
			album = prepareAlbum(album);
			album.setUpdatedOn(date);

			AlbumArtImage albumArtImage = album.getAlbumArtImage();
			if (albumArtImage == null) {
				try {
					albumArtImage = albumArtManager.getAlbumArtImage(musicLibrary, track);
				} catch (Exception e) {
					log.info("Error processing album image", e);
				}
			}

			album.setAlbumArtImage(albumArtImage);
			track.setAlbum(album);

			track.setCreatedOn(date);
			track.setArtist(artist);

			Year year = track.getYear();
			year = prepareYear(year);
			track.setYear(year);

			Genre genre = track.getGenre();
			genre = prepareGenre(genre);
			track.setGenre(genre);

			boolean isSessionFlush = false;
			if (i % BATCH_INSERT_ITEMS == 0 || i == (tracks.size() - 1)) {
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

			searchTextBuilder.append(" " + track.getTitle());
			String searchText = StringUtils.trimToEmpty(searchTextBuilder.toString());
			searchText = searchText.replaceAll("\\s*\\b", " ");
			searchText = StringHelper.normaliseTextForDatabase(searchText);
			track.setSearchText(searchText);

			musicDao.saveTrack(track, isSessionFlush);
			// writeTrackToXml(libraryId, track);

			// encodeMediaItemTaskManager.processMediaItemForEncodingDuringAutomaticUpdate(track,
			// MediaContentType.MP3);

			totalTracksSaved++;

		}

		log.info("Saved " + totalTracksSaved + " tracks.");

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

	private void prepareMusicLibrary(MusicLibrary musicLibrary, File folder, Date date) throws Exception {
		List<Track> tracks = new ArrayList<Track>();
		prepareTracks(date, tracks, folder, musicLibrary, null, null);
	}

	@Override
	public void saveFile(MusicLibrary library, File file, Date date) {

		String fileName = file.getName();

		if (!FileHelper.isSupportedTrack(fileName)) {
			log.info("File is not a supported format: " + fileName);
			return;
		}

		// Clean up
		List<MediaItem> duplicateMediaItems = mediaDao.getMediaItems(file.getPath());
		for (MediaItem duplicateMediaItem : duplicateMediaItems) {
			if (duplicateMediaItem.getMashupMediaType().equals(MashupMediaType.TRACK)) {
				Track duplicateTrack = (Track) duplicateMediaItem;
				deleteTrack(duplicateTrack);
			}
		}

		File libraryFolder = new File(library.getLocation().getPath());
		List<File> relativeFolders = LibraryHelper.getRelativeFolders(libraryFolder, file);

		String folderArtistName = getFolderArtist(relativeFolders, file);
		String folderAlbumName = getFolderAlbum(relativeFolders, file);
		Track track = prepareTrack(file, date, 1, library, folderArtistName, folderAlbumName);

		List<Track> tracks = new ArrayList<>();
		tracks.add(track);
		saveTracks(library, tracks, date);
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

	protected void prepareTracks(Date date, List<Track> tracks, File file, MusicLibrary musicLibrary,
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
					prepareTracks(date, tracks, childFile, musicLibrary, folderArtistName, folderAlbumName);
				}
				saveTracks(musicLibrary, tracks, date);
				tracks.clear();
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
					prepareTracks(date, tracks, childFile, musicLibrary, folderArtistName, folderAlbumName);
				}
				folderAlbumName = "";
			}

		}

		if (!FileHelper.isSupportedTrack(fileName)) {
			return;
		}

		Optional<Date> optionalFileLastModifiedOn = mediaRepository.findFileLastModifiedOnByPath(file.getPath());
		if (optionalFileLastModifiedOn.isPresent()) {
			Optional<MediaItem> optionalMediaItem = mediaRepository.findByPath(file.getPath());
			if (optionalMediaItem.isPresent()) {
				MediaItem savedMediaItem = optionalMediaItem.get();
				if (file.lastModified() == savedMediaItem.getFileLastModifiedOn()) {
					savedMediaItem.setUpdatedOn(date);
					mediaRepository.save(savedMediaItem);
					return;
				}
			}
		}

		musicFileCount++;
		Track track = prepareTrack(file, date, musicFileCount, musicLibrary, folderArtistName, folderAlbumName);
		tracks.add(track);

	}

	private Track prepareTrack(File file, Date date, int musicFileCount, MusicLibrary musicLibrary,
			String folderArtistName, String folderAlbumName) {

		String fileName = file.getName();

		Tag tag = null;
		long bitRate = 0;
		String format = FileHelper.getFileExtension(fileName);
		int trackLength = 0;
		String tagTrackTitle = null;
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
			log.info("prepare track", e);
		}

		if (tag != null) {
			tagTrackTitle = StringUtils.trimToEmpty(tag.getFirst(FieldKey.TITLE));
			trackNumber = NumberUtils.toInt(tag.getFirst(FieldKey.TRACK));
			tagArtistName = StringUtils.trimToEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST));
			tagAlbumName = processAlbumName(tag);
			genreValue = StringUtils.trimToEmpty(tag.getFirst(FieldKey.GENRE));
			yearValue = NumberUtils.toInt(tag.getFirst(FieldKey.YEAR));
		} else {
			log.info("Unable to read tag info for music file: " + file.getAbsolutePath());
		}

		Track track = new Track();
		track.setUpdatedOn(date);

		if (trackNumber == 0) {
			trackNumber = processTrackNumber(fileName, musicFileCount);
			// trackNumber = musicFileCount;
		}
		track.setTrackNumber(trackNumber);

		if (StringUtils.isEmpty(tagTrackTitle)) {
			tagTrackTitle = file.getName();
		} else {
			log.debug("Found track title for music file: " + file.getAbsolutePath());
			track.setReadableTag(true);
		}

		track.setTitle(tagTrackTitle);
		track.setFormat(format);
		track.setTrackLength(trackLength);
		track.setBitRate(bitRate);
		track.setFileName(file.getName());
		track.setPath(file.getAbsolutePath());
		track.setLibrary(musicLibrary);
		track.setSizeInBytes(file.length());
		track.setFileLastModifiedOn(file.lastModified());
		track.setEnabled(true);

		if (yearValue > 0) {
			Year year = new Year();
			year.setYear(yearValue);
			track.setYear(year);
		}

		if (StringUtils.isNotEmpty(genreValue)) {
			Genre genre = new Genre();
			genre.setName(genreValue);
			track.setGenre(genre);
		}

		Album album = new Album();
		if (StringUtils.isEmpty(tagAlbumName)) {
			tagAlbumName = folderAlbumName;
		}
		if (StringUtils.isBlank(folderAlbumName)) {
			log.info("Unable to add track to the library. No album found for artist = " + folderArtistName
					+ ", track title = " + tagTrackTitle);
		}

		album.setName(tagAlbumName);
		album.setFolderName(folderAlbumName);
		track.setAlbum(album);

		Artist artist = new Artist();
		if (StringUtils.isEmpty(tagArtistName)) {
			tagArtistName = folderArtistName;
		}
		artist.setName(tagArtistName);
		artist.setFolderName(folderArtistName);
		artist.setAlbums(new ArrayList<Album>());
		album.setArtist(artist);
		track.setArtist(artist);

		StringBuilder displayTitleBuilder = new StringBuilder();
		displayTitleBuilder.append(track.getDisplayTrackNumber());
		displayTitleBuilder.append(Track.TITLE_SEPERATOR);
		displayTitleBuilder.append(track.getTitle());

		String displayTitle = prepareDisplayTitle(track);
		track.setDisplayTitle(displayTitle);

		return track;
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

	private String prepareDisplayTitle(Track track) {
		StringBuilder titleBuilder = new StringBuilder();
		if (track.isReadableTag()) {
			titleBuilder.append(track.getDisplayTrackNumber());
			titleBuilder.append(Track.TITLE_SEPERATOR);
			titleBuilder.append(track.getTitle());
			return titleBuilder.toString();
		}

		String title = StringUtils.trimToEmpty(track.getTitle());
		int dotIndex = title.lastIndexOf(".");
		if (dotIndex < 0) {
			return title;
		}

		title = title.substring(0, dotIndex);
		return title;
	}

	// @Override
	// public void deleteTracks(List<Track> tracks) {

	// 	for (Track track : tracks) {
	// 		long trackId = track.getId();
	// 		processManager.killProcesses(trackId);
	// 		List<PlaylistMediaItem> playlistMediaItems = playlistMediaItemRepository.findByMediaItemId(trackId);
	// 		playlistMediaItemRepository.deleteAll(playlistMediaItems);
	// 		mediaRepository.delete(track);
	// 	}

	// 	// playlistDao.deletePlaylistMediaItems(tracks);
	// 	// musicDao.deleteTracks(tracks);

	// 	log.info("Deleted " + tracks.size() + " out of date tracks.");

	// 	cleanUp();
	// 	log.info("Cleaned library.");
	// }

	private Artist prepareArtist(Artist artist) {
		// Artist savedArtist = musicDao.getArtist(userGroupIds, artist.getName());
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

		log.debug("prepareAlbum name: " + album.getName());
		log.debug("prepareAlbum folderName: " + album.getFolderName());

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

		// Album savedAlbum = musicDao.getAlbum(userGroupIds, artist.getName(),
		// albumName);
		Optional<Album> optionalAlbum = musicAlbumRepository.findByArtistNameAndAlbumNameIgnoreCase(artist.getName(),
				albumName);

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
	public void cleanUp() {
		List<Album> albums = musicAlbumRepository.findAlbumsWithNoTracks();
		log.info("Found " + albums.size() + " albums to delete");
		musicAlbumRepository.deleteAll(albums);

		List<Artist> artists = artistRepository.findAristsWithNoAlbums();
		log.info("Found " + artists.size() + " artists to delete");
		artistRepository.deleteAll(artists);
	}

}
