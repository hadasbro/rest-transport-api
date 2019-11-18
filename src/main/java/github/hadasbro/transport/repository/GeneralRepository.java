package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.EntityTag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
@SuppressWarnings("unused")
public class GeneralRepository {

    @PersistenceContext(name = "default")
    @Qualifier("entityManager")
    private EntityManager em;

    /**
     * truncateDatatable
     *
     * @param modelClass -
     */
    public void truncateDatatable(Class<?> modelClass) {

        org.hibernate.Session hibernateSession = em.unwrap(org.hibernate.Session.class);
        String table = EntityTag.getTable(modelClass);
        String q1 = String.format("Truncate %s", table);
        Query query = hibernateSession.createNativeQuery(q1);
        query.executeUpdate();

    }

}
