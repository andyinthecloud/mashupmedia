package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.exception.MashupMediaException;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.library.RemoteShare;
import org.mashupmedia.service.LibraryManager.LibraryType;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryDaoImpl extends BaseDaoImpl implements LibraryDao {

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
		query.setParameter("id", id);
		query.setCacheable(true);
		Library library = (Library) query.uniqueResult();
		return library;
	}

	@Override
	public void deleteLibrary(Library library) {
		long libraryId = library.getId();
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
			throw new MashupMediaException("Only existing remote shares can be saved!");
		}

		sessionFactory.getCurrentSession().merge(remoteShare);
	}

	@Override
	public List<Library> getRemoteLibraries() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Library where remote = true order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Library> libraries = (List<Library>) query.list();
		return libraries;
	}

}
