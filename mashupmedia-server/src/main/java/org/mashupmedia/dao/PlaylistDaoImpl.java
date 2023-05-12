package org.mashupmedia.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.repository.playlist.PlaylistMediaItemRepository;
import org.mashupmedia.repository.playlist.UserPlaylistPositionRepository;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.mashupmedia.model.playlist.UserPlaylistPosition;
import org.mashupmedia.util.DaoHelper;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.FileHelper.FileType;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PlaylistDaoImpl extends BaseDaoImpl implements PlaylistDao {

	private final UserPlaylistPositionRepository userPlaylistPositionRepository;

	private final PlaylistMediaItemRepository playlistMediaItemRepository;

	@Override
	public List<Playlist> getPlaylists(long userId, boolean isAdministrator, MashupMediaType mashupMediaType) {

		StringBuilder hqlBuilder = new StringBuilder();
		hqlBuilder.append("from Playlist");
		// hqlBuilder.append(" where createdBy.id = :userId ");
		hqlBuilder.append(" where 1 = 1");
		if (!isAdministrator) {
			hqlBuilder.append(" and (");
			hqlBuilder.append(" createdBy.id = " + userId + " or privatePlaylist = false");
			hqlBuilder.append(" )");
		}

		if (mashupMediaType != null) {
			hqlBuilder.append(" and mediaTypeValue = '" + mashupMediaType.name() + "'");
		}
		hqlBuilder.append(" order by name");

		TypedQuery<Playlist> query = entityManager.createQuery(hqlBuilder.toString(), Playlist.class);

		List<Playlist> playlists = query.getResultList();
		return playlists;
	}

	@Override
	public List<Playlist> getPlaylistsForCurrentUser(long userId, MashupMediaType mashupMediaType) {
		StringBuilder hqlBuilder = new StringBuilder();
		hqlBuilder.append("from Playlist");
		hqlBuilder.append(" where createdBy.id = :userId ");

		if (mashupMediaType != null) {
			hqlBuilder.append(" and mediaTypeValue = '" + mashupMediaType.name() + "'");
		}

		TypedQuery<Playlist> query = entityManager.createQuery(hqlBuilder.toString(), Playlist.class);
		query.setParameter("userId", userId);
		List<Playlist> playlists = query.getResultList();
		return playlists;
	}

	@Override
	public Playlist getPlaylist(long id) {
		TypedQuery<Playlist> query = entityManager.createQuery("from Playlist where id = :id",
				Playlist.class);
		query.setParameter("id", id);
		Playlist playlist = getUniqueResult(query);
		return playlist;
	}

	@Override
	public Playlist getLastAccessedPlaylist(long userId, MashupMediaType mashupMediaType) {
		TypedQuery<Playlist> query = entityManager.createQuery(
				"from Playlist as p where p.updatedBy.id = :userId and p.mediaTypeValue = :mediaTypeValue and p.updatedOn = (select max(tmp.updatedOn) from Playlist as tmp)",
				Playlist.class);
		query.setParameter("userId", userId);
		query.setParameter("mediaTypeValue", mashupMediaType.name());
		List<Playlist> playlists = query.getResultList();
		if (playlists == null || playlists.isEmpty()) {
			return null;
		}

		return playlists.get(0);
	}

	@Override
	public Playlist getDefaultPlaylistForUser(long userId, MashupMediaType mashupMediaType) {
		TypedQuery<Playlist> query = entityManager.createQuery(
				"from Playlist where createdBy.id = :userId and userDefault = true and mediaTypeValue = :mediaTypeValue",
				Playlist.class);
		query.setParameter("userId", userId);
		query.setParameter("mediaTypeValue", mashupMediaType.name());
		List<Playlist> playlists = query.getResultList();
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
		Collection<PlaylistMediaItem> playlistMediaItems = playlist.getPlaylistMediaItems();

		long playlistId = playlist.getId();
		Playlist savedPlaylist = getPlaylist(playlistId);
		if (savedPlaylist == null) {
			playlist.setMashupMediaType(getMashupMediaType(playlistMediaItems));
			saveOrMerge(playlist);
			return;
		}

		savedPlaylist.setName(playlist.getName());
		savedPlaylist.setUpdatedBy(playlist.getUpdatedBy());
		savedPlaylist.setUpdatedOn(playlist.getUpdatedOn());
		savedPlaylist.setPrivatePlaylist(playlist.isPrivatePlaylist());

		Set<PlaylistMediaItem> savedPlaylistMediaItems = savedPlaylist.getPlaylistMediaItems();
		if (savedPlaylistMediaItems != null && !savedPlaylistMediaItems.isEmpty()) {
			savedPlaylist.getPlaylistMediaItems().clear();
		}

		savedPlaylist.getPlaylistMediaItems().addAll(playlistMediaItems);
		saveOrMerge(savedPlaylist);
	}

	private MashupMediaType getMashupMediaType(Collection<PlaylistMediaItem> playlistMediaItems) {
		Optional<MashupMediaType> optionalMashupMediaType = playlistMediaItems
				.stream()
				.map(pmi -> pmi.getMediaItem().getMashupMediaType())
				.findAny();

		return optionalMashupMediaType.orElse(MashupMediaType.MUSIC);
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
			saveOrMerge(newPlaylistMediaItem);
		}

		List<Long> savedPlaylistMediaItemIds = new ArrayList<Long>();
		for (PlaylistMediaItem savedPlaylistMediaItem : newPlaylistMediaItems) {
			// for (PlaylistMediaItem savedPlaylistMediaItem : savedPlaylistMediaItems) {
			savedPlaylistMediaItemIds.add(savedPlaylistMediaItem.getId());
		}

		String hqlPlaylistMediaIds = DaoHelper.convertToHqlParameters(savedPlaylistMediaItemIds);

		if (StringUtils.isNotBlank(hqlPlaylistMediaIds)) {
			entityManager
					.createQuery("update User u set u.playlistMediaItemId = 0 where u.playlistMediaItemId in ("
							+ hqlPlaylistMediaIds + ")")
					.executeUpdate();

			entityManager
					.createQuery("delete PlaylistMediaItem where id in (" + hqlPlaylistMediaIds + ")").executeUpdate();
		}
		for (PlaylistMediaItem newPlaylistMediaItem : newPlaylistMediaItems) {
			saveOrUpdate(newPlaylistMediaItem);
		}

		return newPlaylistMediaItems;
	}

	@Override
	public void deletePlaylistMediaItem(MediaItem mediaItem) {
		long mediaItemId = mediaItem.getId();

		// TypedQuery<PlaylistMediaItem> query = entityManager.createQuery(
		// "from PlaylistMediaItem pmi where pmi.mediaItem.id = :mediaItemId",
		// PlaylistMediaItem.class);
		// query.setParameter("mediaItemId", mediaItemId);

		// List<PlaylistMediaItem> playlistMediaItems = query.getResultList();
		List<PlaylistMediaItem> playlistMediaItems = playlistMediaItemRepository.findByMediaItemId(mediaItemId);
		if (playlistMediaItems == null || playlistMediaItems.isEmpty()) {
			return;
		}

		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			long playlistMediaItemId = playlistMediaItem.getId();
			List<User> users = userPlaylistPositionRepository.findByPlaylistItem(playlistMediaItemId);
			for (User user : users) {
				removePlaylistMediaItemFromUser(user, playlistMediaItemId);
			}
		}

		Library library = mediaItem.getLibrary();
		File[] encodedMediaFiles = FileHelper.getEncodedFiles(library.getId(), mediaItemId,
				FileType.MEDIA_ITEM_STREAM_ENCODED);
		for (File encodedMediaFile : encodedMediaFiles) {
			FileHelper.deleteFile(encodedMediaFile);
		}

	}

	private void removePlaylistMediaItemFromUser(User user, long playlistMediaId) {
		Set<UserPlaylistPosition> userPlaylistPositionToDelete = user.getUserPlaylistPositions()
				.stream()
				.filter(upp -> upp.getPlaylistMediaId() == playlistMediaId)
				.collect(Collectors.toSet());

		user.getUserPlaylistPositions().removeAll(userPlaylistPositionToDelete);
		saveOrMerge(user);
	}

	@Override
	public void deleteLibrary(long libraryId) {
		TypedQuery<PlaylistMediaItem> query = entityManager.createQuery(
				"from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId", PlaylistMediaItem.class);
		query.setParameter("libraryId", libraryId);
		List<PlaylistMediaItem> playlistMediaItems = query.getResultList();
		int deletedItems = playlistMediaItems.size();
		for (PlaylistMediaItem playlistMediaItem : playlistMediaItems) {
			entityManager.remove(playlistMediaItem.getMediaItem());
			entityManager.remove(playlistMediaItem);
		}
		log.info("Deleted " + deletedItems + " playlistMediaItems");
	}

	@Override
	public PlaylistMediaItem getPlaylistMediaItem(long playlistMediaItemId) {
		if (playlistMediaItemId == 0) {
			return null;
		}

		TypedQuery<PlaylistMediaItem> query = entityManager
				.createQuery("from PlaylistMediaItem where id = :playlistMediaItemId", PlaylistMediaItem.class);
		query.setParameter("playlistMediaItemId", playlistMediaItemId);
		List<PlaylistMediaItem> playlistMediaItems = query.getResultList();
		if (playlistMediaItems.isEmpty()) {
			return null;
		}

		return playlistMediaItems.get(0);
	}

}
