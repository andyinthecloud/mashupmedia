package org.mashupmedia.dao;

import java.io.File;
import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.FileHelper.FileType;
import org.springframework.stereotype.Repository;

@Repository
public class PlaylistDaoImpl extends BaseDaoImpl implements PlaylistDao {

	@Override
	public List<Playlist> getPlaylists(long userId, boolean isAdministrator, PlaylistType playlistType) {

		StringBuilder hqlBuilder = new StringBuilder();
		hqlBuilder.append("from Playlist");
		// hqlBuilder.append(" where createdBy.id = :userId ");
		hqlBuilder.append(" where 1 = 1");
		if (!isAdministrator) {
			hqlBuilder.append(" and (");
			hqlBuilder.append(" createdBy.id = " + userId + " or privatePlaylist = false");
			hqlBuilder.append(" )");
		}

		if (playlistType != PlaylistType.ALL) {
			hqlBuilder.append(" and playlistTypeValue = '" + playlistType.getValue() + "'");
		}
		hqlBuilder.append(" order by name");

		Query query = sessionFactory.getCurrentSession().createQuery(hqlBuilder.toString());
		query.setCacheable(true);

		@SuppressWarnings("unchecked")
		List<Playlist> playlists = (List<Playlist>) query.list();
		return playlists;
	}

	@Override
	public List<Playlist> getPlaylistsForCurrentUser(long userId, PlaylistType playlistType) {
		StringBuilder hqlBuilder = new StringBuilder();
		hqlBuilder.append("from Playlist");
		hqlBuilder.append(" where createdBy.id = :userId ");

		if (playlistType != PlaylistType.ALL) {
			hqlBuilder.append(" and playlistTypeValue = '" + playlistType.getValue() + "'");
		}

		Query query = sessionFactory.getCurrentSession().createQuery(hqlBuilder.toString());
		query.setCacheable(true);
		query.setLong("userId", userId);
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
	public Playlist getLastAccessedPlaylist(long userId, PlaylistType playlistType) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Playlist as p where p.updatedBy.id = :userId and p.playlistTypeValue = :playlistTypeValue and p.updatedOn = (select max(tmp.updatedOn) from Playlist as tmp)");
		query.setCacheable(true);
		query.setLong("userId", userId);
		query.setString("playlistTypeValue", PlaylistType.MUSIC.getValue());
		@SuppressWarnings("unchecked")
		List<Playlist> playlists = query.list();
		if (playlists == null || playlists.isEmpty()) {
			return null;
		}

		return playlists.get(0);
	}

	@Override
	public Playlist getDefaultPlaylistForUser(long userId, PlaylistType playlistType) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Playlist where createdBy.id = :userId and userDefault = true and playlistTypeValue = :playlistTypeValue");
		query.setCacheable(true);
		query.setLong("userId", userId);
		query.setString("playlistTypeValue", PlaylistType.MUSIC.getValue());
		@SuppressWarnings("unchecked")
		List<Playlist> playlists = query.list();
		if (playlists == null || playlists.isEmpty()) {
			return null;
		}

		if (playlists.size() > 1) {
			throw new MashupMediaRuntimeException("Error, more than one default playlist found for user id: " + userId);
		}

		return playlists.get(0);
	}

	@Override
	public void savePlaylist(Playlist playlist) {
		long playlistId = playlist.getId();
		deletePlaylistMediaItems(playlistId);
		sessionFactory.getCurrentSession().saveOrUpdate(playlist);

		List<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return;
		}

		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			playlistMediaItem.setId(0);
			saveOrUpdate(playlistMediaItem);
		}
		logger.info("Playlist saved");
	}

	protected void deletePlaylistMediaItems(long playlistId) {
		if (playlistId < 1) {
			return;
		}

		Query playlistMediaItemQuery = sessionFactory.getCurrentSession().createQuery(
				"select pmi.id from PlaylistMediaItem pmi where pmi.playlist.id = :playlistId");
		playlistMediaItemQuery.setLong("playlistId", playlistId);
		@SuppressWarnings("unchecked")
		List<Long> playlistMediaItemIds = playlistMediaItemQuery.list();
		for (Long playlistMediaItemId : playlistMediaItemIds) {
			Query updateUserQuery = sessionFactory.getCurrentSession().createQuery(
					"update User u set u.playlistMediaItemId = 0 where u.playlistMediaItemId = :playlistMediaItemId");
			updateUserQuery.setLong("playlistMediaItemId", playlistMediaItemId);
			updateUserQuery.executeUpdate();
		}

		Query updateMediaItemQuery = sessionFactory.getCurrentSession().createQuery("delete PlaylistMediaItem where playlist.id = :playlistId");
		updateMediaItemQuery.setLong("playlistId", playlistId);
		int deletedItems = updateMediaItemQuery.executeUpdate();
		logger.info("Deleted " + deletedItems + " playlistMediaItems for playlist id: " + playlistId);
	}

	@Override
	public void deletePlaylist(Playlist playlist) {
		Query query = sessionFactory.getCurrentSession().createQuery("delete PlaylistMediaItem pmi where pmi.playlist.id = :playlistId");
		query.setLong("playlistId", playlist.getId());
		query.executeUpdate();
		sessionFactory.getCurrentSession().delete(playlist);
	}

	
	
	@Override
	public void deletePlaylistMediaItem(MediaItem mediaItem) {
//		long totalDeletedItems = 0;

//		for (MediaItem mediaItem : mediaItems) {
			long mediaItemId = mediaItem.getId();

			Query query = sessionFactory.getCurrentSession().createQuery("from PlaylistMediaItem pmi where pmi.mediaItem.id = :mediaItemId");
			query.setLong("mediaItemId", mediaItemId);
			query.setCacheable(true);
			@SuppressWarnings("unchecked")
			List<PlaylistMediaItem> playlistMediaItems = query.list();
			if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
				return;
			}

			for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
				long playlistMediaItemId = playlistMediaItem.getId();
				Query userQuery = sessionFactory.getCurrentSession().createQuery(
						"update User set playlistMediaItemId = 0 where playlistMediaItemId = :playlistMediaItemId");
				userQuery.setLong("playlistMediaItemId", playlistMediaItemId);
				userQuery.executeUpdate();

				sessionFactory.getCurrentSession().delete(playlistMediaItem);
//				totalDeletedItems++;
			}

			Library library = mediaItem.getLibrary();
			File[] encodedMediaFiles = FileHelper.getEncodedFiles(library.getId(), mediaItemId, FileType.MEDIA_ITEM_STREAM_ENCODED);
			for (File encodedMediaFile : encodedMediaFiles) {
				FileHelper.deleteFile(encodedMediaFile);	
			}
			

//		}
//		logger.info("Deleted " + totalDeletedItems + " playlistMediaItems");
	}

	@Override
	public void deleteLibrary(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId");
		query.setLong("libraryId", libraryId);
		@SuppressWarnings("unchecked")
		List<PlaylistMediaItem> playlistMediaItems = query.list();
		int deletedItems = playlistMediaItems.size();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			sessionFactory.getCurrentSession().delete(playlistMediaItem.getMediaItem());
			sessionFactory.getCurrentSession().delete(playlistMediaItem);
		}
		logger.info("Deleted " + deletedItems + " playlistMediaItems");
	}

	@Override
	public PlaylistMediaItem getPlaylistMediaItem(long playlistMediaItemId) {
		if (playlistMediaItemId == 0) {
			return null;
		}

		Query query = sessionFactory.getCurrentSession().createQuery("from PlaylistMediaItem where id = :playlistMediaItemId");
		query.setLong("playlistMediaItemId", playlistMediaItemId);
		@SuppressWarnings("unchecked")
		List<PlaylistMediaItem> playlistMediaItems = query.list();
		if (playlistMediaItems.isEmpty()) {
			return null;
		}

		return playlistMediaItems.get(0);
	}

}
