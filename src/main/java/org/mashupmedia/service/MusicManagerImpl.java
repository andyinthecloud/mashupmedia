package org.mashupmedia.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.mashupmedia.dao.MusicDao;
import org.mashupmedia.model.library.Library;
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

//	@Override
//	public void saveArtists(Library library, List<Artist> artists) {
//		if (artists == null || artists.isEmpty()) {
//			logger.info("There are no artists to save");
//			return;
//		}
//
//		Date date = new Date();
//
//		List<Song> songs = new ArrayList<Song>();
//
//		for (Artist artist : artists) {
//			artist = prepareArtist(artist);
//
//			List<Album> albums = artist.getAlbums();
//			for (Album album : albums) {
//				album = prepareAlbum(album);
//				album.setArtist(artist);
//				AlbumArtImage albumArtImage = album.getAlbumArtImage();
//				if (albumArtImage != null) {
//					albumArtImage.setLibrary(library);
//					albumArtImage.setAlbum(album);
//				}
//
//				List<Song> albumSongs = album.getSongs();
//				for (Song albumSong : albumSongs) {
//					if (isNewSong(library, albumSong)) {
//						albumSong.setAlbum(album);
//						// albumSong.setGenre(genre)
//						albumSong.setUpdatedOn(date);
//						albumSong.setLibrary(library);
//						albumSong.setArtist(artist);
//						songs.add(albumSong);
//					}
//				}
//			}
//
//		}
//
//		musicDao.saveSongs(songs);
//		logger.info(songs.size() + " songs have been saved");
//
//		List<Song> songsToDelete = musicDao.getSongsToDelete(library.getId(), date);
//		musicDao.deleteSongs(songsToDelete);
//		deleteEmpty();
//	}

	protected boolean isNewSong(Library library, Song song) {
		Song savedSong = musicDao.getSong(library.getId(), song.getPath(), song.getSizeInBytes());
		if (savedSong == null) {
			return true;
		}
		return false;
	}

	protected Album prepareAlbum(Album album) {		
		Album savedAlbum = musicDao.getAlbum(album.getName());
		if (savedAlbum == null) {
			return album;
		}

		List<Song> songs = album.getSongs();
		savedAlbum.setSongs(songs);
		return savedAlbum;

	}

	public void saveAlbum(Album album) {
		musicDao.saveAlbum(album);
	}

	protected Artist prepareArtist(Artist artist, Album album) {
		Artist savedArtist = musicDao.getArtist(artist.getName());
		if (savedArtist == null) {
			return artist;
		}

		List<Album> albums = artist.getAlbums();
		if (albums == null) {
			albums = new ArrayList<Album>();
		}
		
		albums.add(album);
		savedArtist.setAlbums(albums);
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
		Hibernate.initialize(album.getSongs());
		return album;
	}

	@Override
	public List<Album> getRandomAlbums() {
		List<Album> albums = musicDao.getRandomAlbums();
		return albums;
	}

	@Override
	public void saveSongs(MusicLibrary musicLibrary, List<Song> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		for (Song song : songs) {
			song.setLibrary(musicLibrary);
			
			Album album = song.getAlbum();
			AlbumArtImage albumArtImage = album.getAlbumArtImage();
			album = prepareAlbum(album);
			if (albumArtImage != null) {
				album.setAlbumArtImage(albumArtImage);
			}
			song.setAlbum(album);

			Artist artist = song.getArtist();
			artist = prepareArtist(artist, album);
			song.setArtist(artist);
			
			Year year = song.getYear();
			year = prepareYear(year);
			song.setYear(year);
			
			Genre genre = song.getGenre();
			genre = prepareGenre(genre);
			song.setGenre(genre);
			
			
			musicDao.saveSong(song);
		}
		
	}
	
//	@Override
//	public void saveSongs(List<Song> songs) {
//		if (songs == null || songs.isEmpty()) {
//			return;
//		}
//
//		for (Song song : songs) {
//			Artist artist = song.getArtist();
//			artist = prepareArtist(artist);
//			song.setArtist(artist);
//
//			Album album = song.getAlbum();
//			album = prepareAlbum(album);
//			song.setAlbum(album);
//
//			Year year = song.getYear();
//			year = processYear(year);
//			song.setYear(year);
//
//		}
//
//		musicDao.saveSongs(songs);
//	}

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
