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
		Query query = sessionFactory.getCurrentSession().createQuery("from MediaItem where library.id = :libraryId order by title");
		query.setLong("libraryId", libraryId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaList = (List<MediaItem>) query.list();
		return mediaList;
	}

	@Override
	public void deleteMediaList(List<MediaItem> mediaItems) {
		
//		Query query = sessionFactory.getCurrentSession().createQuery("delete MediaItem where library.id = :libraryId order by title");
//		query.setLong("libraryId", libraryId);
//		query.setCacheable(true);
		
		
		for (MediaItem mediaItem : mediaItems) {
			sessionFactory.getCurrentSession().delete(mediaItem);
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
	
	@Override
	public MediaItem getMediaItem(long mediaItemId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from MediaItem where id = :mediaItemId");
		query.setLong("mediaItemId", mediaItemId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<MediaItem> mediaItems = (List<MediaItem>) query.list();
		if (mediaItems == null || mediaItems.isEmpty()) {
			return null;
		}
		
		return mediaItems.get(0);
	}
}
