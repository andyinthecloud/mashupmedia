package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MusicManagerImpl implements MusicManager {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MusicDao musicDao;

	@Override
	public List<Album> getAlbums() {
		List<Album> albums = musicDao.getAlbums();
		return albums;
	}

	@Override
	public List<Artist> getArtists() {
		List<Artist> artists = musicDao.getArtists();
		for (Artist artist : artists) {
			Hibernate.initialize(artist.getAlbums());
		}
		return artists;
	}

	@Override
	public void saveArtists(Library library, List<Artist> artists) {
		if (artists == null || artists.isEmpty()) {
			logger.info("There are no artists to save");
			return;
		}

		Date date = new Date();

		List<Song> songs = new ArrayList<Song>();

		for (Artist artist : artists) {
			processArtist(artist);

			List<Album> albums = artist.getAlbums();
			for (Album album : albums) {
				processAlbum(album);
				AlbumArtImage albumArtImage = album.getAlbumArtImage();
				if (albumArtImage != null) {
					albumArtImage.setLibrary(library);					
				}
				
				List<Song> albumSongs = album.getSongs();
				for (Song albumSong : albumSongs) {
					if (isNewSong(library, albumSong)) {
						albumSong.setAlbum(album);
						// albumSong.setGenre(genre)
						albumSong.setUpdatedOn(date);
						albumSong.setLibrary(library);
						albumSong.setArtist(artist);
						songs.add(albumSong);
					}
				}
			}

		}

		logger.info(songs.size() + " songs have been saved");
		musicDao.saveSongs(songs);

		List<Song> songsToDelete = musicDao.getSongsToDelete(library.getId(), date);
		musicDao.deleteSongs(songsToDelete);
	}

	protected boolean isNewSong(Library library, Song song) {
		Song savedSong = musicDao.getSong(library.getId(), song.getPath(), song.getSizeInBytes());
		if (savedSong == null) {
			return true;
		}
		return false;
	}

	protected Album processAlbum(Album album) {
		Album savedAlbum = musicDao.getAlbum(album.getName());
		if (savedAlbum == null) {
			return album;
		}
		return savedAlbum;

	}

	public void saveAlbum(Album album) {
		musicDao.saveAlbum(album);
	}

	protected Artist processArtist(Artist artist) {
		Artist savedArtist = musicDao.getArtist(artist.getName());
		if (savedArtist == null) {
			return artist;
		}

		return savedArtist;
	}

	protected void saveArtist(Artist artist) {
		musicDao.saveArtist(artist);

	}

	@Override
	public List<String> getArtistIndexLetters() {
		List<Artist> artists = getArtists();
		List<String> indexLetters = new ArrayList<String>();
		String indexLetter = null;
		for (Artist artist : artists) {
			String name = StringUtils.trimToEmpty(artist.getName());
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			String s = name.substring(0, 1).toUpperCase();
			indexLetter = StringUtils.trimToEmpty(indexLetter);
			if (!indexLetter.equals(s)) {
				indexLetters.add(s);
				indexLetter = s;
			}

		}

		Collections.sort(indexLetters);
		return indexLetters;
	}

	@Override
	public Album getAlbum(long albumId) {
		Album album = musicDao.getAlbum(albumId);
		return album;
	}

	@Override
	public List<Album> getRandomAlbums() {
		List<Album> albums = musicDao.getRandomAlbums();
		return albums;
	}

	@Override
	public void saveSongs(List<Song> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		for (Song song : songs) {
			Artist artist = song.getArtist();
			artist = processArtist(artist);
			song.setArtist(artist);

			Album album = song.getAlbum();
			album = processAlbum(album);
			song.setAlbum(album);

			Year year = song.getYear();
			year = processYear(year);
			song.setYear(year);

		}

		musicDao.saveSongs(songs);
	}

	private Year processYear(Year year) {
		Year savedYear = musicDao.getYear(year.getYear());
		if (savedYear == null) {
			return year;
		}

		return savedYear;
	}
	
	@Override
	public Album getAlbum(String name) {
		Album album = musicDao.getAlbum(name);
		return album;
	}
	
	@Override
	public List<Song> getSongs(Long albumId) {
		List<Song> songs = musicDao.getSongs(albumId);
		return songs;
	}
	
	
}
