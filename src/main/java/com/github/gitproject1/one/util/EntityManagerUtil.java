package com.github.rreinert.project1.util;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;

public class EntityManagerUtil {
	
	private static EntityManagerUtil instance;
	private static EntityManagerFactory factory;
	private static EntityManager entityManager;
	
	private EntityManagerUtil(){
	}
	
	public static EntityManagerUtil getInstance() {
		if (instance == null){
			instance = new EntityManagerUtil();
			initializeEntityManager();
		}
		return instance;
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return factory;
	}

	private static void initializeEntityManager() {
		DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		String persistenceUnit = "default";
		Map<Object, Object> properties = PropertiesLoader.loadProperties();
		properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		properties.put(PersistenceUnitProperties.WEAVING, "static");

		factory = Persistence.createEntityManagerFactory(persistenceUnit, properties);
	}
	
	public EntityManager getEntityManager() {
		if (entityManager == null){
			entityManager = factory.createEntityManager();
		}
		return entityManager;
	}

	public static void setEntityManager(EntityManager entityManager) {
		EntityManagerUtil.entityManager = entityManager;
	}
	
}
