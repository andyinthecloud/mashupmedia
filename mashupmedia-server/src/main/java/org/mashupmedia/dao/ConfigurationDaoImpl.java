package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationDaoImpl extends BaseDaoImpl implements ConfigurationDao {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Configuration getConfiguration(String key) {
		Query query = getCurrentSession().createQuery("from Configuration where key = :key");
		query.setString("key", key);
		query.setCacheable(true);		
		@SuppressWarnings("unchecked")
		List<Configuration> configurations = query.list();
		if (configurations == null || configurations.isEmpty()) {
			return null;
		}
		
		Configuration configuration = configurations.get(0);
		return configuration;
	}

	@Override
	public void saveConfiguration(Configuration configuration) {
		getCurrentSession().saveOrUpdate(configuration);
	}

	@Override
	public void indexMediaItems() {
//		logger.info("About to start indexing...");
//		FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
//		try {
//			fullTextSession.createIndexer().startAndWait();
//			logger.info("Indexation finished.");
//		} catch (InterruptedException e) {
//			throw new MashupMediaRuntimeException("Error indexing content", e);
//		}
	}

}
