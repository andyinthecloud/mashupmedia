package org.mashupmedia.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.proxy.ProxyFile;
import org.mashupmedia.web.proxy.ProxyTextFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProxyManagerImpl implements ProxyManager{

	private Map<String, ProxyFile> proxyCache = new HashMap<String, ProxyFile>(); 

	@Autowired
	private ConfigurationManager configurationManager;
	
	@Autowired
	private ConnectionManager connectionManager;
	
	public static int DEFAULT_CACHE_SECONDS = 600;
	
	
	@Override
	public ProxyFile loadProxyFile(String url, ProxyType proxyType) throws IOException {
		Date date = new Date();
		String key = createKey(proxyType, url);
		int cacheSeconds = NumberUtils.toInt(MessageHelper.getMessage(MashUpMediaConstants.PROXY_CACHE_SECONDS));
		if (cacheSeconds < 1) {
			cacheSeconds = DEFAULT_CACHE_SECONDS;
		}
		
		
		if (proxyCache.containsKey(url)) {
			ProxyFile proxyFile = proxyCache.get(key);
			long seconds = (date.getTime() - proxyFile.getDate().getTime()) / 1000;
			if (cacheSeconds > seconds) {
				return proxyFile;
			}

			proxyCache.remove(proxyFile);
		}
		
		
		InputStream inputStream = connectionManager.connect(url);
		
		byte[] bytes = IOUtils.toByteArray(inputStream);
		
		ProxyFile proxyFile = null;
		
		if (proxyType == ProxyType.TEXT_FILE) {
			proxyFile = new ProxyTextFile(bytes);
		} else {
			proxyFile = new ProxyFile(bytes);
		}
		proxyFile.processBytes();
		
		proxyCache.put(key, proxyFile);
		
		return proxyFile;
	}
	
	private String createKey(ProxyType proxyType, String url) {
		String key = proxyType.toString() + "-" + url;
		return key;
	}
}
