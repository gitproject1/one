package example;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.ReadAllQuery;

public class Queries {
    
    public DynamicEntity findEmployee(EntityManager em, DynamicType type, Object id) {
        return (DynamicEntity) em.find(type.getJavaClass(), id);
    }
    
    @SuppressWarnings("unchecked")
    public List<DynamicEntity> readAllEmployeesUsing(EntityManager em) {
        return em.createQuery("SELECT e FROM Employee e ORDER BY e.id ASC").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<DynamicEntity> joinFetchEmployeeWithAddress(EntityManager em) {
        return em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address ORDER BY e.lastName ASC, e.firstName ASC").getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<DynamicEntity> joinFetchHint(EntityManager em) {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.manager.address.city = 'Ottawa' ORDER BY e.lastName ASC, e.firstName ASC");
        query.setHint(QueryHints.FETCH, "e.address");
        query.setHint(QueryHints.FETCH, "e.manager");
        query.setHint(QueryHints.FETCH, "e.manager.address");
        query.setHint(QueryHints.BATCH, "e.manager.phoneNumbers");
        List<DynamicEntity> emps = query.getResultList();

        for (DynamicEntity emp : emps) {
            emp.<DynamicEntity>get("manager").<Collection>get("phoneNumbers").size();
        }

        return emps;
    }

    public int minimumEmployeeId(EntityManager em) {
        return ((Number) em.createQuery("SELECT MIN(e.id) FROM Employee e").getSingleResult()).intValue();
    }

    public DynamicEntity minimumEmployee(EntityManager em) {
        Query q = em.createQuery("SELECT e FROM Employee e WHERE e.id in (SELECT MIN(ee.id) FROM Employee ee)");

        return (DynamicEntity) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<DynamicEntity> findEmployeesUsingGenderIn(EntityManager em) {
        return em.createQuery("SELECT e FROM Employee e WHERE e.gender IN (:GENDER1, :GENDER2)").setParameter("GENDER1", "Male").setParameter("GENDER2", "Female").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<DynamicEntity> findUsingNativeReadAllQuery(EntityManager em) {
        ClassDescriptor descriptor = JpaHelper.getEntityManager(em).getServerSession().getDescriptorForAlias("Employee");
        ReadAllQuery raq = new ReadAllQuery(descriptor.getJavaClass());
        ExpressionBuilder eb = raq.getExpressionBuilder();
        raq.setSelectionCriteria(eb.get("gender").equal("Male"));

        Query query = JpaHelper.createQuery(raq, em);

        return query.getResultList();
    }

    public DynamicEntity minEmployeeWithAddressAndPhones(EntityManager em) {
        return (DynamicEntity) em.createQuery("SELECT e FROM Employee e JOIN FETCH e.address WHERE e.id IN (SELECT MIN(p.id) FROM PhoneNumber p)").getSingleResult();
    }
}
