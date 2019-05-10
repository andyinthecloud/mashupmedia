package org.mashupmedia.cache;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheEventLogger implements CacheEventListener<Object, Object> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
		logger.info("Cache event {} for item with key {}. Old value = {}, New value = {}", cacheEvent.getType(),
				cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());

	}

}
