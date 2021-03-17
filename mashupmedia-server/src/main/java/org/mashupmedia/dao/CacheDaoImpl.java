package org.mashupmedia.dao;

import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CacheDaoImpl extends BaseDaoImpl implements CacheDao {
	
	@Override
	public void clearCache() {
		entityManager.getEntityManagerFactory().getCache().evictAll();
		log.info("Cleared cache.");
	}
}
