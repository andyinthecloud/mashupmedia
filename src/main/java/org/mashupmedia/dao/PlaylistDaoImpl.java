package org.mashupmedia.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.util.DaoHelper;
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
		Query query = sessionFactory.getCurrentSession().createQuery(
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
		Playlist savedPlaylist = getPlaylist(playlistId);
		List<PlaylistMediaItem> savedPlaylistMediaItems = savedPlaylist.getPlaylistMediaItems();
		List<PlaylistMediaItem> newPlaylistMediaItems = playlist.getPlaylistMediaItems();
		synchronisePlaylistMediaItems(savedPlaylistMediaItems, newPlaylistMediaItems);
		saveOrMerge(playlist);
		logger.info("Playlist saved");
	}

	protected List<PlaylistMediaItem> synchronisePlaylistMediaItems(List<PlaylistMediaItem> savedPlaylistMediaItems,
			List<PlaylistMediaItem> newPlaylistMediaItems) {

		if (savedPlaylistMediaItems == null) {
			savedPlaylistMediaItems = new ArrayList<PlaylistMediaItem>();
		}

		if (newPlaylistMediaItems == null || newPlaylistMediaItems.isEmpty()) {
			newPlaylistMediaItems = new ArrayList<PlaylistMediaItem>();
		}

		for (Iterator<PlaylistMediaItem> iterator = newPlaylistMediaItems.iterator(); iterator.hasNext();) {
			PlaylistMediaItem newPlaylistMediaItem = (PlaylistMediaItem) iterator.next();
			if (savedPlaylistMediaItems.contains(newPlaylistMediaItem)) {
				iterator.remove();
				savedPlaylistMediaItems.remove(newPlaylistMediaItem);
			}
			saveOrMerge(newPlaylistMediaItem);
		}

		List<Long> savedPlaylistMediaItemIds = new ArrayList<Long>();
		for (PlaylistMediaItem savedPlaylistMediaItem : savedPlaylistMediaItems) {
			savedPlaylistMediaItemIds.add(savedPlaylistMediaItem.getId());
		}

		String hqlPlaylistMediaIds = DaoHelper.convertToHqlParameters(savedPlaylistMediaItemIds);

		Query resetUserPlaylistMediaItemQuery = sessionFactory.getCurrentSession()
				.createQuery("update User u set u.playlistMediaItemId = 0 where u.playlistMediaItemId in ("
						+ hqlPlaylistMediaIds + ")");
		resetUserPlaylistMediaItemQuery.executeUpdate();

		Query deletePlaylistMediaItemQuery = sessionFactory.getCurrentSession()
				.createQuery("delete PlaylistMediaItem where id in (" + hqlPlaylistMediaIds + ")");
		deletePlaylistMediaItemQuery.executeUpdate();

		for (PlaylistMediaItem newPlaylistMediaItem : newPlaylistMediaItems) {
			saveOrUpdate(newPlaylistMediaItem);
		}

		return newPlaylistMediaItems;
	}

	@Override
	public void deletePlaylist(Playlist playlist) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("delete PlaylistMediaItem pmi where pmi.playlist.id = :playlistId");
		query.setLong("playlistId", playlist.getId());
		query.executeUpdate();
		sessionFactory.getCurrentSession().delete(playlist);
	}

	@Override
	public void deletePlaylistMediaItem(MediaItem mediaItem) {
		long mediaItemId = mediaItem.getId();

		Query query = sessionFactory.getCurrentSession()
				.createQuery("from PlaylistMediaItem pmi where pmi.mediaItem.id = :mediaItemId");
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
		}

		Library library = mediaItem.getLibrary();
		File[] encodedMediaFiles = FileHelper.getEncodedFiles(library.getId(), mediaItemId,
				FileType.MEDIA_ITEM_STREAM_ENCODED);
		for (File encodedMediaFile : encodedMediaFiles) {
			FileHelper.deleteFile(encodedMediaFile);
		}

	}

	@Override
	public void deleteLibrary(long libraryId) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId");
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

		Query query = sessionFactory.getCurrentSession()
				.createQuery("from PlaylistMediaItem where id = :playlistMediaItemId");
		query.setLong("playlistMediaItemId", playlistMediaItemId);
		@SuppressWarnings("unchecked")
		List<PlaylistMediaItem> playlistMediaItems = query.list();
		if (playlistMediaItems.isEmpty()) {
			return null;
		}

		return playlistMediaItems.get(0);
	}

}
