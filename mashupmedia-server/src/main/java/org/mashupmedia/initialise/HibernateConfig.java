package org.mashupmedia.initialise;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mashupmedia.util.FileHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"org.mashupmedia.dao"})
public class HibernateConfig {
	
//	@Autowired
//	private Environment env;
	
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(dataSource());
		localSessionFactoryBean.setPackagesToScan("org.mashupmedia.model", "org.mashupmedia.model.library",
				"org.mashupmedia.model.location", "org.mashupmedia.model.media", "org.mashupmedia.model.playlist");
		localSessionFactoryBean.setHibernateProperties(hibernateProperties());
		return localSessionFactoryBean;
	}
	
	
	

	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
//	        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
		dataSource.setUrl("jdbc:h2:file:" + applicationFolderPath + "/db");

		dataSource.setUsername("sa");
		dataSource.setPassword("sa");

		return dataSource;
	}

//	@Bean
//	public PlatformTransactionManager hibernateTransactionManager() {
//		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//		return transactionManager;
//	}

	

//	  @Bean
//	    public PlatformTransactionManager hibernateTransactionManager() {
//	        HibernateTransactionManager transactionManager
//	          = new HibernateTransactionManager();
//	        transactionManager.setSessionFactory(sessionFactory().getObject());
//	        return transactionManager;
//	    }
	
	  @Bean
	   public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
	      return new PersistenceExceptionTranslationPostProcessor();
	   }
	
	private final Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		return hibernateProperties;
	}
}
