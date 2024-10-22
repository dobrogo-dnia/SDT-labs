package repository.implementations;

import jakarta.persistence.*;
import model.Session;
import repository.interfaces.SessionRepository;

import java.util.List;

public class SessionRepositoryImpl implements SessionRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    public SessionRepositoryImpl(){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public List<Session> getByUserId(int userId) {
        TypedQuery<Session> query = entityManager.createQuery(
                "SELECT s FROM Session s WHERE s.user.userId = :userId", Session.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public Session createSession(Session session) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(session);
        transaction.commit();

        return session;
    }

    @Override
    public Session updateSessionStatus(int sessionId) {
        Session session = entityManager.find(Session.class, sessionId);
        if(session != null) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            session.setIsActive(false);
            session = entityManager.merge(session);
            transaction.commit();
        }
        return session;
    }

    @Override
    public List<Session> getActiveSessions() {
        String queryText = "SELECT s FROM Session s WHERE s.isActive = true";
        TypedQuery<Session> query = entityManager.createQuery(queryText, Session.class);
        return query.getResultList();
    }

    @Override
    public Session getActiveSessionForUser(int userId) {
        try {
            String query = "SELECT s FROM Session s WHERE s.user.id = :userId AND s.isActive = true";
            List<Session> sessions = entityManager.createQuery(query, Session.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getResultList();

            return sessions.get(0);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public void deleteSession(int sessionId) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Session session = entityManager.find(Session.class, sessionId);
        if(session != null)
            entityManager.remove(session);
        transaction.commit();
    }
}