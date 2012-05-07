package org.mashupmedia.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.mashupmedia.dao.LibraryDao;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Media;
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

	@Override
	public List<MusicLibrary> getMusicLibraries() {
		List<MusicLibrary> musicLibraries = libraryDao.getMusicLibraries();
		return musicLibraries;
	}

	@Override
	public void saveMusicLibrary(MusicLibrary musicLibrary) {

		prepareLocation(musicLibrary);
		
		User user = SecurityHelper.getLoggedInUser();
		if (user == null) {
			logger.error("No user found in session, exiting...");
		}

		Date date = new Date();
		if (musicLibrary.getId() == 0) {
			musicLibrary.setCreatedBy(user);
			musicLibrary.setCreatedOn(date);
		} 

		musicLibrary.setUpdatedBy(user);
		musicLibrary.setUpdatedOn(date);

		libraryDao.saveMusicLibrary(musicLibrary);
	}

	protected void prepareLocation(MusicLibrary musicLibrary) {
		if (musicLibrary.getId() == 0) {
			return;
		}
		
		Location location = musicLibrary.getLocation();
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
		
		String name = musicLibrary.getName();
		MusicLibrary savedMusicLibrary = getMusicLibrary(name);
		Location savedLocation = savedMusicLibrary.getLocation();
		if (!(savedLocation instanceof FtpLocation)) {
			return;
		}
		
		FtpLocation savedFtpLocation = (FtpLocation) savedLocation;
		password = savedFtpLocation.getPassword();
		ftpLocation.setPassword(password);
		musicLibrary.setLocation(ftpLocation);
	}

	@Override
	public MusicLibrary getMusicLibrary(long id) {
		MusicLibrary musicLibrary = libraryDao.getMusicLibrary(id);
		Hibernate.initialize(musicLibrary.getGroups());
		return musicLibrary;
	}

	@Override
	public void deleteLibrary(Library library) {
		long id = library.getId();
		List<Media> mediaList = mediaManager.getMedia(id);
		mediaManager.deleteMediaList(mediaList);
		List<AlbumArtImage> albumArtImages = mediaManager.getAlbumArtImages(id);
		mediaManager.deleteAlbumArtImages(albumArtImages);
		
//		List<Album> albums = mediaManager.getLibraryAlbums(id);
//		for (Album album : albums) {
//			album.setAlbumArtImage(null);
//			musicManager.saveAlbum(album);			
//		}
		
		
//		libraryDao.deleteLibrary(library);
	}

	@Override
	public MusicLibrary getMusicLibrary(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		
		MusicLibrary musicLibrary = libraryDao.getMusicLibrary(name);
		Hibernate.initialize(musicLibrary.getGroups());
		return musicLibrary;
	}
}