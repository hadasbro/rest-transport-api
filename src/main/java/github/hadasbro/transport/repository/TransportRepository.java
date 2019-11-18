package github.hadasbro.transport.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@SuppressWarnings("unused")
public class TransportRepository {

    @PersistenceContext(name = "EntityManagerFactoryBb2")
    @Qualifier( "entityManagerBb2")
    private EntityManager emExternalDB1;

    @PersistenceContext(name = "default")
    @Qualifier( "entityManager")
    private EntityManager em;

    @Resource
    public JourneylegRepository journeylegRepository;

    @Resource
    public JourneyRepository journeyRepository;

    @Resource
    public ActionRepository actionRepository;

}
