package org.mashupmedia.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.mashupmedia.exception.MashupMediaRuntimeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseDaoImpl {

	@PersistenceContext
	protected EntityManager entityManager;

	protected void saveOrUpdate(Object entity) {
		if (entity == null) {
			log.info("Unable to save or update object because it is null");
			return;
		}

		entityManager.persist(entity);
	}
	
	protected void saveOrMerge(Object entity) {
		if (entity == null) {
			log.info("Unable to save or update object because it is null");
			return;
		}
		
		
		Long id;
		try {
			id = getObjectId(entity);
		} catch (Exception e) {
			throw new MashupMediaRuntimeException("Unable to get id for object: " + entity.toString());
		} 
		
		if (id == null) {
			throw new MashupMediaRuntimeException("Unable to get id for object: " + entity.toString());			
		}
		
		if (id == 0) {
			entityManager.persist(entity);
		} else {
			entityManager.merge(entity);
		}
		
	}

	protected <T> T getUniqueResult(TypedQuery<T> query) {
		List<T> items = query.getResultList();	
		if (items == null || items.isEmpty()) {
//			throw new MashupMediaRuntimeException("Unable to get a unique result as items are empty.");
			return null;
		}

		if (items.size() > 1) {
			throw new MashupMediaRuntimeException("Unable to get a unique result as there are more than one item.");

		}

		return items.get(0);
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

		entityManager.flush();
		entityManager.clear();
		log.debug("Flushed and cleared session.");
	}

}
