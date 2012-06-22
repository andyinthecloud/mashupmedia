package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.service.PlaylistManager.PlaylistType;
import org.springframework.stereotype.Repository;

@Repository
public class PlaylistDaoImpl extends BaseDaoImpl implements PlaylistDao {

	@Override
	public List<Playlist> getPlaylists() {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Playlist");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Playlist> playlists = (List<Playlist>) query.list();
		return playlists;
	}

	@Override
	public Playlist getPlaylist(long id) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Playlist where id = :id");
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
	public Playlist getDefaultMusicPlaylist(long userId) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Playlist where owner.id = :userId and isDefault = true and playlistType = :playlistType");
		query.setCacheable(true);
		query.setLong("userId", userId);
		query.setString("playlistType", PlaylistType.MUSIC.getIdName());
		Playlist musicPlaylist = (Playlist) query.uniqueResult();
		return musicPlaylist;
	}

	@Override
	public void savePlaylist(Playlist playlist) {
		saveOrUpdate(playlist);
	}

	@Override
	public List<Playlist> getPlaylists(long userId) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Playlist where owner.id = :userId");
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
	
//	@Override
//	public Playlist getPlaylistFromPlaylistMediaItemId(long playlistMediaItemId) {
//		Query query = sessionFactory
//				.getCurrentSession()
//				.createQuery(
//						"select playlist from Playlist playlist inner join playlist.playlistMediaItems pmi where pmi.id = :playlistMediaItemId");
//		query.setCacheable(true);
//		query.setLong("playlistMediaItemId", playlistMediaItemId);
//		Playlist musicPlaylist = (Playlist) query.uniqueResult();
//		return musicPlaylist;
//	}

}
