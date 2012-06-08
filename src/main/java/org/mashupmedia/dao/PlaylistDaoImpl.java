package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.media.Playlist;
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
	public Playlist getLastAccessedPlaylist(long userId) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Playlist as p where p.owner.id = :userId and p.lastAccessedOn = (select max(sp.lastAccessedOn) from Playlist as sp)");
		query.setCacheable(true);
		query.setLong("userId", userId);
		Playlist playlist = (Playlist) query.uniqueResult();
		return playlist;
	}
	
	@Override
	public Playlist getDefaultPlaylist(long userId) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Playlist where owner.id = :userId and isDefault = true");
		query.setCacheable(true);
		query.setLong("userId", userId);
		Playlist playlist = (Playlist) query.uniqueResult();
		return playlist;
	}

	@Override
	public void savePlaylist(Playlist playlist) {
		saveOrUpdate(playlist);
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

}
