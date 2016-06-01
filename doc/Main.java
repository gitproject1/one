package example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class Main {

    public static void main(String[] args) throws Exception {
        runDynamicAPITest();
    }
    
    public static void runDynamicAPITest() {
        DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
        List<DynamicType> types = EmployeeDynamicMappings.createTypes(dcl, "example.jpa.dynamic.model.employee");        
        
        DynamicType[] array = new DynamicType[types.size()];
        types.toArray(array); 
        
        EntityManagerFactory emf = createEntityManagerFactory(dcl, "default");
        
        JPADynamicHelper helper = new JPADynamicHelper(emf);
        helper.addTypes(true, true, array);
        
        // Create database and populate
        new SchemaManager(helper.getSession()).replaceDefaultTables();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        new Samples(emf).persistAll(em);
        em.getTransaction().commit();
        em.clear();

        DynamicType empType = helper.getType("Employee");

        Queries queries = new Queries();

        int minEmpId = queries.minimumEmployeeId(em);
        queries.findEmployee(em, empType, minEmpId);
        queries.findEmployeesUsingGenderIn(em);

        Transactions txn = new Transactions();
        txn.createUsingPersist(em);

        em.close();
        emf.close();
    }
    
    public static EntityManagerFactory createEntityManagerFactory(DynamicClassLoader dcl, String persistenceUnit) {
        Map<Object, Object> properties = new HashMap<Object, Object>();
        
        PropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        
        return Persistence.createEntityManagerFactory(persistenceUnit, properties);
    }
}
