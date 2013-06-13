package org.mashupmedia.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.Configuration;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationDaoImpl extends BaseDaoImpl implements ConfigurationDao {
	private Logger logger = Logger.getLogger(getClass()); 

	@Override
	public Configuration getConfiguration(String key) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Configuration where key = :key");
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
		sessionFactory.getCurrentSession().saveOrUpdate(configuration);
	}

	@Override
	public void indexMediaItems() {
		logger.info("About to start indexing...");
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
		try {
			fullTextSession.createIndexer().startAndWait();
			logger.info("Indexation finished.");
		} catch (InterruptedException e) {
			throw new MashupMediaRuntimeException("Error indexing content", e);
		}
	}

}
