package org.mashupmedia.dao;

import org.hibernate.Query;
import org.mashupmedia.model.Configuration;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationDaoImpl extends BaseDaoImpl implements ConfigurationDao {

	@Override
	public Configuration getConfiguration(String key) {		
		Query query = sessionFactory.getCurrentSession().createQuery("from Configuration where key = :key");
        query.setString("key", key);
        query.setCacheable(true);
        Configuration configuration = (Configuration) query.uniqueResult();        
		return configuration;
	}

	@Override
	public void saveConfiguration(Configuration configuration) {		
		sessionFactory.getCurrentSession().saveOrUpdate(configuration);
	}

}
