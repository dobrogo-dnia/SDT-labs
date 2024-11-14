package service;

import model.Session;
import model.User;

public class AuthenticationService {
    private final UserService userService;
    private final SessionService sessionService;

    public AuthenticationService(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public User authenticate(String username, String password) {
        User currentUser = userService.getByUsername(username);
        if (currentUser != null && currentUser.getPassword().equals(password)) {
            Session newSession = new Session(currentUser);
            sessionService.createSession(newSession);
            return currentUser;
        }
        return null;
    }

    public void signOut(String username) {
        UserService userService = new UserService();
        User user = userService.getByUsername(username);
        Session activeSession = sessionService.getActiveSessionForUser(user.getUserId());
        if (activeSession != null)
            sessionService.modifySessionStatus(activeSession.getSessionId());
    }

    public boolean userExists(String username) {
        User user = userService.getByUsername(username);
        return user != null;
    }

}