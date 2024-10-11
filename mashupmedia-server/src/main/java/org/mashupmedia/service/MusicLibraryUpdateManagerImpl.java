package org.mashupmedia.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.mashupmedia.dao.MediaDao;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.account.User;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaResource;
import org.mashupmedia.model.media.MetaImage;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.MetaTrack;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.repository.media.MediaRepository;
import org.mashupmedia.repository.media.MediaResourceRepository;
import org.mashupmedia.repository.media.music.ArtistRepository;
import org.mashupmedia.repository.media.music.MusicAlbumRepository;
import org.mashupmedia.service.media.audio.AudioMetaManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MetaEntityHelper;
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
	private final AlbumArtManager albumArtManager;
	private final MusicDao musicDao;
	private final PlaylistDao playlistDao;
	// private final LibraryManager libraryManager;
	private final MediaDao mediaDao;
	private final ArtistRepository artistRepository;
	private final MusicAlbumRepository musicAlbumRepository;
	private final MediaRepository mediaRepository;
	private final MediaResourceRepository mediaResourceRepository;
	private final AudioMetaManager audioMetaManager;

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
		throw new UnsupportedOperationException("Not implemented, updateRemoteLibrary");
		// Location location = musicLibrary.getLocation();
		// String remoteLibraryUrl = location.getPath();
		// String libraryXml =
		// connectionManager.proceessRemoteLibraryConnection(remoteLibraryUrl);
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

			Artist artist = track.getArtist();
			User user = musicLibrary.getUser();
			artist = prepareArtist(artist, user.getId());

			Album album = track.getAlbum();
			if (StringUtils.isBlank(album.getName())) {
				log.error("Unable to save track: " + track.toString());
				continue;
			}

			album.setArtist(artist);
			album = prepareAlbum(album);
			album.setUpdatedOn(date);

			MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
			MetaImage metaImage = metaImageHelper.getDefaultEntity(album.getMetaImages());
			if (metaImage == null) {
				try {
					metaImage = albumArtManager.getMetaImage(musicLibrary, track);
				} catch (Exception e) {
					log.info("Error processing album image", e);
				}
			}

			Set<MetaImage> metaImages = metaImageHelper.addMetaEntity(metaImage, album.getMetaImages());
			album.setMetaImages(metaImages);
			track.setAlbum(album);

			track.setCreatedOn(date);
			Genre genre = track.getGenre();
			genre = prepareGenre(genre);
			track.setGenre(genre);

			boolean isSessionFlush = false;
			if (i % BATCH_INSERT_ITEMS == 0 || i == (tracks.size() - 1)) {
				isSessionFlush = true;
			}

			musicDao.saveTrack(track, isSessionFlush);
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

	private void prepareMusicLibrary(MusicLibrary musicLibrary, File folder, Date date) throws Exception {
		List<Track> tracks = new ArrayList<Track>();
		prepareTracks(date, tracks, folder, musicLibrary, null, null);
	}

	@Override
	public void saveFile(MusicLibrary library, File file, Date date) {

		if (!FileHelper.isSupportedTrack(file.toPath())) {
			log.info("File is not a supported format: " + file.getAbsolutePath());
			return;
		}

		// Clean up
		List<MediaItem> duplicateMediaItems = mediaDao.getMediaItems(file.getPath());
		for (MediaItem duplicateMediaItem : duplicateMediaItems) {
			if (duplicateMediaItem.getMashupMediaType().equals(MashupMediaType.MUSIC)) {
				Track duplicateTrack = (Track) duplicateMediaItem;
				deleteTrack(duplicateTrack);
			}
		}

		File libraryFolder = new File(library.getPath());
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

		if (!FileHelper.isSupportedTrack(file.toPath())) {
			return;
		}
		
		Optional<MediaItem> optionalMediaItem = getSavedMediaItem(file);
		if (optionalMediaItem.isPresent()) {
			MediaItem mediaItem = optionalMediaItem.get();
			mediaItem.setUpdatedOn(new Date());
			mediaRepository.save(mediaItem);
			return;
		}

		musicFileCount++;
		Track track = prepareTrack(file, date, musicFileCount, musicLibrary, folderArtistName, folderAlbumName);
		if (track != null) {
			tracks.add(track);
		}

	}

	private Optional<MediaItem> getSavedMediaItem(File file) {
		Optional<MediaResource> optionalMediaResource = mediaResourceRepository.findByPath(file.getAbsolutePath());
		if (optionalMediaResource.isEmpty()) {
			return Optional.empty();
		}

		MediaResource mediaResource = optionalMediaResource.get();
		return file.lastModified() == mediaResource.getFileLastModifiedOn()
				? Optional.of(mediaResource.getMediaItem())
				: Optional.empty();
	}

	private Track prepareTrack(File file, Date date, int musicFileCount, MusicLibrary musicLibrary,
			String folderArtistName, String folderAlbumName) {

		String fileName = file.getName();
		long bitRate = 0;
		String format = FileHelper.getFileExtension(fileName);

		MetaTrack metaTrack = audioMetaManager.getMetaTrack(file.toPath());

		Track track = new Track();
		track.setUpdatedOn(date);

		int trackNumber = metaTrack.getNumber();
		if (trackNumber == 0) {
			trackNumber = processTrackNumber(fileName, musicFileCount);
		}
		track.setTrackNumber(trackNumber);
		track.setTitle(metaTrack.getTitle());
		track.setTrackLength(metaTrack.getLength());
		track.setBitRate(bitRate);
		track.setFileName(file.getName());
		track.setLibrary(musicLibrary);
		track.setEnabled(true);
		track.setTrackYear(metaTrack.getYear());
		track.setGenre(metaTrack.getGenre());

		MediaResource mediaResource = MediaResource.builder()
				.mediaItem(track)
				.original(true)
				.mediaContentType(MediaContentType.getMediaContentType(format))
				.path(file.getAbsolutePath())
				.sizeInBytes(file.length())
				.fileLastModifiedOn(file.lastModified())
				.build();
		track.getMediaResources().add(mediaResource);

		Album album = new Album();
		album.setName(metaTrack.getAlbum());
		if (StringUtils.isBlank(album.getName())) {
			log.info("Unable to add track to the library: " + metaTrack.toString());
			// return;
		}

		track.setAlbum(album);

		Artist artist = new Artist();
		artist.setName(metaTrack.getArtist());
		User user = musicLibrary.getUser();
		artist.setUser(user);
		artist.setAlbums(new ArrayList<Album>());
		album.setArtist(artist);
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

	private Artist prepareArtist(Artist artist, long userId) {
		return artistRepository
				.findArtistByNameIgnoreCase(artist.getName(), userId)
				.orElse(artist);
	}

	private Album prepareAlbum(Album album) {
		Artist artist = album.getArtist();
		String albumName = album.getName();
		if (StringUtils.isBlank(albumName)) {
			return null;
		}

		MetaEntityHelper<MetaImage> metaImageHelper = new MetaEntityHelper<>();
		MetaImage metaImage = metaImageHelper.getDefaultEntity(album.getMetaImages());

		Optional<Album> optionalAlbum = musicAlbumRepository.findByArtistNameAndAlbumNameIgnoreCase(artist.getName(),
				albumName);

		if (metaImage == null) {
			return optionalAlbum.orElse(album);
		}

		if (optionalAlbum.isPresent()) {
			Album savedAlbum = optionalAlbum.get();
			MetaImage savedMetaImage = metaImageHelper.getDefaultEntity(savedAlbum.getMetaImages());
			if (savedMetaImage == null) {
				Set<MetaImage> metaImages = metaImageHelper.addMetaEntity(metaImage, savedAlbum.getMetaImages());
				savedAlbum.setMetaImages(metaImages);
			} else {
				savedMetaImage.setUrl(metaImage.getUrl());
				savedMetaImage.setThumbnailUrl(metaImage.getThumbnailUrl());
			}

			return savedAlbum;
		}

		Set<MetaImage> metaImages = metaImageHelper.addMetaEntity(metaImage, album.getMetaImages());
		album.setMetaImages(metaImages);

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
