package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.service.LibraryManager.LibraryType;
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

		Query query = sessionFactory.getCurrentSession().createQuery("from " + libraryClassName + " where remote = false order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = (List<Library>) query.list();
		return libraries;
	}

	@Override
	public void saveLibrary(Library library) {
		long id = library.getId();

		if (id > 0) {
			sessionFactory.getCurrentSession().merge(library);
		} else {
			sessionFactory.getCurrentSession().save(library);
		}
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
		Query query = sessionFactory.getCurrentSession().createQuery("select l from Library l inner join l.remoteShares rs where rs.uniqueName = :uniqueName");
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
	public Library getRemoteLibrary(long libraryId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Library where id = :id and remote = true");
		query.setParameter("id", libraryId);
		query.setCacheable(true);
		Library library = (Library) query.uniqueResult();
		return library;
	}
	
	@Override
	public void deleteLibrary(Library library) {
		
		long libraryId = library.getId();
		
		Query deleteVotesQuery = sessionFactory.getCurrentSession().createQuery("delete Vote v where id in (select id from Vote v where v.mediaItem.library.id = :libraryId)");
		deleteVotesQuery.setLong("libraryId", libraryId);
		deleteVotesQuery.executeUpdate();
		
		Query deletePlaylistMediaQuery = sessionFactory.getCurrentSession().createQuery("delete PlaylistMediaItem where id in (select pmi.id from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId)");
		deletePlaylistMediaQuery.setLong("libraryId", libraryId);
		deletePlaylistMediaQuery.executeUpdate();
		
		if (library instanceof MusicLibrary) {			
			Query deleteSongsQuery = sessionFactory.getCurrentSession().createQuery("delete Song where id in (select s.id from Song s where s.library.id = :libraryId)");
			deleteSongsQuery.setLong("libraryId", libraryId);
			deleteSongsQuery.executeUpdate();
		}
		
		sessionFactory.getCurrentSession().evict(library);
		library = getLibrary(libraryId);
		sessionFactory.getCurrentSession().delete(library);
	}

	@Override
	public List<Library> getLibrariesForGroup(long groupId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Library l inner join l.groups g where g.id = :groupId order by l.name");
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


}
