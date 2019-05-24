package org.mashupmedia.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDaoImpl {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext
	protected EntityManager entityManager;

	protected Session getCurrentSession() {
		Session session = entityManager.unwrap(Session.class);
		return session;
	}

	protected void saveOrUpdate(Object object) {

		if (object == null) {
			logger.info("Unable to save or update object because it is null");
			return;
		}
		getCurrentSession().saveOrUpdate(object);
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
			getCurrentSession().save(object);
		} else {
			getCurrentSession().merge(object);
		}

	}

	protected Long getObjectId(Object object)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
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

		getCurrentSession().flush();
		getCurrentSession().clear();
		logger.debug("Flushed and cleared session.");
	}

}
