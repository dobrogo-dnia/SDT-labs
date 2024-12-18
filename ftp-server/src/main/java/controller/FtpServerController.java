package controller;

import logger.Logger;
import model.Session;
import model.User;
import service.*;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FtpServerController {
    private final SessionService sessionService = SessionService.getSessionService();
    private final AuthenticationService authenticationService = AuthenticationService.getAuthenticationService();

    public boolean newUserCanConnect(int maxConnectionNum) {
        List<Session> activeSessions = sessionService.getActiveSessions();
        int activeSessionsNum = activeSessions.size();
        return activeSessionsNum < maxConnectionNum;
    }

    public boolean checkIfUserExist(String username) {
        try {
            return authenticationService.userExists(username);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public User processLogin(String username, String password, Logger logger, Socket clientSocket) throws IOException {
        if (!authenticationService.userExists(username)) {
            logger.writeErrorEventToFile(clientSocket.getInetAddress().toString(), username, "User does not exist");
            return null;
        }

        User user = authenticationService.authenticate(username, password, logger, clientSocket);
        if (user == null) {
            logger.writeErrorEventToFile(clientSocket.getInetAddress().toString(), username, "Authentication failed");
        }
        return user;
    }

    public void processLogOut(String username) {
        if (authenticationService.userExists(username)) {
            authenticationService.signOut(username);
        }
    }

    public void deactivateSessionIfActive(int userId) {
        Session activeSession = sessionService.getActiveSessionForUser(userId);
        if(activeSession != null)
            sessionService.modifySessionStatus(activeSession.getSessionId());
    }

    private final Map<String, Integer> userSpeedLimits = Collections.synchronizedMap(new HashMap<>());

    private int globalSpeedLimit = 0;

    public void setGlobalSpeedLimit(int limit) {
        this.globalSpeedLimit = limit;
        System.out.println("Global speed limit set to " + limit + " KB/s.");
    }

    public int getGlobalSpeedLimit() {
        return globalSpeedLimit;
    }

    public boolean setUserSpeedLimit(String username, int limit) {
        if (checkIfUserExist(username)) {
            userSpeedLimits.put(username, limit);
            System.out.println("Speed limit for user " + username + " set to " + limit + " KB/s.");
            return true;
        }
        return false;
    }

    public int getUserSpeedLimit(String username) {
        return userSpeedLimits.getOrDefault(username, globalSpeedLimit);
    }
}
