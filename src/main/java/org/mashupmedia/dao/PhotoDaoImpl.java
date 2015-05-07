package org.mashupmedia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.media.photo.Album;
import org.mashupmedia.model.media.photo.Photo;
import org.mashupmedia.service.PhotoManager.PhotoSequenceType;
import org.mashupmedia.util.DaoHelper;
import org.springframework.stereotype.Repository;

@Repository
public class PhotoDaoImpl extends BaseDaoImpl implements PhotoDao {

	@Override
	public List<Album> getAlbums(String albumName) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from org.mashupmedia.model.media.photo.Album a where a.name = :albumName");

		query.setString("albumName", albumName);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Album> albums = query.list();
		return albums;
	}

	@Override
	public void savePhoto(Photo photo, boolean isSessionFlush) {
		Album album = photo.getAlbum();
		saveOrUpdate(album);
		saveOrUpdate(photo);
		flushSession(isSessionFlush);
	}

	@Override
	public Photo getPhotoByAbsolutePath(String path) {

		StringBuilder queryBuilder = new StringBuilder("select p from Photo p");
		queryBuilder.append(" where p.path = :path");

		Query query = sessionFactory.getCurrentSession().createQuery(
				queryBuilder.toString());
		query.setString("path", path);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Photo> photos = query.list();

		if (photos == null || photos.isEmpty()) {
			return null;
		}

		if (photos.size() > 1) {
			logger.error("Duplicate photos found for the same file. Attempting to remove files");
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

	@Override
	public List<Photo> getLatestPhotos(List<Long> groupIds, int pageNumber,
			int totalItems) {

		StringBuilder queryBuilder = new StringBuilder(
				"select distinct p from Photo p join p.library.groups g");
		DaoHelper.appendGroupFilter(queryBuilder, groupIds);
		queryBuilder.append(" order by p.createdOn desc");

		Query query = sessionFactory.getCurrentSession().createQuery(
				queryBuilder.toString());
		query.setCacheable(true);

		int firstResult = pageNumber * totalItems;
		query.setMaxResults(totalItems);
		query.setFirstResult(firstResult);

		@SuppressWarnings("unchecked")
		List<Photo> photos = query.list();
		return photos;
	}

	@Override
	public List<Album> getAlbums() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select a from org.mashupmedia.model.media.photo.Album a order by a.updatedOn");

		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Album> albums = query.list();
		return albums;
	}

	@Override
	public List<Photo> getObsoletePhotos(long libraryId, Date date) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Photo p where p.updatedOn < :date and p.library.id = :libraryId");
		query.setDate("date", date);
		query.setLong("libraryId", libraryId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Photo> photos = query.list();
		return photos;
	}

	@Override
	public int removeObsoletePhotos(long libraryId, Date date) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"delete Photo p where p.updatedOn < :date and p.library.id = :libraryId");
		query.setDate("date", date);
		query.setLong("libraryId", libraryId);
		int totalDeletedPhotos = query.executeUpdate();
		return totalDeletedPhotos;
	}

	@Override
	public Album getAlbum(List<Long> groupIds, long albumId) {

		Query albumQuery = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from org.mashupmedia.model.media.photo.Album a where a.id = :albumId");
		albumQuery.setLong("albumId", albumId);
		albumQuery.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Album> albums = albumQuery.list();
		if (albums.isEmpty()) {
			return null;
		}

		if (albums.size() > 1) {
			throw new MashupMediaRuntimeException("Error - " + albums.size()
					+ " share the same id");
		}

		Album album = albums.get(0);

		StringBuilder listPhotosQueryBuilder = new StringBuilder(
				"select distinct p from Photo p join p.library.groups g");
		listPhotosQueryBuilder.append(" where p.album.id = :albumId");
		DaoHelper.appendGroupFilter(listPhotosQueryBuilder, groupIds);
		listPhotosQueryBuilder.append(" order by p.createdOn desc");
		Query listPhotosQuery = sessionFactory.getCurrentSession().createQuery(
				listPhotosQueryBuilder.toString());
		listPhotosQuery.setLong("albumId", albumId);
		listPhotosQuery.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Photo> photos = listPhotosQuery.list();

		album.setPhotos(photos);

		return album;
	}

	@Override
	public Photo getPhotoInSequence(List<Long> groupIds, Long photoId, Date photoCreatedOn,
			Long albumId, PhotoSequenceType photoSequenceType) {
		
//		List nextItems = sessionFactory.getCurrentSession().createQuery("select distinct(p) from Photo p join p.library.groups g where p.album.id = 1 and g.id in (2,1) and p.createdOn <= '2015-05-04 15:35:37.331' order by p.id desc, ").list();
//		List previousItems = sessionFactory.getCurrentSession().createQuery("select distinct(p) from Photo p join p.library.groups g where p.album.id = 2 and g.id in (2,1) and p.createdOn <= '2015-05-04 15:35:37.331' order by p.id asc").list();

	
		StringBuilder photoQueryBuilder = new StringBuilder();
		photoQueryBuilder.append(" select distinct(p) from Photo p join p.library.groups g");
		photoQueryBuilder.append(" where p.album.id = :albumId");
		DaoHelper.appendGroupFilter(photoQueryBuilder, groupIds);
		
		if (photoSequenceType == PhotoSequenceType.PREVIOUS) {
			preparePreviousPhotoSql(photoQueryBuilder, groupIds);
		} else if (photoSequenceType == PhotoSequenceType.NEXT) {
			prepareNextPhotoSql(photoQueryBuilder, groupIds);
		}

		Query photoQuery = sessionFactory.getCurrentSession().createQuery(
				photoQueryBuilder.toString());
		photoQuery.setLong("photoId", photoId);
		photoQuery.setLong("albumId", albumId);
		photoQuery.setTimestamp("createdOn", photoCreatedOn);
		photoQuery.setCacheable(true);
		photoQuery.setFirstResult(0);
		photoQuery.setMaxResults(1);
		photoQuery.setFetchSize(1);

		@SuppressWarnings("unchecked")
		List<Photo> photos = photoQuery.list();
		if (photos == null || photos.isEmpty()) {
			return null;
		}

		return photos.get(0);
	}

	protected void preparePreviousPhotoSql(StringBuilder queryBuilder,
			List<Long> groupIds) {
		queryBuilder.append(" and p.createdOn <= :createdOn");
		queryBuilder.append(" and p.id < :photoId");
		queryBuilder.append(" order by p.id desc");
	}

	protected void prepareNextPhotoSql(StringBuilder queryBuilder,
			List<Long> groupIds) {
		queryBuilder.append(" and p.createdOn >= :createdOn");
		queryBuilder.append(" and p.id > :photoId");
		queryBuilder.append(" order by p.id asc");
	}

}
