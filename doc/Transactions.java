package example;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.eclipse.persistence.config.PessimisticLock;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;

public class Transactions {

    public DynamicEntity createUsingPersist(EntityManager em) {
        DynamicHelper helper = new JPADynamicHelper(em);

        DynamicType empType = helper.getType("Employee");
        DynamicType addrType = helper.getType("Address");
        DynamicType phoneType = helper.getType("PhoneNumber");

        DynamicEntity emp = (DynamicEntity) empType.newDynamicEntity();
        emp.set("firstName", "Sample");
        emp.set("lastName", "Employee");
        emp.set("gender", "Male");
        emp.set("salary", 123456);

        DynamicEntity address = (DynamicEntity) addrType.newDynamicEntity();
        emp.set("address", address);

        DynamicEntity phone = (DynamicEntity) phoneType.newDynamicEntity();
        phone.set("type", "Mobile");
        phone.set("areaCode", "613");
        phone.set("number", "555-1212");
        phone.set("owner", emp);
        emp.<Collection<DynamicEntity>> get("phoneNumbers").add(phone);

        em.getTransaction().begin();
        em.persist(emp);
        em.getTransaction().commit();

        return emp;
    }

    public DynamicEntity createUsingMerge(EntityManager em) {
        JPADynamicHelper helper = new JPADynamicHelper(em);

        DynamicEntity emp = helper.getType("Employee").newDynamicEntity();
        emp.set("firstName", "Sample");
        emp.set("lastName", "Employee");
        emp.set("gender", "Male");
        emp.set("salary", 123456);

        DynamicEntity address = helper.getType("Address").newDynamicEntity();
        emp.set("address", address);

        DynamicEntity phone = helper.getType("PhoneNumber").newDynamicEntity();
        phone.set("type", "Mobile");
        phone.set("areaCode", "613");
        phone.set("number", "555-1212");
        phone.set("owner", emp);
        emp.<Collection<DynamicEntity>> get("phoneNumbers").add(phone);

        em.getTransaction().begin();
        // When merging the managed instance is returned from the call.
        // Further usage within the transaction must be done with this managed
        // entity.
        emp = (DynamicEntity) em.merge(emp);
        em.getTransaction().commit();

        return emp;
    }

    public DynamicEntity createWithRelationshipsToExistingEntities(EntityManager em) {
        return null;
    }

    public DynamicEntity deleteEntity(EntityManager em) {
        return null;
    }

    public void queriesOnTransactionalState(EntityManager em) {
        em.setFlushMode(FlushModeType.COMMIT);
    }

    public void pessimisticLocking(EntityManager em) throws Exception {

        // Find the Employee with the minimum ID
        int minId = new Queries().minimumEmployeeId(em);

        em.getTransaction().begin();

        // Lock Employee using query with hint
        DynamicEntity emp = (DynamicEntity) em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID").setParameter("ID", minId).setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.Lock).getSingleResult();

        emp.set("salary", emp.<Integer> get("salary") - 1);

        em.flush();
    }

    @SuppressWarnings("unchecked")
    public void updateEmployeeWithCity(EntityManager em) throws Exception {
        em.getTransaction().begin();

        List<Object[]> emps = em.createQuery("SELECT e, e.address.city FROM Employee e").getResultList();
        DynamicEntity emp = (DynamicEntity) emps.get(0)[0];
        emp.set("salary", emp.<Integer> get("salary") + 1);

        em.flush();

        em.getTransaction().rollback();
    }

}
