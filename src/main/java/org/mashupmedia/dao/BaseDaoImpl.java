package org.mashupmedia.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mashupmedia.exception.MashupMediaRuntimeException;
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
	
	protected void saveOrMerge(Object object) {
		if (object == null) {
			logger.info("Unable to save or update object because it is null");
			return;
		}
		
		
		Long id;
		try {
			id = getObjectId(object);
		} catch (Exception e) {
			throw new MashupMediaRuntimeException("Unable to get id for object: " + object.toString());
		} 
		
		if (id == null) {
			throw new MashupMediaRuntimeException("Unable to get id for object: " + object.toString());			
		}
		
		if (id == 0) {
			sessionFactory.getCurrentSession().save(object);
		} else {
			sessionFactory.getCurrentSession().merge(object);
		}
		
	}
	
	
	protected Long getObjectId(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method[] methods = object.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase("getid")) {
				Long id = (Long) method.invoke(object);
				return id;
			}
		}
		
		return null;
		
	}
	
	
	protected void flushSession(boolean isFlushSession) {
		if (!isFlushSession) {
			return;
		}
		
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
		logger.debug("Flushed and cleared session.");
	}

}
