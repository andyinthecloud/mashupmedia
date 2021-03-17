package org.mashupmedia.dao;

import org.mashupmedia.model.Configuration;

public interface ConfigurationDao {
	
	public Configuration getConfiguration(String key);
	
	public void saveConfiguration(Configuration configuration);

	public void indexMediaItems();
	

}
