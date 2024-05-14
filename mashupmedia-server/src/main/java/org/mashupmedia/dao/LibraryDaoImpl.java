package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class LibraryDaoImpl extends BaseDaoImpl implements LibraryDao {

	@Override
	public List<Library> getLocalLibraries(LibraryType libraryType) {
		String libraryClassName = null;

		if (libraryType == LibraryType.MUSIC) {
			libraryClassName = MusicLibrary.class.getName();
		} else {
			libraryClassName = Library.class.getName();
		}

		Query query = entityManager
				.createQuery("from " + libraryClassName + " where remote = false and enabled = true order by name");
		@SuppressWarnings("unchecked")
		List<Library> libraries = query.getResultList();
		return libraries;
	}

	@Override
	public List<Library> getLibraries() {
		TypedQuery<Library> query = entityManager.createQuery("from Library order by name",
				Library.class);
		List<Library> libraries = query.getResultList();
		return libraries;
	}

	@Override
	public List<Library> getLibraries(String username) {
		StringBuilder hqlBuilder = new StringBuilder("select l from Library l");
		hqlBuilder.append(" join l.user u");
		hqlBuilder.append(" where u.username = :username");
		hqlBuilder.append(" order by l.name");
		TypedQuery<Library> query = entityManager.createQuery(hqlBuilder.toString(), Library.class);
		query.setParameter("username", username);
		return query.getResultList();
	}

	@Override
	public void saveLibrary(Library library) {
		saveOrMerge(library);
	}

	@Override
	public Library getLibrary(long id) {
		TypedQuery<Library> query = entityManager.createQuery("from Library where id = :id", Library.class);
		query.setParameter("id", id);
		Library library = getUniqueResult(query);
		return library;
	}

	@Override
	public void deleteLibrary(Library library) {

		long libraryId = library.getId();

		Query deleteVotesQuery = entityManager.createQuery(
				"delete Vote v where id in (select id from Vote v where v.mediaItem.library.id = :libraryId)");
		deleteVotesQuery.setParameter("libraryId", libraryId);
		deleteVotesQuery.executeUpdate();

		Query deletePlaylistMediaQuery = entityManager.createQuery(
				"delete PlaylistMediaItem where id in (select pmi.id from PlaylistMediaItem pmi where pmi.mediaItem.library.id = :libraryId)");
		deletePlaylistMediaQuery.setParameter("libraryId", libraryId);
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

		Query deleteMediaItemsQuery = entityManager.createQuery(
				"delete MediaItem where id in (select mi.id from MediaItem mi where mi.library.id = :libraryId)");
		deleteMediaItemsQuery.setParameter("libraryId", libraryId);
		deleteMediaItemsQuery.executeUpdate();

		entityManager.detach(library);
		library = getLibrary(libraryId);
		entityManager.remove(library);
	}

	@Override
	public List<Library> getLibrariesForGroup(long groupId) {
		TypedQuery<Library> query = entityManager
				.createQuery("select l from Library l inner join l.groups g where g.id = :groupId order by l.name",
						Library.class);
		query.setParameter("groupId", groupId);
		List<Library> libraries = query.getResultList();
		return libraries;
	}

	@Override
	public void reinitialiseLibrary(Library library) {
		long totalMediaItems = getTotalMediaItemsFromLibrary(library.getId());
		if (totalMediaItems == 0) {
			return;
		}

		Query query = entityManager.createQuery(
				"update MediaItem set fileLastModifiedOn = :fileLastModifiedOn where library.id = :libraryId");
		query.setParameter("fileLastModifiedOn", 0l);
		query.setParameter("libraryId", library.getId());
		int totalItemsUpdated = query.executeUpdate();
		log.info("Total media items reinitialised: " + totalItemsUpdated);
	}

	@Override
	public long getTotalMediaItemsFromLibrary(long libraryId) {
		StringBuilder queryBuilder = new StringBuilder(
				"select count(mi.id) from MediaItem mi where mi.library.id = :libraryId ");
		TypedQuery<Long> query = entityManager.createQuery(queryBuilder.toString(), Long.class);
		query.setParameter("libraryId", libraryId);
		Long totalMediaItems = getUniqueResult(query);
		return totalMediaItems;
	}

}
