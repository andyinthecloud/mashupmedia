/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.initialise;

import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Properties;

import javax.sql.DataSource;

import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.util.FileHelper;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MashupMediaServiceLocator {

	private DataSource dataSource;

	
	public MashupMediaServiceLocator() {
		File logFolder = new File(FileHelper.getApplicationFolder(), "log");		
		System.setProperty("log.path", logFolder.getAbsolutePath());
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource createDataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.hsqldb.jdbcDriver");
		} catch (PropertyVetoException e) {
			throw new MashupMediaRuntimeException(e.getMessage());
		}
		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
//		dataSource.setJdbcUrl("jdbc:hsqldb:file:" + applicationFolderPath + "/db;shutdown=true;hsqldb.write_delay_millis=0;hsqldb.tx=mvcc");
		dataSource.setJdbcUrl("jdbc:hsqldb:file:" + applicationFolderPath + "/db;shutdown=true;hsqldb.write_delay_millis=0");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		dataSource.setMinPoolSize(2);
		dataSource.setMaxPoolSize(5);
		dataSource.setMaxIdleTime(600);

		return dataSource;
	}

	public LocalSessionFactoryBean createSessionFactory() {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(getDataSource());
		localSessionFactoryBean.setAnnotatedPackages(new String[] { "org.mashupmedia.model" });

		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
		hibernateProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
		hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
		hibernateProperties.setProperty("hibernate.query.substitutions", "true '1', false '0'");
		hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
		hibernateProperties.setProperty("hibernate.search.default.directory_provider", "filesystem");
		File indexBaseFolder = new File(FileHelper.getApplicationFolder(), "lucene/indexes");
		hibernateProperties.setProperty("hibernate.search.default.indexBase", indexBaseFolder.getAbsolutePath());
		localSessionFactoryBean.setHibernateProperties(hibernateProperties);

		Properties packagesToScanProperties = new Properties();
		packagesToScanProperties.setProperty("", "");
		packagesToScanProperties.setProperty("", "");
		packagesToScanProperties.setProperty("", "");
		packagesToScanProperties.setProperty("", "");
		packagesToScanProperties.setProperty("", "");
		localSessionFactoryBean.setPackagesToScan("org.mashupmedia.model", "org.mashupmedia.model.library", "org.mashupmedia.model.location",
				"org.mashupmedia.model.media", "org.mashupmedia.model.playlist");

		return localSessionFactoryBean;
	}

}