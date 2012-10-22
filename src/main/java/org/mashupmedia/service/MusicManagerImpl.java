package org.mashupmedia.service;

import java.util.ArrayList;
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
import org.mashupmedia.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MusicManagerImpl implements MusicManager {
	private Logger logger = Logger.getLogger(getClass());
	private final int BATCH_INSERT_ITEMS = 20;

	@Autowired
	private AlbumArtManager albumArtManager;

	@Autowired
	private MusicDao musicDao;

	@Autowired
	private PlaylistDao playlistDao;

	@Override
	public List<Album> getAlbums(String searchLetter, int pageNumber, int totalItems) {		
		List<Album> albums = musicDao.getAlbums(searchLetter, pageNumber, totalItems);
		return albums;
	}
	
	@Override
	public List<String> getAlbumIndexLetters() {
		List<String> indexLetters = musicDao.getAlbumIndexLetters();
		return indexLetters;	}

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

	
	protected Artist prepareArtist(Artist artist) {
		Artist savedArtist = musicDao.getArtist(artist.getName());
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
	
	
	protected Album prepareAlbum(Album album) {
		Artist artist = album.getArtist();
		String albumName = album.getName();
		if (StringUtils.isBlank(albumName)) {
			return null;
		}
		
		Album savedAlbum = musicDao.getAlbum(artist.getName(), albumName);
		if (savedAlbum != null) {
			return savedAlbum;
		}

		String albumIndexLetter = StringHelper.getSearchIndexLetter(albumName);
		album.setIndexLetter(albumIndexLetter);
		String albumIndexText = StringHelper.getSearchIndexText(albumName);
		album.setIndexText(albumIndexText);
		
		return album;
		
	}
		

	protected void saveArtist(Artist artist) {
		musicDao.saveArtist(artist);

	}

	@Override
	public List<String> getArtistIndexLetters() {
		List<String> indexLetters = musicDao.getArtistIndexLetters();
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
		
		for (int i = 0; i < songs.size(); i++) {
			Song song = songs.get(i);
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

			Artist artist = song.getArtist();
			artist = prepareArtist(artist);
			
			
			Album album = song.getAlbum();
			if (StringUtils.isBlank(album.getName())) {
				logger.error("Unable to save song: " + song.toString());
				continue;
			}
			
			album.setArtist(artist);
			album = prepareAlbum(album);

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

			musicDao.saveSong(song, isSessionFlush);
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

	@Override
	public Album getAlbum(String artistName, String albumName) {
		Album album = musicDao.getAlbum(artistName, albumName);
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

	@Override
	public Artist getArtist(Long artistId) {
		Artist artist = musicDao.getArtist(artistId);
		Hibernate.initialize(artist.getAlbums());
		return artist;
	}

}
