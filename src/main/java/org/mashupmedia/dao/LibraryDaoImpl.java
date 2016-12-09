package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.RemoteShare;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryDaoImpl extends BaseDaoImpl implements LibraryDao {

	@Override
	public List<Library> getRemoteLibraries() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Library where remote = true order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = (List<Library>) query.list();
		return libraries;
	}

	@Override
	public List<Library> getLocalLibraries(LibraryType libraryType) {
		String libraryClassName = null;

		if (libraryType == LibraryType.MUSIC) {
			libraryClassName = MusicLibrary.class.getName();
		} else {
			libraryClassName = Library.class.getName();
		}

		Query query = sessionFactory.getCurrentSession()
				.createQuery("from " + libraryClassName + " where remote = false and enabled = true order by name");
		
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = (List<Library>) query.list();
		return libraries;
	}

	@Override
	public List<Library> getLibraries(LibraryType libraryType) {
		String libraryClassName = null;

		if (libraryType == LibraryType.MUSIC) {
			libraryClassName = MusicLibrary.class.getName();
		} else {
			libraryClassName = Library.class.getName();
		}

		Query query = sessionFactory.getCurrentSession().createQuery("from " + libraryClassName + " order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = (List<Library>) query.list();
		return libraries;
	}

	@Override
	public void saveLibrary(Library library) {
		saveOrUpdate(library);
	}

	@Override
	public Library getLibrary(long id) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Library where id = :id");
		query.setLong("id", id);
		query.setCacheable(true);
		Library library = (Library) query.uniqueResult();
		return library;
	}

	@Override
	public Library getRemoteLibrary(String uniqueName) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("select l from Library l inner join l.remoteShares rs where rs.uniqueName = :uniqueName");
		query.setString("uniqueName", uniqueName);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = query.list();
		if (libraries == null || libraries.isEmpty()) {
			return null;
		}

		if (libraries.size() > 1) {
			logger.error("More than one library found for uniqueName = " + uniqueName);
		}

		Library library = libraries.get(0);
		return library;
	}

	@Override
	public boolean hasRemoteLibrary(String path) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("select l from Library l where l.location.path = :path");
		query.setString("path", path);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = query.list();
		if (libraries == null || libraries.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public Library getRemoteLibrary(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Library where id = :id and remote = true");
		query.setLong("id", libraryId);
		query.setCacheable(true);
		Library library = (Library) query.uniqueResult();
		return library;
	}

	@Override
	public void deleteLibrary(Library library) {

		long libraryId = library.getId();

		Query deleteVotesQuery = sessionFactory.getCurrentSession().createQuery(
				"delete Vote v where id in (select id from Vote v where v.mediaItem.library.id = :libraryId)");
		deleteVotesQuery.setLong("libraryId", libraryId);
		deleteVotesQuery.executeUpdate();

		Query deletePlaylistMediaQuery = sessionFactory.getCurrentSession().createQuery(
				"delete PlaylistMediaItem where id in (select pmi.id from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId)");
		deletePlaylistMediaQuery.setLong("libraryId", libraryId);
		deletePlaylistMediaQuery.executeUpdate();

		// if (library instanceof MusicLibrary) {
		// Query deleteSongsQuery =
		// sessionFactory.getCurrentSession().createQuery(
		// "delete Song where id in (select s.id from Song s where s.library.id
		// = :libraryId)");
		// deleteSongsQuery.setLong("libraryId", libraryId);
		// deleteSongsQuery.executeUpdate();
		// } else if (library instanceof VideoLibrary) {
		//
		// }

		Query deleteMediaItemsQuery = sessionFactory.getCurrentSession().createQuery(
				"delete MediaItem where id in (select mi.id from MediaItem mi where mi.library.id = :libraryId)");
		deleteMediaItemsQuery.setLong("libraryId", libraryId);
		deleteMediaItemsQuery.executeUpdate();

		sessionFactory.getCurrentSession().evict(library);
		library = getLibrary(libraryId);
		sessionFactory.getCurrentSession().delete(library);
	}

	@Override
	public List<Library> getLibrariesForGroup(long groupId) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("select l from Library l inner join l.groups g where g.id = :groupId order by l.name");
		query.setLong("groupId", groupId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = (List<Library>) query.list();
		return libraries;
	}

	@Override
	public RemoteShare getRemoteShare(Long remoteShareId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from RemoteShare where id = :remoteShareId");
		query.setLong("remoteShareId", remoteShareId);
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<RemoteShare> remoteShares = (List<RemoteShare>) query.list();
		if (remoteShares == null || remoteShares.isEmpty()) {
			return null;
		}

		return remoteShares.get(0);
	}

	@Override
	public void saveRemoteShare(RemoteShare remoteShare) {
		if (remoteShare.getId() == 0) {
			throw new MashupMediaRuntimeException("Only existing remote shares can be saved!");
		}

		sessionFactory.getCurrentSession().merge(remoteShare);
	}

	@Override
	public void reinitialiseLibrary(Library library) {
		long totalMediaItems = getTotalMediaItemsFromLibrary(library.getId());
		if (totalMediaItems == 0) {
			return;
		}

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update MediaItem set fileLastModifiedOn = :fileLastModifiedOn where library.id = :libraryId");
		query.setLong("fileLastModifiedOn", 0);
		query.setLong("libraryId", library.getId());
		int totalItemsUpdated = query.executeUpdate();
		logger.info("Total media items reinitialised: " + totalItemsUpdated);
	}

	@Override
	public long getTotalMediaItemsFromLibrary(long libraryId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select count(mi.id) from MediaItem mi where mi.library.id = :libraryId ");
		Query query = sessionFactory.getCurrentSession().createQuery(queryBuilder.toString());
		query.setCacheable(true);
		query.setLong("libraryId", libraryId);
		Long totalMediaItems = (Long) query.uniqueResult();
		return totalMediaItems;
	}

}
