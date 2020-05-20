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
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.util.FileHelper;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	// public DataSource createDataSource() {
		
	// 	DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

	// 	// ComboPooledDataSource dataSource = new ComboPooledDataSource();
	// 		dataSourceBuilder.driverClassName("org.h2.Driver");
	// 	String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
	// 	dataSourceBuilder.url("jdbc:h2:file:" + applicationFolderPath + "/db;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
	// 	dataSourceBuilder.username("sa");
	// 	dataSourceBuilder.password("");
	// 	// dataSourceBuilder.poolsetMinPoolSize(3);
	// 	// dataSourceBuilder.posetMaxPoolSize(20);
	// 	// dataSourceBuilder.setMaxIdleTime(600);
	// 	return dataSourceBuilder.build();
	// }

	public LocalSessionFactoryBean createSessionFactory() {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(getDataSource());
		localSessionFactoryBean.setAnnotatedPackages(new String[] { "org.mashupmedia.model" });

		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
		hibernateProperties.setProperty("hibernate.jdbc.batch_size", "20");
		hibernateProperties.setProperty("hibernate.cache.region.factory_class",
				"org.hibernate.cache.ehcache.EhCacheRegionFactory");
		hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
		hibernateProperties.setProperty("hibernate.query.substitutions", "true '1', false '0'");
		hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
		hibernateProperties.setProperty("hibernate.search.default.directory_provider", "filesystem");
		File indexBaseFolder = new File(FileHelper.getApplicationFolder(), "lucene/indexes");
		hibernateProperties.setProperty("hibernate.search.default.indexBase", indexBaseFolder.getAbsolutePath());
		localSessionFactoryBean.setHibernateProperties(hibernateProperties);

		localSessionFactoryBean.setPackagesToScan("org.mashupmedia.model", "org.mashupmedia.model.library",
				"org.mashupmedia.model.location", "org.mashupmedia.model.media", "org.mashupmedia.model.playlist");

		return localSessionFactoryBean;
	}
	
	
	public void shutdown() {
		// Shut down the database cleanly
        try {
        	Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("SHUTDOWN");
            statement.close();
        } catch (Exception e) {
        	log.error("Error shutting down", e);
        }
        
        try {
        	Connection connection = dataSource.getConnection();
            connection.close();
        } catch (Exception e) {
        	log.error("Error shutting down", e);
        }
	}
	
	
	

}
