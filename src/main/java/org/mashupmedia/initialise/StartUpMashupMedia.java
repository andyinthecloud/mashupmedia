package org.mashupmedia.initialise;

import java.beans.PropertyVetoException;

import org.mashupmedia.util.FileHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
public class StartUpMashupMedia {

	@Bean( name="dataSource")
	public ComboPooledDataSource createComboPooledDataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.hsqldb.jdbcDriver");
//			dataSource.setDriverClass("org.hsqldb.jdbc.JDBCDriver");
			
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
		dataSource.setJdbcUrl("jdbc:hsqldb:file:" + applicationFolderPath + "/db;shutdown=true;hsqldb.write_delay_millis=0;hsqldb.tx=mvcc");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		dataSource.setMinPoolSize(2);
		dataSource.setMaxPoolSize(5);
		dataSource.setMaxIdleTime(600);
		return dataSource;
	}
	
	
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		
//		
//		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
//
//		ComboPooledDataSource dataSource = new ComboPooledDataSource();
//		try {
//			dataSource.setDriverClass("org.hsqldb.jdbcDriver");
//		} catch (PropertyVetoException e) {
//			throw new RuntimeException(e);
//		}
//		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
//		dataSource.setJdbcUrl("jdbc:hsqldb:file:" + applicationFolderPath + "/db;shutdown=true;hsqldb.write_delay_millis=0;hsqldb.tx=mvcc");
//		dataSource.setUser("sa");
//		dataSource.setPassword("");
//		dataSource.setMinPoolSize(2);
//		dataSource.setMaxPoolSize(5);
//		dataSource.setMaxIdleTime(600);
//
//		beanFactory.createBean()
//		
//		beanFactory.autowireBean(dataSource);
//		beanFactory.initializeBean(dataSource, "dataSource");
//	}
//
//	@Override
//	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//		// TODO Auto-generated method stub
//		
//	}

}
