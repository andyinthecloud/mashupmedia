package org.mashupmedia.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.dao.ConfigurationDao;
import org.mashupmedia.model.Configuration;
import org.mashupmedia.util.EncryptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfigurationManagerImpl implements ConfigurationManager {

	@Autowired
	private ConfigurationDao configurationDao;

	@Override
	public Configuration getConfiguration(String key) {
		Configuration configuration = configurationDao.getConfiguration(key);
		return configuration;
	}

	@Override
	public void saveConfiguration(Configuration configuration) {
		configuration.setCreatedOn(new Date());
		configurationDao.saveConfiguration(configuration);
	}

	@Override
	public void saveConfiguration(String key, String value) {
		Configuration configuration = getConfiguration(key);
		if (configuration == null) {
			configuration = new Configuration();
		}

		configuration.setKey(key);
		configuration.setValue(value);
		configurationDao.saveConfiguration(configuration);
	}

	@Override
	public String getConfigurationValue(String key) {
		Configuration configuration = getConfiguration(key);
		if (configuration == null) {
			return "";
		}

		String value = StringUtils.trimToEmpty(configuration.getValue());
		return value;
	}

	@Override
	public String getConfigurationDecryptedValue(String key) {
		String encryptedValue = getConfigurationValue(key);
		String decryptedValue = EncryptionHelper.decryptText(encryptedValue);
		return decryptedValue;
	}

	@Override
	public void saveEncryptedConfiguration(String key, String value) {
		value = StringUtils.trimToEmpty(value);
		String encryptedValue = EncryptionHelper.encryptText(value);
		saveConfiguration(key, encryptedValue);
	}
	
	@Override
	public void indexMediaItems() {
		configurationDao.indexMediaItems();
	}
	
	@Override
	public Date getConfigurationDate(String configurationKey) {
		String value = StringUtils.trimToEmpty(getConfigurationValue(configurationKey));
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		
		long timestamp = NumberUtils.toLong(value);
		if (timestamp == 0) {
			return null;
		}
		
		Date date = new Date(timestamp);
		return date;
	}
}
