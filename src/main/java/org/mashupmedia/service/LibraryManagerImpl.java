package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LibraryManagerImpl implements LibraryManager {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private MediaManager mediaManager;
	@Autowired
	private LibraryDao libraryDao;
	@Autowired
	private MusicManager musicManager;
	@Autowired
	private PlaylistManager playlistManager;
	@Autowired
	private VoteManager voteManager;
	@Autowired
	private MusicLibraryUpdateManager musicLibraryUpdateManager;

	@Override
	public List<Library> getLibraries(LibraryType libraryType) {
		List<Library> musicLibraries = libraryDao.getLibraries(libraryType);
		return musicLibraries;
	}

	@Override
	public List<Library> getLibrariesForGroup(long groupId) {
		List<Library> musicLibraries = libraryDao.getLibrariesForGroup(groupId);
		return musicLibraries;
	}
	
	@Override
	public void saveLibrary(Library library) {
		prepareLocation(library);
		User user = SecurityHelper.getLoggedInUser();
		if (user == null) {
			logger.error("No user found in session, exiting...");
			return;
		}

		Date date = new Date();
		if (library.getId() == 0) {
			library.setCreatedBy(user);
			library.setCreatedOn(date);
		}

		library.setUpdatedBy(user);
		library.setUpdatedOn(date);

		libraryDao.saveLibrary(library);

	}

	protected void prepareLocation(Library library) {
		if (library.getId() == 0) {
			return;
		}

		Location location = library.getLocation();
		if (location == null) {
			return;
		}

		if (!(location instanceof FtpLocation)) {
			return;
		}

		FtpLocation ftpLocation = (FtpLocation) location;
		String password = ftpLocation.getPassword();
		if (StringUtils.isNotBlank(password)) {
			return;
		}

		long id = library.getId();
		Library savedLibrary = getLibrary(id);
		Location savedLocation = savedLibrary.getLocation();
		if (!(savedLocation instanceof FtpLocation)) {
			return;
		}

		FtpLocation savedFtpLocation = (FtpLocation) savedLocation;
		password = savedFtpLocation.getPassword();
		ftpLocation.setPassword(password);
		library.setLocation(ftpLocation);
	}

	@Override
	public Library getLibrary(long id) {
		Library library = libraryDao.getLibrary(id);
		return library;
	}

	@Override
	public void deleteLibrary(Library library) {
		long id = library.getId();

		List<MediaItem> mediaItems = mediaManager.getMediaItemsForLibrary(id);
		if (mediaItems == null || mediaItems.isEmpty()) {
			return;
		}

		deleteVotes(mediaItems);
		playlistManager.deleteLibrary(library.getId());

		MediaType mediaType = mediaItems.get(0).getMediaType();
		if (mediaType == MediaType.SONG) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<Song> songs = (List) mediaItems;
			musicLibraryUpdateManager.deleteSongs(songs);
			List<AlbumArtImage> albumArtImages = mediaManager.getAlbumArtImages(id);
			mediaManager.deleteAlbumArtImages(albumArtImages);
			musicLibraryUpdateManager.deleteEmpty();
		} else {
			mediaManager.deleteMediaItems(mediaItems);
		}

		libraryDao.deleteLibrary(library);
		FileHelper.deleteLibrary(id);
	}

	private void deleteVotes(List<MediaItem> mediaItems) {
		for (MediaItem mediaItem : mediaItems) {
			voteManager.deleteVotesForMediaItem(mediaItem.getId());
		}
	}

}
