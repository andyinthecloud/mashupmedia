package org.mashupmedia.dao;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CacheDaoImpl extends BaseDaoImpl implements CacheDao {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Override
	public void clearCache() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		
		
//		sessionFactory.getCache().evictCollectionRegions();
		sessionFactory.getCache().evictCollectionData();
//		sessionFactory.getCache().evictEntityRegions();
		sessionFactory.getCache().evictEntityData();
		sessionFactory.getCache().evictQueryRegions();
		sessionFactory.getCache().evictDefaultQueryRegion();
		logger.info("Cleared hibernate cache.");
	}
}
