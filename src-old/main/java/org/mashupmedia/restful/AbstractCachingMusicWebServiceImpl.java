package org.mashupmedia.restful;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;

public abstract class AbstractCachingMusicWebServiceImpl implements MusicWebService {
	private Logger logger = Logger.getLogger(getClass());
	
	private Map<String, RemoteMediaMetaItem> remoteMediaCache = new HashMap<String, RemoteMediaMetaItem>();
	public static int MAXIMUM_SECONDS_IN_CACHE = 86400;

	
	protected RemoteMediaMetaItem getRemoteMediaItemFromCache(String remoteId) {
		
		Date date = new Date();
		
		RemoteMediaMetaItem cachedRemoteMediaMetaItem = remoteMediaCache.get(remoteId);
		if (cachedRemoteMediaMetaItem != null) {
			long secondsInCache = (date.getTime() - cachedRemoteMediaMetaItem.getDate().getTime()) / 1000;
			
			if (secondsInCache < MAXIMUM_SECONDS_IN_CACHE) {
				return cachedRemoteMediaMetaItem;
			}
			
			remoteMediaCache.remove(cachedRemoteMediaMetaItem);

		}		

		return null;
	}
	

	protected void addRemoteMediaItemToCache(RemoteMediaMetaItem remoteMediaMetaItem) {
		remoteMediaMetaItem.setDate(new Date());
		String remoteId = remoteMediaMetaItem.getRemoteId();
		if (StringUtils.isBlank(remoteId)) {
			logger.error("Unable to add remote media item to cache, remote id is empty.");
			return;
		}
		
		remoteMediaCache.put(remoteId, remoteMediaMetaItem);
		
	}
	
}
