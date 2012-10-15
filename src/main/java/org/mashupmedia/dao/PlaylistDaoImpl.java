package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.service.PlaylistManager.PlaylistType;
import org.springframework.stereotype.Repository;

@Repository
public class PlaylistDaoImpl extends BaseDaoImpl implements PlaylistDao {

	@Override
	public List<Playlist> getPlaylists() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Playlist");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Playlist> playlists = (List<Playlist>) query.list();
		return playlists;
	}

	@Override
	public Playlist getPlaylist(long id) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Playlist where id = :id");
		query.setCacheable(true);
		query.setLong("id", id);
		Playlist playlist = (Playlist) query.uniqueResult();
		return playlist;
	}

	@Override
	public Playlist getLastAccessedMusicPlaylist(long userId) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Playlist as mp where mp.owner.id = :userId and mp.playlistType = :playlistType and mp.lastAccessedOn = (select max(tmp.lastAccessedOn) from Playlist as tmp)");
		query.setCacheable(true);
		query.setLong("userId", userId);
		query.setString("playlistType", PlaylistType.MUSIC.getIdName());
		Playlist playlist = (Playlist) query.uniqueResult();
		return playlist;
	}

	@Override
	public Playlist getDefaultMusicPlaylistForUser(long userId) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Playlist where owner.id = :userId and isUserDefault = true and playlistType = :playlistType");
		query.setCacheable(true);
		query.setLong("userId", userId);
		query.setString("playlistType", PlaylistType.MUSIC.getIdName());
		Playlist musicPlaylist = (Playlist) query.uniqueResult();
		return musicPlaylist;
	}

	@Override
	public void savePlaylist(Playlist playlist) {
		deletePlaylistMediaItems(playlist.getId());
		saveOrUpdate(playlist);
		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			sessionFactory.getCurrentSession().saveOrUpdate(playlistMediaItem);
		}
	}

	protected void deletePlaylistMediaItems(long playlistId) {
		if (playlistId < 1) {
			return;
		}
		
		Query query = sessionFactory.getCurrentSession().createQuery("delete PlaylistMediaItem pmi where pmi.playlist.id = :playlistId");
		query.setLong("playlistId", playlistId);
		int deletedItems = query.executeUpdate();
		logger.info("Deleted " + deletedItems + " playlistMediaItems for playlist id: " + playlistId);
	}

	@Override
	public List<Playlist> getPlaylists(long userId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Playlist where owner.id = :userId");
		query.setCacheable(true);
		query.setLong("userId", userId);
		@SuppressWarnings("unchecked")
		List<Playlist> playlists = (List<Playlist>) query.list();
		return playlists;
	}

	@Override
	public void deletePlaylist(Playlist playlist) {
		sessionFactory.getCurrentSession().delete(playlist);
	}

	@Override
	public void deletePlaylistMediaItems(List<? extends MediaItem> mediaItems) {
		for (MediaItem mediaItem : mediaItems) {
			long mediaItemId = mediaItem.getId();
			Query query = sessionFactory.getCurrentSession().createQuery("delete PlaylistMediaItem pmi where pmi.mediaItem.id = :mediaItemId");
			query.setLong("mediaItemId", mediaItemId);
			int deletedItems = query.executeUpdate();
			logger.info("Deleted " + deletedItems + " playlistMediaItems");
		}
	}

	@Override
	public void deleteLibrary(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId");
		query.setLong("libraryId", libraryId);		
		@SuppressWarnings("unchecked")
		List<PlaylistMediaItem> playlistMediaItems = query.list();
		int deletedItems = playlistMediaItems.size();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			sessionFactory.getCurrentSession().delete(playlistMediaItem);
		}
		logger.info("Deleted " + deletedItems + " playlistMediaItems");
		
//		Query query = sessionFactory.getCurrentSession().createQuery("delete PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId");
//		query.setLong("libraryId", libraryId);
//		int deletedItems = query.executeUpdate();
//		logger.info("Deleted " + deletedItems + " playlistMediaItems");
		
	}
	
	// protected void deleteMediaItem(Playlist playlist, long mediaItemId) {
	// List<PlaylistMediaItem> playlistMediaItems =
	// playlist.getPlaylistMediaItems();
	// if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
	// return;
	// }
	//
	// List<PlaylistMediaItem> playlistMediaItemsToDelete = new
	// ArrayList<PlaylistMediaItem>();
	//
	// for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
	// MediaItem mediaItem = playlistMediaItem.getMediaItem();
	// if (mediaItem.getId() == mediaItemId) {
	// playlistMediaItemsToDelete.add(playlistMediaItem);
	// }
	// }
	//
	// if (playlistMediaItems.isEmpty()) {
	// return;
	// }
	//
	// playlistMediaItems.removeAll(playlistMediaItemsToDelete);
	// sessionFactory.getCurrentSession().merge(playlist);
	// }

	// @Override
	// public Playlist getPlaylistFromPlaylistMediaItemId(long
	// playlistMediaItemId) {
	// Query query = sessionFactory
	// .getCurrentSession()
	// .createQuery(
	// "select playlist from Playlist playlist inner join playlist.playlistMediaItems pmi where pmi.id = :playlistMediaItemId");
	// query.setCacheable(true);
	// query.setLong("playlistMediaItemId", playlistMediaItemId);
	// Playlist musicPlaylist = (Playlist) query.uniqueResult();
	// return musicPlaylist;
	// }

}
