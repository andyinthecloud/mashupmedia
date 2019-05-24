package org.mashupmedia.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
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

		Query<Playlist> query = getCurrentSession().createQuery(hqlBuilder.toString(), Playlist.class);
		query.setCacheable(true);

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

		Query<Playlist> query = getCurrentSession().createQuery(hqlBuilder.toString(), Playlist.class);
		query.setCacheable(true);
		query.setParameter("userId", userId);
		List<Playlist> playlists = query.list();
		return playlists;
	}

	@Override
	public Playlist getPlaylist(long id) {
		Query<Playlist> query = getCurrentSession().createQuery("from Playlist where id = :id",
				Playlist.class);
		query.setCacheable(true);
		query.setParameter("id", id);
		Playlist playlist = (Playlist) query.uniqueResult();
		return playlist;
	}

	@Override
	public Playlist getLastAccessedPlaylist(long userId, PlaylistType playlistType) {
		Query<Playlist> query = getCurrentSession().createQuery(
				"from Playlist as p where p.updatedBy.id = :userId and p.playlistTypeValue = :playlistTypeValue and p.updatedOn = (select max(tmp.updatedOn) from Playlist as tmp)",
				Playlist.class);
		query.setCacheable(true);
		query.setParameter("userId", userId);
		query.setParameter("playlistTypeValue", PlaylistType.MUSIC.getValue());
		List<Playlist> playlists = query.list();
		if (playlists == null || playlists.isEmpty()) {
			return null;
		}

		return playlists.get(0);
	}

	@Override
	public Playlist getDefaultPlaylistForUser(long userId, PlaylistType playlistType) {
		Query<Playlist> query = getCurrentSession().createQuery(
				"from Playlist where createdBy.id = :userId and userDefault = true and playlistTypeValue = :playlistTypeValue",
				Playlist.class);
		query.setCacheable(true);
		query.setParameter("userId", userId);
		query.setParameter("playlistTypeValue", playlistType.getValue());
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
		List<PlaylistMediaItem> savedPlaylistMediaItems = new ArrayList<PlaylistMediaItem>();
		if (savedPlaylist != null) {
			savedPlaylistMediaItems = savedPlaylist.getPlaylistMediaItems();
		}
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
			if (savedPlaylistMediaItems.remove(newPlaylistMediaItem)) {
				iterator.remove();
				continue;
			}
			saveOrMerge(newPlaylistMediaItem);
		}

		List<Long> savedPlaylistMediaItemIds = new ArrayList<Long>();
		for (PlaylistMediaItem savedPlaylistMediaItem : savedPlaylistMediaItems) {
			savedPlaylistMediaItemIds.add(savedPlaylistMediaItem.getId());
		}

		String hqlPlaylistMediaIds = DaoHelper.convertToHqlParameters(savedPlaylistMediaItemIds);

		if (StringUtils.isNotBlank(hqlPlaylistMediaIds)) {
			getCurrentSession()
					.createQuery("update User u set u.playlistMediaItemId = 0 where u.playlistMediaItemId in ("
							+ hqlPlaylistMediaIds + ")")
					.executeUpdate();

			getCurrentSession()
					.createQuery("delete PlaylistMediaItem where id in (" + hqlPlaylistMediaIds + ")").executeUpdate();
		}
		for (PlaylistMediaItem newPlaylistMediaItem : newPlaylistMediaItems) {
			saveOrUpdate(newPlaylistMediaItem);
		}

		return newPlaylistMediaItems;
	}

	@Override
	public void deletePlaylist(Playlist playlist) {
		getCurrentSession()
				.createQuery("delete PlaylistMediaItem pmi where pmi.playlist.id = :playlistId")
				.setParameter("playlistId", playlist.getId()).executeUpdate();
		getCurrentSession().delete(playlist);
	}

	@Override
	public void deletePlaylistMediaItem(MediaItem mediaItem) {
		long mediaItemId = mediaItem.getId();

		Query<PlaylistMediaItem> query = getCurrentSession().createQuery(
				"from PlaylistMediaItem pmi where pmi.mediaItem.id = :mediaItemId", PlaylistMediaItem.class);
		query.setParameter("mediaItemId", mediaItemId);
		query.setCacheable(true);
		List<PlaylistMediaItem> playlistMediaItems = query.list();
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return;
		}

		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			long playlistMediaItemId = playlistMediaItem.getId();
			getCurrentSession()
					.createQuery(
							"update User set playlistMediaItemId = 0 where playlistMediaItemId = :playlistMediaItemId")
					.setParameter("playlistMediaItemId", playlistMediaItemId).executeUpdate();

			getCurrentSession().delete(playlistMediaItem);
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
		Query<PlaylistMediaItem> query = getCurrentSession().createQuery(
				"from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId", PlaylistMediaItem.class);
		query.setParameter("libraryId", libraryId);
		List<PlaylistMediaItem> playlistMediaItems = query.list();
		int deletedItems = playlistMediaItems.size();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			getCurrentSession().delete(playlistMediaItem.getMediaItem());
			getCurrentSession().delete(playlistMediaItem);
		}
		logger.info("Deleted " + deletedItems + " playlistMediaItems");
	}

	@Override
	public PlaylistMediaItem getPlaylistMediaItem(long playlistMediaItemId) {
		if (playlistMediaItemId == 0) {
			return null;
		}

		Query<PlaylistMediaItem> query = getCurrentSession()
				.createQuery("from PlaylistMediaItem where id = :playlistMediaItemId", PlaylistMediaItem.class);
		query.setParameter("playlistMediaItemId", playlistMediaItemId);
		List<PlaylistMediaItem> playlistMediaItems = query.list();
		if (playlistMediaItems.isEmpty()) {
			return null;
		}

		return playlistMediaItems.get(0);
	}

}
