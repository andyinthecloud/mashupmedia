package org.mashupmedia.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class CacheDaoImpl extends BaseDaoImpl implements CacheDao {
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void clearCache() {
		sessionFactory.getCache().evictCollectionRegions();
		sessionFactory.getCache().evictEntityRegions();
		sessionFactory.getCache().evictQueryRegions();
		sessionFactory.getCache().evictDefaultQueryRegion();
		logger.info("Cleared hibernate cache.");
	}
}
