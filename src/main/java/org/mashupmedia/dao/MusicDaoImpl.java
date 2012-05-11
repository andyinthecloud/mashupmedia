package org.mashupmedia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.Artist;
import org.mashupmedia.model.media.Genre;
import org.mashupmedia.model.media.Song;
import org.mashupmedia.model.media.Year;
import org.springframework.stereotype.Repository;

@Repository
public class MusicDaoImpl extends BaseDaoImpl implements MusicDao {

	@Override
	public List<Album> getAlbums() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Album> albums = (List<Album>) query.list();
		return albums;
	}

	@Override
	public List<Artist> getArtists() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Artist order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Artist> artists = (List<Artist>) query.list();
		return artists;
	}

	@Override
	public Artist getArtist(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Artist where name = :name");
		query.setCacheable(true);
		query.setString("name", name);
		Artist artist = (Artist) query.uniqueResult();
		return artist;
	}

	@Override
	public void deleteSongs(List<Song> songs) {
		for (Song song : songs) {
			sessionFactory.getCurrentSession().delete(song);
		}
	}

	@Override
	public Album getAlbum(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album where name = :name");
		query.setCacheable(true);
		query.setString("name", name);
		Album album = (Album) query.uniqueResult();
		return album;
	}

	@Override
	public Album getAlbum(long albumId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album where id = :id");
		query.setCacheable(true);
		query.setLong("id", albumId);
		Album album = (Album) query.uniqueResult();
		return album;
	}

	@Override
	public Song getSong(long libraryId, String path, long sizeInBytes) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Song where library.id = :libraryId and path = :path and sizeInBytes = :sizeInBytes");
		query.setCacheable(true);
		query.setLong("libraryId", libraryId);
		query.setString("path", path);
		query.setLong("sizeInBytes", sizeInBytes);

		Song song = (Song) query.uniqueResult();
		return song;
	}

	@Override
	public List<Song> getSongsToDelete(long libraryId, Date date) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Song where library.id = :libraryId and updatedOn < :updatedOn");
		query.setCacheable(true);
		query.setLong("libraryId", libraryId);
		query.setDate("updatedOn", date);

		@SuppressWarnings("unchecked")
		List<Song> songs = query.list();
		return songs;
	}

	@Override
	public void saveSong(Song song) {
		Artist artist = song.getArtist();
		saveOrUpdate(artist);
		Album album = song.getAlbum();
		album.setArtist(artist);
		saveOrUpdate(album.getAlbumArtImage());
		saveOrUpdate(album);
		saveOrUpdate(song.getYear());
		saveOrUpdate(song.getGenre());
		saveOrUpdate(song);
	}

	@Override
	public void saveAlbum(Album album) {
		saveOrUpdate(album);
	}

	@Override
	public void saveArtist(Artist artist) {
		saveOrUpdate(artist);
	}

	@Override
	public List<Album> getRandomAlbums() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album order by rand()");
		@SuppressWarnings("unchecked")
		List<Album> albums = query.setMaxResults(20).list();
		return albums;
	}

	@Override
	public Year getYear(int year) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Year where year = :year");
		query.setCacheable(true);
		query.setInteger("year", year);
		Year album = (Year) query.uniqueResult();
		return album;
	}

	@Override
	public Genre getGenre(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Genre where name = :name");
		query.setCacheable(true);
		query.setString("name", name);
		Genre genre = (Genre) query.uniqueResult();
		return genre;
	}
	
	@Override
	public List<Song> getSongs(Long albumId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Song where album.id = :albumId order by trackNumber");
		query.setCacheable(true);
		query.setLong("albumId", albumId);

		@SuppressWarnings("unchecked")
		List<Song> songs = query.list();
		return songs;
	}

	@Override
	public void deleteAlbum(Album album) {
		Artist artist = album.getArtist();
		sessionFactory.getCurrentSession().delete(album);
		List<Album> albums = artist.getAlbums();
		if (albums == null || albums.isEmpty()) {
			sessionFactory.getCurrentSession().delete(artist);
		}
	}

	@Override
	public void deleteArtist(Artist artist) {
		sessionFactory.getCurrentSession().delete(artist);
	}

	@Override
	public List<Album> getAlbumsByArtist(long artistId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Album where artist.id = :artistId order by name");
		query.setCacheable(true);
		query.setLong("artistId", artistId);
		@SuppressWarnings("unchecked")
		List<Album> albums = query.list();
		return albums;
	}

}
