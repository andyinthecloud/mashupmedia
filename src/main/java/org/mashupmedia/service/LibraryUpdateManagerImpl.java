package org.mashupmedia.service;

import it.sauronsoftware.ftp4j.FTPClient;

import java.util.List;

import org.apache.log4j.Logger;
import org.mashupmedia.exception.MashupMediaException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.FtpLocation;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.util.EncryptionHelper;
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

	}

}
