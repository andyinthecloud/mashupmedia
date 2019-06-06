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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.mashupmedia.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;


public class MashupMediaServiceLocator {
	private Logger logger = LoggerFactory.getLogger(getClass());

//	private DataSource dataSource;
	
	private static StandardServiceRegistry registry;
	
	 private static SessionFactory sessionFactory;

	public MashupMediaServiceLocator() {
		File logFolder = new File(FileHelper.getApplicationFolder(), "log");
		System.setProperty("log.path", logFolder.getAbsolutePath());
	}

//	public DataSource getDataSource() {
//		return dataSource;
//	}
//
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}

//	public DataSource createDataSource() {
//
//		BasicDataSource dataSource = new BasicDataSource();
//
//		dataSource.setDriverClassLoader(org.h2.Driver.class.getClassLoader());
//		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
//		dataSource.setValidationQuery("SELECT 1");
////		dataSource.setDriver(new org.h2.Driver());
//		dataSource.setUrl("jdbc:h2:file:" + applicationFolderPath + "/db");
//		dataSource.setUsername("sa");
//		dataSource.setPassword("");
//		dataSource.setInitialSize(3);
//		dataSource.setMaxTotal(20);
//		dataSource.setMaxIdle(600);
//		return dataSource;
//	}
	
/*
	public DataSource createDataSource() {
		
//		PoolingDataSource<>

		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassLoader(org.h2.Driver.class.getClassLoader());
		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
		dataSource.setValidationQuery("SELECT 1");
//		dataSource.setDriver(new org.h2.Driver());
		dataSource.setUrl("jdbc:h2:file:" + applicationFolderPath + "/db");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setInitialSize(3);
		dataSource.setMaxTotal(20);
		dataSource.setMaxIdle(600);
		return dataSource;
	}
*/
	
	
//	public DataSource createDataSource() {
//		ComboPooledDataSource dataSource = new ComboPooledDataSource();
//		try {
//			dataSource.setDriverClass("org.h2.Driver");
//		} catch (PropertyVetoException e) {
//			throw new MashupMediaRuntimeException(e.getMessage());
//		}
//		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
//		dataSource.setJdbcUrl("jdbc:h2:file:" + applicationFolderPath + "/db;MV_STORE=FALSE;MVCC=FALSE");
//		dataSource.setUser("sa");
//		dataSource.setPassword("");
//		dataSource.setMinPoolSize(3);
//		dataSource.setMaxPoolSize(20);
//		dataSource.setMaxIdleTime(600);
//		return dataSource;
//	}	
	

	public LocalSessionFactoryBean createSessionFactory_del() {
		
		
		
		
		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
		
		Map<String, Object> settings = new HashMap<>();
		
	     settings.put(Environment.DRIVER, "org.h2.Driver");
	     String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
         settings.put(Environment.URL, "jdbc:h2:file:" + applicationFolderPath + "/db");
         settings.put(Environment.USER, "sa");
         settings.put(Environment.HBM2DDL_AUTO, "update");
         settings.put(Environment.C3P0_MIN_SIZE, 3);
         settings.put(Environment.C3P0_MAX_SIZE, 20);
         settings.put(Environment.C3P0_TIMEOUT, 600);
		
      // Enable second level cache (default value is true)
         settings.put(Environment.USE_SECOND_LEVEL_CACHE, true);

         // Enable Query cache
         settings.put(Environment.USE_QUERY_CACHE, true);

         // Specify cache region factory class
//         settings.put(Environment.CACHE_REGION_FACTORY, JCacheRegionFactory.class);

         // Specify cache provider
//         settings.put("hibernate.javax.cache.provider", EhcacheCachingProvider.class);
 		registryBuilder.applySettings(settings);
 		registry = registryBuilder.build();
 		
 		MetadataSources sources = new MetadataSources(registry).addPackage(Package.getPackage("org.mashupmedia.model"));
 		Metadata metadata = sources.getMetadataBuilder().build();
 		sessionFactory = metadata.getSessionFactoryBuilder().build();
		
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
//		localSessionFactoryBean.setDataSource(getDataSource());
		localSessionFactoryBean.setAnnotatedPackages(new String[] { "org.mashupmedia.model" });

		
		/*
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
		hibernateProperties.setProperty("hibernate.jdbc.batch_size", "20");
//		hibernateProperties.setProperty("hibernate.cache.region.factory_class",
//				"org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
		
		hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", Boolean.TRUE.toString());
		hibernateProperties.setProperty("hibernate.cache.region.factory_class",
				"org.hibernate.cache.jcache.JCacheRegionFactory");
		hibernateProperties.setProperty("hibernate.javax.cache.provider", 
                "org.ehcache.jsr107.EhcacheCachingProvider");
		
		hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
		hibernateProperties.setProperty("hibernate.query.substitutions", "true '1', false '0'");
		//hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
		hibernateProperties.setProperty("hibernate.search.default.directory_provider", "filesystem");
		File indexBaseFolder = new File(FileHelper.getApplicationFolder(), "lucene/indexes");
		hibernateProperties.setProperty("hibernate.search.default.indexBase", indexBaseFolder.getAbsolutePath());
		localSessionFactoryBean.setHibernateProperties(hibernateProperties);
*/


		localSessionFactoryBean.setPackagesToScan("org.mashupmedia.model", "org.mashupmedia.model.library",
				"org.mashupmedia.model.location", "org.mashupmedia.model.media", "org.mashupmedia.model.playlist");

		return localSessionFactoryBean;
	}

//	public void shutdown() {
//		// Shut down the database cleanly
//		try {
//			Connection connection = dataSource.getConnection();
//			Statement statement = connection.createStatement();
//			statement.execute("SHUTDOWN");
//			statement.close();
//		} catch (Exception e) {
//			logger.error("Error shutting down", e);
//		}
//
//		try {
//			Connection connection = dataSource.getConnection();
//			connection.close();
//		} catch (Exception e) {
//			logger.error("Error shutting down", e);
//		}
//	}

}
