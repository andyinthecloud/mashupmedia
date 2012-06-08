package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;
import org.springframework.stereotype.Repository;

@Repository
public class MediaDaoImpl extends BaseDaoImpl implements MediaDao {

	@Override
	public List<MediaItem> getMedia(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Media where library.id = :libraryId order by title");
		query.setLong("libraryId", libraryId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaList = (List<MediaItem>) query.list();
		return mediaList;
	}

	@Override
	public void deleteMediaList(List<MediaItem> mediaList) {
		for (MediaItem media : mediaList) {
			sessionFactory.getCurrentSession().delete(media);
		}

	}

	@Override
	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages) {
		
		for (AlbumArtImage albumArtImage : albumArtImages) {
			Album album = albumArtImage.getAlbum();
			if (album == null) {
				logger.debug(albumArtImage.getName() + " has no album.");
				continue;
			}
				
			album.setAlbumArtImage(null);
//			sessionFactory.getCurrentSession().saveOrUpdate(album);
			sessionFactory.getCurrentSession().delete(albumArtImage);
		}
	}
	
	@Override
	public List<AlbumArtImage> getAlbumArtImages(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from AlbumArtImage where library.id = :libraryId");
		query.setLong("libraryId", libraryId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<AlbumArtImage> albumArtImages = (List<AlbumArtImage>) query.list();
		return albumArtImages;
	}
}
