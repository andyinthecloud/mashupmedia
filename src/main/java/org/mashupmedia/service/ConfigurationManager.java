package org.mashupmedia.service;

import java.util.Date;

import org.mashupmedia.model.Configuration;

public interface ConfigurationManager {
	

	public Configuration getConfiguration(String key);
	
	public void saveConfiguration(Configuration configuration);
	
	public void saveConfiguration(String key, String value);

	public String getConfigurationValue(String key);

	public String getConfigurationDecryptedValue(String proxyPassword);

	public void saveEncryptedConfiguration(String key, String value);

	public void indexMediaItems();

	public Date getConfigurationDate(String configurationKey);

}
