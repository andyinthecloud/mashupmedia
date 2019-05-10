package org.mashupmedia.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CacheDaoImpl extends BaseDaoImpl implements CacheDao {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void clearCache() {
//		sessionFactory.getCache().evictCollectionRegions();
		sessionFactory.getCache().evictCollectionData();
//		sessionFactory.getCache().evictEntityRegions();
		sessionFactory.getCache().evictEntityData();
		sessionFactory.getCache().evictQueryRegions();
		sessionFactory.getCache().evictDefaultQueryRegion();
		logger.info("Cleared hibernate cache.");
	}
}
