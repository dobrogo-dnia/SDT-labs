package repository.interfaces;

import model.Session;

import java.util.List;

public interface SessionRepository {
    List<Session> getByUserId(int userId);
    Session createSession(Session session);
    Session updateSessionStatus(int sessionId);
    List<Session> getActiveSessions();
    Session getActiveSessionForUser(int userId);
    void deleteSession(int sessionId);
}
