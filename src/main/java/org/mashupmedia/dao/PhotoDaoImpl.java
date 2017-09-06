package org.mashupmedia.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.collections.list.SetUniqueList;
import org.hibernate.query.Query;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.util.DaoHelper;
import org.mashupmedia.util.MediaItemHelper.MediaItemSequenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PhotoDaoImpl extends BaseDaoImpl implements PhotoDao {

	@Autowired
	private LibraryDao libraryDao;

	@Autowired
	private GroupDao groupDao;

	private enum PhotoSequenceType {
		ALBUM_PREVIOUS, ALBUM_NEXT, PREVIOUS, NEXT, ALBUM_FIRST, ALBUM_LAST, FIRST, LAST;
	}

	@Override
	public List<Album> getAlbums(String albumName) {
		Query<Album> query = sessionFactory.getCurrentSession()
				.createQuery("from org.mashupmedia.model.media.photo.Album a where a.name = :albumName", Album.class);

		query.setParameter("albumName", albumName);
		query.setCacheable(true);
		List<Album> albums = query.list();
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
		logger.debug("Saved photo. id: " + photo.getId() + ", path: " + photo.getPath());
	}

	@Override
	public Photo getPhotoByAbsolutePath(String path) {

		StringBuilder queryBuilder = new StringBuilder("select p from Photo p");
		queryBuilder.append(" where p.path = :path");

		Query<Photo> query = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString(), Photo.class);
		query.setParameter("path", path);
		query.setCacheable(true);
		List<Photo> photos = query.list();

		if (photos == null || photos.isEmpty()) {
			return null;
		}

		if (photos.size() > 1) {
			logger.error("Duplicate photos found for the same file. Attempting to remove files. Path = " + path);
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
			logger.info("Deleting photo: " + photo.getPath());
			sessionFactory.getCurrentSession().delete(photo);
		}

	}

	protected int getTotalGroups() {
		int totalGroups = groupDao.getGroupIds().size();
		return totalGroups;
	}

	@Override
	public List<Photo> getLatestPhotos(List<Long> groupIds, int pageNumber, int totalItems) {

		StringBuilder queryBuilder = new StringBuilder("select distinct p from Photo p join p.library.groups g");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by p.takenOn desc");

		Query<Photo> query = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString(), Photo.class);
		query.setCacheable(false);

		int firstResult = pageNumber * totalItems;
		query.setMaxResults(totalItems);
		query.setFirstResult(firstResult);

		List<Photo> photos = query.list();

		return photos;
	}

	@Override
	public List<Album> getAlbums(List<Long> groupIds, MediaItemSequenceType mediaItemSequenceType) {
		StringBuilder queryBuilder = new StringBuilder("select a from Photo p join p.album a");
		queryBuilder.append(" join p.library.groups g");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			queryBuilder.append(" order by p.takenOn desc");
		} else {
			queryBuilder.append(" order by a.name");
		}

		Query<Album> query = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString(), Album.class);
		query.setCacheable(true);
		List<Album> albums = new ArrayList<Album>(new LinkedHashSet<Album>(query.list()));
		return albums;
	}

	@Override
	public List<Photo> getObsoletePhotos(long libraryId, Date date) {
		Query<Photo> query = sessionFactory.getCurrentSession()
				.createQuery("from Photo p where p.updatedOn < :date and p.library.id = :libraryId", Photo.class);
		query.setParameter("date", date);
		query.setParameter("libraryId", libraryId);
		query.setCacheable(true);
		List<Photo> photos = query.list();
		return photos;
	}

	@Override
	public int removeObsoletePhotos(long libraryId, Date date) {
		int totalDeletedPhotos = sessionFactory.getCurrentSession()
				.createQuery("delete Photo p where p.updatedOn < :date and p.library.id = :libraryId")
				.setParameter("date", date).setParameter("libraryId", libraryId).executeUpdate();
		return totalDeletedPhotos;
	}

	@Override
	public Album getAlbum(List<Long> groupIds, long albumId) {

		Query<Album> albumQuery = sessionFactory.getCurrentSession()
				.createQuery("from org.mashupmedia.model.media.photo.Album a where a.id = :albumId", Album.class);
		albumQuery.setParameter("albumId", albumId);
		albumQuery.setCacheable(true);
		List<Album> albums = albumQuery.list();
		if (albums.isEmpty()) {
			return null;
		}

		if (albums.size() > 1) {
			throw new MashupMediaRuntimeException("Error - " + albums.size() + " share the same id");
		}

		Album album = albums.get(0);

		StringBuilder listPhotosQueryBuilder = new StringBuilder(
				"select distinct p from Photo p join p.library.groups g");
		listPhotosQueryBuilder.append(" where p.album.id = :albumId");
		DaoHelper.appendGroupFilter(listPhotosQueryBuilder, groupIds);
		listPhotosQueryBuilder.append(" order by p.takenOn");
		Query<Photo> listPhotosQuery = sessionFactory.getCurrentSession().createQuery(listPhotosQueryBuilder.toString(),
				Photo.class);
		listPhotosQuery.setParameter("albumId", albumId);
		listPhotosQuery.setCacheable(true);
		List<Photo> photos = listPhotosQuery.list();

		album.setPhotos(photos);

		return album;
	}

	@Override
	public Photo getPreviousPhotoInSequence(List<Long> userGroupIds, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {

		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.NEXT);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.ALBUM_PREVIOUS);
		}

		return photo;
	}

	@Override
	public Photo getNextPhotoInSequence(List<Long> userGroupIds, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {

		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.PREVIOUS);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.ALBUM_NEXT);
		}

		return photo;
	}

	@Override
	public Photo getFirstPhotoInSequence(List<Long> userGroupIds, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {
		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.FIRST);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.ALBUM_FIRST);
		}

		return photo;
	}

	@Override
	public Photo getLastPhotoInSequence(List<Long> userGroupIds, Date takenOn, Long albumId,
			MediaItemSequenceType mediaItemSequenceType) {
		Photo photo = null;

		if (mediaItemSequenceType == MediaItemSequenceType.LATEST) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.LAST);
		} else if (mediaItemSequenceType == MediaItemSequenceType.PHOTO_ALBUM) {
			photo = getPhotoInSequence(userGroupIds, takenOn, albumId, PhotoSequenceType.ALBUM_LAST);
		}

		return photo;
	}

	public Photo getPhotoInSequence(List<Long> groupIds, Date takenOn, Long albumId,
			PhotoSequenceType photoSequenceType) {

		boolean hasAlbumParameter = false;
		boolean hasCreatedOnParameter = false;

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" select p from Photo p join p.library.groups g");
		queryBuilder.append(" where 1 = 1");

		DaoHelper.appendGroupFilter(queryBuilder, groupIds);

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

		Query<Photo> photoQuery = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString(), Photo.class);

		if (hasAlbumParameter) {
			photoQuery.setParameter("albumId", albumId);
		}

		if (hasCreatedOnParameter) {
			photoQuery.setParameter("takenOn", takenOn);
		}

		photoQuery.setCacheable(true);
		photoQuery.setFirstResult(0);

		int maxResults = getTotalGroups();
		photoQuery.setMaxResults(maxResults);
		photoQuery.setFetchSize(maxResults);

		@SuppressWarnings("unchecked")
		List<Photo> photos = SetUniqueList.decorate(photoQuery.list());

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
