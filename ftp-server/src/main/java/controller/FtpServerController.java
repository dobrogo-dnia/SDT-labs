package controller;

import model.File;
import model.Session;
import model.User;
import service.AuthenticationService;
import service.FileService;
import service.SessionService;
import service.UserService;

import java.util.List;

public class FtpServerController {
    private final UserService userService;
    private final SessionService sessionService;
    private final FileService fileService;
    private final AuthenticationService authenticationService;

    public FtpServerController() {
        this.sessionService = new SessionService();
        this.fileService = new FileService();
        this.userService = new UserService();
        this.authenticationService = new AuthenticationService(userService, sessionService);
    }

    public void processLogin(String username, String password) {
        authenticationService.authenticate(username, password);
    }

    public void processSignOut(String username) {
        authenticationService.signOut(username);
    }

    public User createUserByAdmin(String adminUsername, String username, String password, boolean isAdmin, String homeLocation) {
        User currentUser = userService.getByUsername(adminUsername);
        if (currentUser != null || currentUser.getIsAdmin()) {
            User newUser = new User(username, password, isAdmin, homeLocation);
            return userService.createUser(newUser);
        }
        return null;
    }

    public List<File> getAllUserFiles(String username) {
        User user = userService.getByUsername(username);
        List<File> files = fileService.getAllUserFiles(user);
        return files;
    }

    public List<Session> getAllSessionsForUser(String username) {
        User user = userService.getByUsername(username);
        List<Session> sessions = sessionService.getAllSessionsForUser(user);
        return sessions;
    }

}