package com.github.gitproject1.one.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.springframework.stereotype.Repository;

import com.github.gitproject1.one.util.EntityManagerUtil;

@Repository(value="dynamicEntityDAO")
public class DynamicEntityDAO {
	
	EntityManager entityManager = EntityManagerUtil.getInstance().getEntityManager();
	
	public void beginTransaction(){
		entityManager.getTransaction().begin();
	}
	
	public void commitTransaction(){
		entityManager.getTransaction().commit();
	}
	
	public void rollbackTransaction(){
		entityManager.getTransaction().rollback();
	}
	
    public void insert(final DynamicEntity entity) throws PersistenceException {
    	try {
			entityManager.persist(entity);
	        entityManager.flush();
    	} catch (PersistenceException e) {
    		throw e;
    	} catch (Exception e) {
    		throw new PersistenceException(e);
		}
    }
	
    public void update(final DynamicEntity entity) throws PersistenceException {
    	try {
			entityManager.merge(entity);	
	        entityManager.flush();
    	} catch (PersistenceException e) {
    		throw e;
    	} catch (Exception e) {
    		throw new PersistenceException(e);
		}
    }

	
	public void delete(final DynamicEntity entity) throws PersistenceException {
    	try {
            entityManager.remove(entity);
            entityManager.flush();
    	} catch (PersistenceException e) {
    		throw e;
    	} catch (Exception e) {
    		throw new PersistenceException(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DynamicEntity find(final Class clazz, Object id) throws PersistenceException {
		try {
			return (DynamicEntity) this.entityManager.find(clazz, id);
		} catch (NoResultException e) {			
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DynamicEntity> findAll(Class clazz) {
        ReadAllQuery raq = new ReadAllQuery(clazz);
        Query query = JpaHelper.createQuery(raq, entityManager);

        return query.getResultList();
	}

}