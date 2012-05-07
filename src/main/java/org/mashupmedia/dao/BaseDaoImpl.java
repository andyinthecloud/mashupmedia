package org.mashupmedia.dao;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDaoImpl {
	protected Logger logger = Logger.getLogger(getClass());

	@Autowired
	protected SessionFactory sessionFactory;

	protected void saveOrUpdate(Object object) {
		if (object == null) {
			logger.info("Unable to save or update object because it is null");
			return;
		}
		sessionFactory.getCurrentSession().saveOrUpdate(object);
	}

}
