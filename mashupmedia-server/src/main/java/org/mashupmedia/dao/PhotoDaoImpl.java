package org.mashupmedia.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import jakarta.persistence.TypedQuery;

import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.util.DaoHelper;
import org.mashupmedia.util.MediaItemHelper.MediaItemSequenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PhotoDaoImpl extends BaseDaoImpl implements PhotoDao {

	@Autowired
	private LibraryDao libraryDao;

	private enum PhotoSequenceType {
		ALBUM_PREVIOUS, ALBUM_NEXT, PREVIOUS, NEXT, ALBUM_FIRST, ALBUM_LAST, FIRST, LAST;
	}

	@Override
	public List<Album> getAlbums(String albumName) {
		TypedQuery<Album> query = entityManager
				.createQuery("from org.mashupmedia.model.media.photo.Album a where a.name = :albumName", Album.class);

		query.setParameter("albumName", albumName);
		List<Album> albums = query.getResultList();
		return albums;
	}

	@Override
	public void savePhoto(Photo photo, boolean isSessionFlush) {
		Album album = photo.getAlbum();
		saveOrUpdate(album);
		saveOrUpdate(photo);
		Library library = photo.getLibrary();
		libraryDao.saveLibrary(library);
		flushSession(isSessionFlush);
		log.debug("Saved photo. id: " + photo.getId() + ", path: " + photo.getPath());
	}

	@Override
	public Photo getPhotoByAbsolutePath(String path) {

		StringBuilder queryBuilder = new StringBuilder("select p from Photo p");
		queryBuilder.append(" where p.path = :path");

		TypedQuery<Photo> query = entityManager.createQuery(queryBuilder.toString(), Photo.class);
		query.setParameter("path", path);
		List<Photo> photos = query.getResultList();

		if (photos == null || photos.isEmpty()) {
			return null;
		}

		if (photos.size() > 1) {
			log.error("Duplicate photos found for the same file. Attempting to remove files. Path = " + path);
			Photo photo = photos.get(0);
			photos.remove(photo);
			deletePhotos(photos);
			return photo;
		}

		return photos.get(0);

	}

	protected void deletePhotos(List<Photo> photos) {
		if (photos == null || photos.isEmpty()) {
			return;
		}

		for (Photo photo : photos) {
			log.info("Deleting photo: " + photo.getPath());
			entityManager.remove(photo);
		}

	}

	@Override
	public List<Photo> getLatestPhotos(Long userId, int pageNumber, int totalItems) {

		StringBuilder queryBuilder = new StringBuilder("select p from Photo p");
		queryBuilder.append(" inner join p.library l");
		queryBuilder.append(" left join l.users u");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);
		queryBuilder.append(" order by p.takenOn desc");

		TypedQuery<Photo> query = entityManager.createQuery(queryBuilder.toString(), Photo.class);

		int firstResult = pageNumber * totalItems;
		query.setMaxResults(totalItems);
		query.setFirstResult(firstResult);

		return query.getResultList();
	}

	@Override
	public List<Album> getAlbums(Long userId, MediaItemSequenceType mediaItemSequenceType) {

		StringBuilder queryBuilder = new StringBuilder("select a from Photo p");
		queryBuilder.append(" join p.album a");
		queryBuilder.append(" join p.library l");
		queryBuilder.append(" left join l.users u");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			queryBuilder.append(" order by p.takenOn desc");
		} else {
			queryBuilder.append(" order by a.name");
		}

		TypedQuery<Album> query = entityManager.createQuery(queryBuilder.toString(), Album.class);
		List<Album> albums = new ArrayList<Album>(new LinkedHashSet<Album>(query.getResultList()));
		return albums;
	}

	@Override
	public List<Photo> getObsoletePhotos(long libraryId, Date date) {
		TypedQuery<Photo> query = entityManager
				.createQuery("from Photo p where p.updatedOn < :date and p.library.id = :libraryId", Photo.class);
		query.setParameter("date", date);
		query.setParameter("libraryId", libraryId);
		List<Photo> photos = query.getResultList();
		return photos;
	}

	@Override
	public int removeObsoletePhotos(long libraryId, Date date) {
		int totalDeletedPhotos = entityManager
				.createQuery("delete Photo p where p.updatedOn < :date and p.library.id = :libraryId")
				.setParameter("date", date).setParameter("libraryId", libraryId).executeUpdate();
		return totalDeletedPhotos;
	}

	@Override
	public Album getAlbum(Long userId, long albumId) {

		TypedQuery<Album> albumQuery = entityManager
				.createQuery("from org.mashupmedia.model.media.photo.Album a where a.id = :albumId", Album.class);
		albumQuery.setParameter("albumId", albumId);
		List<Album> albums = albumQuery.getResultList();
		if (albums.isEmpty()) {
			return null;
		}

		if (albums.size() > 1) {
			throw new MashupMediaRuntimeException("Error - " + albums.size() + " share the same id");
		}

		Album album = albums.get(0);

		StringBuilder listPhotosQueryBuilder = new StringBuilder(
				"select distinct p from Photo p");
		listPhotosQueryBuilder.append(" join p.library l");
		listPhotosQueryBuilder.append(" left join l.users u");
		listPhotosQueryBuilder.append(" where p.album.id = :albumId");
		DaoHelper.appendUserIdFilter(listPhotosQueryBuilder, userId);
		listPhotosQueryBuilder.append(" order by p.takenOn");
		TypedQuery<Photo> listPhotosQuery = entityManager.createQuery(listPhotosQueryBuilder.toString(),
				Photo.class);
		listPhotosQuery.setParameter("albumId", albumId);
		List<Photo> photos = listPhotosQuery.getResultList();

		album.setPhotos(photos);

		return album;
	}

	@Override
	public Photo getPreviousPhotoInSequence(Long userId, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {

		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.NEXT);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.ALBUM_PREVIOUS);
		}

		return photo;
	}

	@Override
	public Photo getNextPhotoInSequence(Long userId, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {

		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.PREVIOUS);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.ALBUM_NEXT);
		}

		return photo;
	}

	@Override
	public Photo getFirstPhotoInSequence(Long userId, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {
		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.FIRST);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.ALBUM_FIRST);
		}

		return photo;
	}

	@Override
	public Photo getLastPhotoInSequence(Long userId, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {
		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.LAST);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userId, takenOn, albumId, PhotoSequenceType.ALBUM_LAST);
		}

		return photo;
	}

	public Photo getPhotoInSequence(Long userId, Date takenOn, Long albumId,
			PhotoSequenceType photoSequenceType) {

		boolean hasAlbumParameter = false;
		boolean hasCreatedOnParameter = false;

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" select p from Photo p");
		queryBuilder.append(" join p.library l");
		queryBuilder.append(" left join l.users u");
		DaoHelper.appendUserIdFilter(queryBuilder, userId);

		if (photoSequenceType == PhotoSequenceType.ALBUM_PREVIOUS) {
			queryBuilder.append(" and p.album.id = :albumId");
			hasAlbumParameter = true;
			preparePreviousPhotoSql(queryBuilder);
			hasCreatedOnParameter = true;
		} else if (photoSequenceType == PhotoSequenceType.ALBUM_NEXT) {
			queryBuilder.append(" and p.album.id = :albumId");
			hasAlbumParameter = true;
			prepareNextPhotoSql(queryBuilder);
			hasCreatedOnParameter = true;
		} else if (photoSequenceType == PhotoSequenceType.PREVIOUS) {
			preparePreviousPhotoSql(queryBuilder);
			hasCreatedOnParameter = true;
		} else if (photoSequenceType == PhotoSequenceType.NEXT) {
			prepareNextPhotoSql(queryBuilder);
			hasCreatedOnParameter = true;
		} else if (photoSequenceType == PhotoSequenceType.ALBUM_FIRST) {
			queryBuilder.append(" and p.album.id = :albumId");
			hasAlbumParameter = true;
			prepareFirstPhotoSql(queryBuilder);
		} else if (photoSequenceType == PhotoSequenceType.FIRST) {
			prepareFirstPhotoSql(queryBuilder);
		} else if (photoSequenceType == PhotoSequenceType.ALBUM_LAST) {
			queryBuilder.append(" and p.album.id = :albumId");
			hasAlbumParameter = true;
			prepareLastPhotoSql(queryBuilder);
		} else if (photoSequenceType == PhotoSequenceType.LAST) {
			prepareLastPhotoSql(queryBuilder);
		}

		TypedQuery<Photo> photoQuery = entityManager.createQuery(queryBuilder.toString(), Photo.class);

		if (hasAlbumParameter) {
			photoQuery.setParameter("albumId", albumId);
		}

		if (hasCreatedOnParameter) {
			photoQuery.setParameter("takenOn", takenOn);
		}

		photoQuery.setFirstResult(0);

		photoQuery.setMaxResults(1);
		// photoQuery.setFetchSize(maxResults);

		// @SuppressWarnings("unchecked")
		List<Photo> photos = photoQuery.getResultList();

		if (photos == null || photos.isEmpty()) {
			return null;
		}

		return photos.get(0);
	}

	protected void prepareLastPhotoSql(StringBuilder queryBuilder) {
		queryBuilder.append(" order by p.takenOn desc");

	}

	protected void prepareFirstPhotoSql(StringBuilder queryBuilder) {
		queryBuilder.append(" order by p.takenOn asc");
	}

	protected void preparePreviousPhotoSql(StringBuilder queryBuilder) {
		queryBuilder.append(" and p.takenOn < :takenOn");
		queryBuilder.append(" order by p.takenOn desc");
	}

	protected void prepareNextPhotoSql(StringBuilder queryBuilder) {
		queryBuilder.append(" and p.takenOn > :takenOn");
		queryBuilder.append(" order by p.takenOn asc");
	}

}
