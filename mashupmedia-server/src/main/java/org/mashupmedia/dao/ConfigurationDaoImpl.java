package org.mashupmedia.dao;

import java.util.List;

import org.mashupmedia.model.Configuration;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;

@Repository
public class ConfigurationDaoImpl extends BaseDaoImpl implements ConfigurationDao {

	@Override
	public Configuration getConfiguration(@Param("key") String key) {
		Query query = entityManager.createQuery("from Configuration where key = :key");
		query.setParameter("key", key);
		@SuppressWarnings("unchecked")
		List<Configuration> configurations = query.getResultList();
		if (configurations == null || configurations.isEmpty()) {
			return null;
		}

		Configuration configuration = configurations.get(0);
		return configuration;
	}

	@Override
	public void saveConfiguration(Configuration configuration) {
		saveOrUpdate(configuration);
	}

	@Override
	public void indexMediaItems() {

		throw new UnsupportedOperationException("no longer supported");

		// log.info("About to start indexing...");
		// FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		// try {
		// 	fullTextEntityManager.createIndexer().startAndWait();
		// 	log.info("Indexation finished.");
		// } catch (InterruptedException e) {
		// 	throw new MashupMediaRuntimeException("Error indexing content", e);
		// }
	}

}
