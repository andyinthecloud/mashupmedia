package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.dao.PlaylistDao;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
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

	@Autowired
	private PlaylistDao playlistDao;

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

	public void saveAlbum(Album album) {
		musicDao.saveAlbum(album);
	}

	protected Album prepareAlbum(Artist artist, Album album) {
		album.setArtist(artist);

		Artist savedArtist = musicDao.getArtist(artist.getName());
		if (savedArtist == null) {
			return album;
		}

		List<Album> albums = artist.getAlbums();
		if (albums == null) {
			album.setArtist(savedArtist);
			return album;
		}

		String albumName = StringUtils.trimToEmpty(album.getName());

		for (Album savedAlbum : albums) {
			if (albumName.equalsIgnoreCase(savedAlbum.getName())) {
				return savedAlbum;
			}
		}

		return album;
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
		Hibernate.initialize(album.getSongs());
		return album;
	}

	@Override
	public List<Album> getRandomAlbums(int numberOfAlbums) {
		List<Album> albums = new ArrayList<Album>();
		List<Album> randomAlbums = musicDao.getRandomAlbums(numberOfAlbums);
		if (randomAlbums.isEmpty()) {
			return albums;
		}

		albums.addAll(randomAlbums);

		while (numberOfAlbums > albums.size()) {
			int appendItemsTotal = randomAlbums.size();
			int numberOfAlbumsAfterAppend = albums.size() + appendItemsTotal;
			if (numberOfAlbumsAfterAppend > numberOfAlbums) {
				appendItemsTotal = numberOfAlbums - albums.size();
				randomAlbums = randomAlbums.subList(0, appendItemsTotal);
			}

			albums.addAll(randomAlbums);
		}

		return albums;
	}

	@Override
	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		long libraryId = musicLibrary.getId();
		long totalSongsSaved = 0;

		Date date = new Date();

		for (Song song : songs) {
			song.setLibrary(musicLibrary);
			song.setUpdatedOn(date);

			String songPath = song.getPath();
			long songSizeInBytes = song.getSizeInBytes();
			Song savedSong = musicDao.getSong(libraryId, songPath, songSizeInBytes);

			if (savedSong != null) {
				long savedSongId = savedSong.getId();
				song.setId(savedSongId);
				savedSong.setUpdatedOn(date);
				musicDao.saveSong(savedSong);
				logger.info("Song is already in database, updated song date.");
				continue;
			}

			Album album = song.getAlbum();

			Artist artist = song.getArtist();
			album = prepareAlbum(artist, album);
			artist = album.getArtist();

			AlbumArtImage albumArtImage = album.getAlbumArtImage();
			if (albumArtImage != null) {
				album.setAlbumArtImage(albumArtImage);
			}
			song.setAlbum(album);

			song.setArtist(artist);

			Year year = song.getYear();
			year = prepareYear(year);
			song.setYear(year);

			Genre genre = song.getGenre();
			genre = prepareGenre(genre);
			song.setGenre(genre);

			musicDao.saveSong(song);
			totalSongsSaved++;
		}

		logger.info("Saved " + totalSongsSaved + " songs.");

		List<Song> songsToDelete = musicDao.getSongsToDelete(libraryId, date);

		playlistDao.deletePlaylistMediaItems(songsToDelete);

		musicDao.deleteSongs(songsToDelete);
		logger.info("Deleted " + songsToDelete.size() + " out of date songs.");

		deleteEmpty();
		logger.info("Cleaned library.");

	}

	private Genre prepareGenre(Genre genre) {
		if (genre == null || StringUtils.isBlank(genre.getName())) {
			return null;
		}

		Genre savedGenre = musicDao.getGenre(genre.getName());
		if (savedGenre == null) {
			return genre;
		}

		return savedGenre;
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

	@Override
	public void deleteEmpty() {
		List<Artist> artists = getArtists();
		for (Artist artist : artists) {
			List<Album> albums = musicDao.getAlbumsByArtist(artist.getId());
			if (albums == null || albums.isEmpty()) {
				musicDao.deleteArtist(artist);
				continue;
			}

			for (Album album : albums) {
				List<Song> songs = musicDao.getSongs(album.getId());
				if (songs == null || songs.isEmpty()) {
					musicDao.deleteAlbum(album);
				}
			}
		}

	}

}
