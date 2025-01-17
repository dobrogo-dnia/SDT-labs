package service;

import model.Session;
import model.User;
import repository.implementations.SessionRepositoryImpl;
import repository.interfaces.SessionRepository;

import java.util.Collections;
import java.util.List;

public class SessionService {
    private final SessionRepository sessionRepository;
    private static SessionService sessionService;

    private SessionService() {
        this.sessionRepository = new SessionRepositoryImpl();
    }

    public static SessionService getSessionService() {
        if(sessionService == null)
            sessionService = new SessionService();
        return sessionService;
    }

    public List<Session> getByUserId(int userId) {
        return sessionRepository.getByUserId(userId);
    }

    public void createSession(Session session) {
        sessionRepository.createSession(session);
    }

    public List<Session> getActiveSessions() { return sessionRepository.getActiveSessions(); }

    public Session getActiveSessionForUser(int userId) { return sessionRepository.getActiveSessionForUser(userId); }

    public List<Session> getAllSessionsForUser(User user) {
        if(user != null)
            return sessionRepository.getByUserId(user.getUserId());
        return Collections.emptyList();
    }

    public void modifySessionStatus(int sessionId) {
        sessionRepository.updateSessionStatus(sessionId);
    }

    public void deleteSession(int sessionId) {
        sessionRepository.deleteSession(sessionId);
    }
}
