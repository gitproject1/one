package com.github.gitproject1.one.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gitproject1.one.dao.DynamicEntityDAO;
import com.github.gitproject1.one.util.EntityManagerUtil;

@Service("dynamicEntityService")
public class DynamicEntityService {
	
	@Autowired
	private DynamicEntityDAO dynamicEntityDAO;
	
	public void save(DynamicEntity t) throws Exception {
		
		try {
			dynamicEntityDAO.beginTransaction();
			
			Integer id = t.get("id");
			if ( (id == null) || (id.longValue() == 0)) {
				dynamicEntityDAO.insert(t);	
			} else {
				dynamicEntityDAO.update(t);
			}
			
			dynamicEntityDAO.commitTransaction();
			
		} catch (Exception e) {
			dynamicEntityDAO.rollbackTransaction();
			throw new RuntimeException(e);
		}
	}
	
	public void delete(DynamicEntity t) {
		try {
			dynamicEntityDAO.beginTransaction();
			dynamicEntityDAO.delete(t);
			dynamicEntityDAO.commitTransaction();
			
		} catch (Exception e) {
			dynamicEntityDAO.rollbackTransaction();
			throw new RuntimeException(e);
		}
	}
	
	public DynamicEntity find(Object id) {
		return dynamicEntityDAO.find(DynamicEntity.class, id);
	}
	
	public DynamicEntity findByName(String alias, String name) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("name", name);
		List<DynamicEntity> entities = findEntities("select e from "+alias+" e where e.name = :name", parameters);
		if ( (entities != null) && (entities.size() > 0)){
			return entities.get(0);
		}
		
		return null;
	}
	
	public DynamicEntity findById(String alias, Integer id) {
		
		EntityManager em = EntityManagerUtil.getInstance().getEntityManager();
		ClassDescriptor descriptor = JpaHelper.getEntityManager(em).getServerSession().getDescriptorForAlias(alias);
		
		return dynamicEntityDAO.find(descriptor.getJavaClass(), id);
	}

	
	public DynamicEntity findByNameAndEntity(String alias, String name, String entityName) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("name", name);
		parameters.put("entityName", entityName);
		List<DynamicEntity> entities = findEntities("select e from "+alias+" e where e.name = :name and e.entity.name = :entityName", parameters);
		if ( (entities != null) && (entities.size() > 0)){
			return entities.get(0);
		}
		
		return null;
	}
	
	public List<DynamicEntity> findAll(String alias) {
		return findAll(alias, -1, -1);
	}

	@SuppressWarnings("unchecked")
	public List<DynamicEntity> findAll(String alias, int firstResult, int maxResults) {
		
		List<DynamicEntity> ret = new ArrayList<DynamicEntity>();
		EntityManager em = EntityManagerUtil.getInstance().getEntityManager();
        ClassDescriptor descriptor = JpaHelper.getEntityManager(em).getServerSession().getDescriptorForAlias(alias);
        
        if (descriptor != null){
        
	        ReadAllQuery raq = new ReadAllQuery(descriptor.getJavaClass());
	        Query query = JpaHelper.createQuery(raq, em);
	        
	        if (firstResult != -1) {
	        	query.setFirstResult(firstResult);
	        }
	        
	        if (maxResults != -1) {
	        	query.setMaxResults(maxResults);
	        }
	        
	        ret = query.getResultList();
        }
        return ret;
	}
	
    public DynamicEntity newInstance(String entityAlias) {
		EntityManagerFactory emf = EntityManagerUtil.getInstance().getEntityManagerFactory();
        JPADynamicHelper helper = new JPADynamicHelper(emf);
        DynamicEntity entity = helper.getType(entityAlias).newDynamicEntity();
        return entity;
    }
	
	public List<DynamicEntity> findEntities(String hql, Map<String, Object> parameters) {
    	return findEntities(hql, parameters, -1, -1);
    }
	
	@SuppressWarnings("unchecked")
	public List<DynamicEntity> findEntities(String hql, Map<String, Object> parameters, int firstResult, int maxResults) {
    	EntityManager em = EntityManagerUtil.getInstance().getEntityManager();
     
    	Query query = em.createQuery(hql);
        
        if (parameters != null) {
	        for (String key : parameters.keySet()) {
				Object parameter = parameters.get(key);
				query.setParameter(key, parameter);
			}
        }
        
        if (firstResult != -1) {
        	query.setFirstResult(firstResult);
        }
        
        if (maxResults != -1) {
        	query.setMaxResults(maxResults);
        }
        
		return query.getResultList();
    }
    
	public int count(String entity) {
		EntityManager em = EntityManagerUtil.getInstance().getEntityManager();		
		return ((Number) em.createQuery("SELECT COUNT(e) FROM " + entity + " e").getSingleResult()).intValue();
	}

	public void saveDynamicEntity(DynamicEntity e) {
		EntityManagerFactory emf = EntityManagerUtil.getInstance().getEntityManagerFactory();
		loadDynamicEntity(e);
		JPADynamicHelper helper = new JPADynamicHelper(emf);
		new SchemaManager(helper.getSession()).extendDefaultTables(true);
	}
	
	public void deleteDynamicEntity(DynamicEntity e) {
		JPADynamicTypeBuilder dynamicTypeBuilder = getDynamicTypeBuilder(e);
		EntityManagerFactory emf = EntityManagerUtil.getInstance().getEntityManagerFactory();
		DynamicType type = dynamicTypeBuilder.getType();
		JPADynamicHelper helper = new JPADynamicHelper(emf);
		helper.addTypes(true, true, type);
		new SchemaManager(helper.getSession()).dropDefaultTables();
	}
	
	public void loadDynamicEntities() {
		List<DynamicEntity> entities = findAll("Entity");
		for (DynamicEntity e : entities) {
			loadDynamicEntity(e);
		}
	}
	
	private JPADynamicTypeBuilder getDynamicTypeBuilder(DynamicEntity e){
		DynamicType parentType = null;
		try {
			DynamicEntity parent = e.get("parent");
			if (parent != null) {
				JPADynamicTypeBuilder parentBuilder = getDynamicTypeBuilder(parent);
				parentType = parentBuilder.getType();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		
		String packagePrefix = "";
		String name = e.get("name");
		Class<?> dynamicClass = dcl.createDynamicClass(packagePrefix + name);
		String tableName = "system_" + name.toLowerCase();
		
		return new JPADynamicTypeBuilder(dynamicClass, parentType, tableName);
	}
	
	private void configureDefaultId(DynamicEntity e, JPADynamicTypeBuilder dynamicTypeBuilder) {
		try {
			
			String id = "id";
			dynamicTypeBuilder.setPrimaryKeyFields(id);
			dynamicTypeBuilder.addDirectMapping(id.toLowerCase(), Integer.class, id);
			dynamicTypeBuilder.configureSequencing(e.get("name")+"_"+"seq", id);
			
			DynamicEntity idAttribute = null;
			String entityName = e.get("name");
			DynamicEntity issueNo = findByNameAndEntity("Attribute", "id", entityName);
			if ( issueNo != null ){
				idAttribute = issueNo;
			} else {
				idAttribute = newInstance("Attribute");
				idAttribute.set("name", "id");
				idAttribute.set("entity", e);
				idAttribute.set("dataType", "Integer");
				idAttribute.set("orderNo", 0);
				idAttribute.set("editable", false);
				save(idAttribute);
			}
		
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	private void configureAttributes(DynamicEntity e, JPADynamicTypeBuilder dynamicTypeBuilder) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String name = e.get("name");
		parameters.put("name", name);
		List<DynamicEntity> attributes = findEntities("select a from Attribute a where a.entity.name = :name", parameters);
		for (DynamicEntity attribute : attributes) {
			String attributeName = (String) attribute.get("name");
			if (! dynamicTypeBuilder.getType().containsProperty(attributeName)){
				Class<?> classType = getClassType(attribute);
				dynamicTypeBuilder.addDirectMapping(attributeName, classType, attributeName);
			}
		}
	}
	
	private Class<?> getClassType(DynamicEntity attribute){
		
		Class<?> ret = null;
		Object type = attribute.get("dataType");
		
		if (type.equals("String")){
			return String.class; 
		} else if (type.equals("Boolean")){
			return Boolean.class;
		} else if (type.equals("Integer")){
			return Integer.class;
		} else if (type.equals("Date")){
			return Date.class;
		}else if (type instanceof Class<?>) {
			ret = (Class<?>) type;
		}
		return ret;
	}

	private void loadDynamicEntity(DynamicEntity e) {
		
		EntityManagerFactory emf = EntityManagerUtil.getInstance().getEntityManagerFactory();
		JPADynamicTypeBuilder dynamicTypeBuilder = getDynamicTypeBuilder(e);
		configureDefaultId(e, dynamicTypeBuilder);
		configureAttributes(e, dynamicTypeBuilder);
		DynamicType type = dynamicTypeBuilder.getType();
		JPADynamicHelper helper = new JPADynamicHelper(emf);
		
		helper.addTypes(true, true, type);
	}
	
	public void loadSystemEntities() {
		
		EntityManagerFactory emf = EntityManagerUtil.getInstance().getEntityManagerFactory();
		JPADynamicHelper helper = new JPADynamicHelper(emf);
		
		if (! systemEntitiesLoaded(helper)){
			
			List<DynamicType> types = new ArrayList<DynamicType>();
			String packagePrefix = "system.";
			DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
			
			Class<?> entityClass = dcl.createDynamicClass(packagePrefix + "Entity");
			JPADynamicTypeBuilder entity = new JPADynamicTypeBuilder(entityClass, null, "system_entity");
			
			Class<?> attributeClass = dcl.createDynamicClass(packagePrefix + "Attribute");
			JPADynamicTypeBuilder attribute = new JPADynamicTypeBuilder(attributeClass, null, "system_attribute");
			
			configureEntity(types, entity, attribute);
			configureAttribute(types, entity, attribute);
	        
	        DynamicType[] array = new DynamicType[types.size()];
	        types.toArray(array); 
	        helper.addTypes(true, true, array);
	        
	        SchemaManager schemaManager = new SchemaManager(helper.getSession());
			schemaManager.extendDefaultTables(true);
			
			loadSystemData();
			loadSampleData();
		}
	}
	
	private void configureEntity(List<DynamicType> types, JPADynamicTypeBuilder entity, JPADynamicTypeBuilder attribute) {
		entity.setPrimaryKeyFields("id");
		entity.addDirectMapping("id", Integer.class, "id");
		entity.addDirectMapping("name", String.class, "name");
		entity.addOneToOneMapping("parent", entity.getType(), "parent");
		
		OneToManyMapping attrMapping = entity.addOneToManyMapping("attributes", attribute.getType(), "entity");
        attrMapping.setCascadeAll(true);
		
		entity.configureSequencing("Entity_seq", "id");
		types.add(entity.getType());
	}
	
	private void configureAttribute(List<DynamicType> types, 
			JPADynamicTypeBuilder entity, 
			JPADynamicTypeBuilder attribute) {
        
		attribute.setPrimaryKeyFields("id");
		attribute.addDirectMapping("id", Integer.class, "id");
		attribute.addDirectMapping("name", String.class, "name");
		attribute.addDirectMapping("orderNo", Integer.class, "orderNo");
		attribute.addDirectMapping("dataType", String.class, "dataType");
		attribute.addDirectMapping("editable", Boolean.class, "editable");
		
		OneToManyMapping attrMapping = attribute.addOneToManyMapping("attributes", attribute.getType(), "attribute");
        attrMapping.setCascadeAll(true);
        attrMapping.setIsPrivateOwned(true);
		
		attribute.addOneToOneMapping("associationEntity", entity.getType(), "associationEntity");
		attribute.addDirectMapping("associationType", String.class, "associationType");
		
		attribute.addDirectMapping("entityId", int.class, "entity").readOnly();
		attribute.addOneToOneMapping("entity", entity.getType(), "entity");

		attribute.addDirectMapping("attributeId", int.class, "attribute").readOnly();
		attribute.addOneToOneMapping("attribute", attribute.getType(), "attribute");
		
        attribute.configureSequencing("Attribute_seq", "id");
		types.add(attribute.getType());
	}

	private void loadSampleData() {
		
		try {
			
			DynamicEntity issuesEntity = null;
			DynamicEntity issues = findByName("Entity", "Issues");
			if ( issues != null ){
				issuesEntity = issues;
			} else {
				issuesEntity = newInstance("Entity");
				issuesEntity.set("name", "Issues");
				save(issuesEntity);
			}
			
			DynamicEntity descriptionAttribute = null;
			DynamicEntity desc = findByNameAndEntity("Attribute", "description", "Issues");
			if ( desc != null ){
				descriptionAttribute = desc;
			} else {
				descriptionAttribute = newInstance("Attribute");
				descriptionAttribute.set("name", "description");
				descriptionAttribute.set("entity", issuesEntity);
				descriptionAttribute.set("dataType", "String");
				descriptionAttribute.set("orderNo", 2);
				descriptionAttribute.set("editable", true);
				save(descriptionAttribute);
			}
			
			DynamicEntity createdAttribute = null;
			DynamicEntity created = findByNameAndEntity("Attribute", "created", "Issues");
			if ( created != null ){
				createdAttribute = created;
			} else {
				createdAttribute = newInstance("Attribute");
				createdAttribute.set("name", "created");
				createdAttribute.set("entity", issuesEntity);
				createdAttribute.set("dataType", "Date");
				createdAttribute.set("orderNo", 3);
				createdAttribute.set("editable", true);
				save(createdAttribute);
			}

			DynamicEntity issueNumberAttribute = null;
			DynamicEntity issueNo = findByNameAndEntity("Attribute", "issueno", "Issues");
			if ( issueNo != null ){
				issueNumberAttribute = issueNo;
			} else {
				issueNumberAttribute = newInstance("Attribute");
				issueNumberAttribute.set("name", "issueno");
				issueNumberAttribute.set("entity", issuesEntity);
				issueNumberAttribute.set("dataType", "Integer");
				issueNumberAttribute.set("orderNo", 1);
				issueNumberAttribute.set("editable", true);
				
				save(issueNumberAttribute);
			}
			
			DynamicEntity impedimentAttribute = null;
			DynamicEntity impediment = findByNameAndEntity("Attribute", "impediment", "Issues");
			if ( issueNo != null ){
				impedimentAttribute = impediment;
			} else {
				impedimentAttribute = newInstance("Attribute");
				impedimentAttribute.set("name", "impediment");
				impedimentAttribute.set("entity", issuesEntity);
				impedimentAttribute.set("dataType", "Boolean");
				impedimentAttribute.set("orderNo", 4);
				impedimentAttribute.set("editable", true);
				save(impedimentAttribute);
			}
			
			saveDynamicEntity(issuesEntity);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadSystemData() {
		DynamicEntity systemEntity = null;

		try {
			DynamicEntity entity = findByName("Entity", "Entity");
			if ( entity != null ){
				systemEntity = entity;
			} else {
				systemEntity = newInstance("Entity");
				systemEntity.set("name", "Entity");
				
				DynamicEntity enameAttribute = newInstance("Attribute");
				enameAttribute.set("name", "name");
				enameAttribute.set("dataType", "String");
				enameAttribute.set("orderNo", 1);
				enameAttribute.set("editable", true);
				enameAttribute.set("entity", systemEntity);
				
				systemEntity.<Collection<DynamicEntity>> get("attributes").add(enameAttribute);

				save(systemEntity);

				DynamicEntity systemAttribute = newInstance("Entity");
				systemAttribute.set("name", "Attribute");

				DynamicEntity enameAttribute1 = newInstance("Attribute");
				enameAttribute1.set("name", "name");
				enameAttribute1.set("dataType", "String");
				enameAttribute1.set("orderNo", 1);
				enameAttribute1.set("editable", true);
				enameAttribute1.set("entity", systemAttribute);

				DynamicEntity enameAttribute2 = newInstance("Attribute");
				enameAttribute2.set("name", "dataType");
				enameAttribute2.set("dataType", "String");
				enameAttribute2.set("orderNo", 1);
				enameAttribute2.set("editable", true);
				enameAttribute2.set("entity", systemAttribute);

				systemAttribute.<Collection<DynamicEntity>> get("attributes").add(enameAttribute1);
				systemAttribute.<Collection<DynamicEntity>> get("attributes").add(enameAttribute2);

				save(systemAttribute);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Class<?> getClass(String alias){
		EntityManager em = EntityManagerUtil.getInstance().getEntityManager();
		ClassDescriptor descriptor = JpaHelper.getEntityManager(em).getServerSession().getDescriptorForAlias(alias);
		Class<?> javaClass = descriptor.getJavaClass();
		return javaClass;
	}
	
	private boolean systemEntitiesLoaded(JPADynamicHelper helper) {
		DynamicType f = helper.getType("Entity");
		return f != null;
	}
	
}