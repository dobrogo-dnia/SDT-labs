package service;

import logger.Logger;
import model.Session;
import model.User;
import visitor.CreateVisitor;

import java.io.IOException;
import java.net.Socket;

public class AuthenticationService {
    private final UserService userService = UserService.getUserService();
    private final SessionService sessionService = SessionService.getSessionService();
    private static AuthenticationService authenticationService;

    private AuthenticationService() { }

    public static AuthenticationService getAuthenticationService() {
        if(authenticationService == null)
            authenticationService = new AuthenticationService();
        return authenticationService;
    }

    public boolean userExists(String username) {
        User user = userService.getByUsername(username);
        return user != null;
    }

    public boolean userAllowedToHaveNewSession(String username) {
        User user = userService.getByUsername(username);
        if(user.getIsAdmin())
            return true;

        return sessionService.getActiveSessionForUser(user.getUserId()) == null;
    }

    public User authenticate(String username, String password, Logger logger, Socket clientSocket) throws IOException {
        User currentUser = userService.getByUsername(username);
        if(currentUser != null && currentUser.getPassword().equals(password)){
            if (sessionService.getActiveSessionForUser(currentUser.getUserId()) == null){
                Session newSession = new Session(currentUser);
                newSession.accept(new CreateVisitor());
                logger.writeLogInEventToFile(clientSocket.getInetAddress().getHostAddress(), currentUser.getUsername());
            }
            return currentUser;
        }
        return null;
    }

    public void signOut(String username) {
        User user = userService.getByUsername(username);
        Session activeSession = sessionService.getActiveSessionForUser(user.getUserId());
        if(activeSession != null)
            sessionService.modifySessionStatus(activeSession.getSessionId());
    }
}
