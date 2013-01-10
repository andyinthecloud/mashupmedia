package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
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

}
