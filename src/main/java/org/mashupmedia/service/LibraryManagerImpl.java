package org.mashupmedia.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.mashupmedia.controller.configuration.LibraryHelper;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.RemoteShare;
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
		User user = SecurityHelper.getLoggedInUser();
		if (user == null) {
			logger.error("No user found in session, exiting...");
			return;
		}

		Date date = new Date();
		long libraryId = library.getId();

		if (libraryId == 0) {
			library.setCreatedBy(user);
			library.setCreatedOn(date);
		}

		library.setUpdatedBy(user);
		library.setUpdatedOn(date);

		Set<RemoteShare> remoteShares = library.getRemoteShares();
		if (remoteShares != null) {
			for (RemoteShare remoteShare : remoteShares) {
				String uniqueName = remoteShare.getUniqueName();
				if (StringUtils.isBlank(uniqueName)) {
					remoteShare.setUniqueName(LibraryHelper.createUniqueName());
				}
				remoteShare.setCreatedBy(user);
				remoteShare.setCreatedOn(new Date());
			}
		}

		libraryDao.saveLibrary(library);

	}

	@Override
	public Library getLibrary(long id) {
		Library library = libraryDao.getLibrary(id);
		Hibernate.initialize(library.getRemoteShares());
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
