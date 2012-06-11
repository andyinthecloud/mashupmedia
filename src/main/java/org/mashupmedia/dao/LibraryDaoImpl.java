package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.MusicLibrary;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryDaoImpl extends BaseDaoImpl implements LibraryDao {

	@Override
	public List<MusicLibrary> getMusicLibraries() {
		Query query = sessionFactory.getCurrentSession().createQuery("from MusicLibrary order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<MusicLibrary> musicLibraries = (List<MusicLibrary>) query.list();
		return musicLibraries;
	}

	@Override
	public void saveMusicLibrary(MusicLibrary musicLibrary) {
		long id = musicLibrary.getId();

		if (id > 0) {
			sessionFactory.getCurrentSession().merge(musicLibrary);
		} else {
			sessionFactory.getCurrentSession().save(musicLibrary);
		}
	}

	@Override
	public MusicLibrary getMusicLibrary(long id) {
		Query query = sessionFactory.getCurrentSession().createQuery("from MusicLibrary where id = :id");
		query.setParameter("id", id);
		query.setCacheable(true);
		MusicLibrary musicLibrary = (MusicLibrary) query.uniqueResult();
		return musicLibrary;
	}

	@Override
	public void deleteLibrary(Library library) {
		long libraryId = library.getId();
		sessionFactory.getCurrentSession().evict(library);
		library = getMusicLibrary(libraryId);
		sessionFactory.getCurrentSession().delete(library);
	}

}
