package visitor;

import model.File;
import model.Session;
import model.User;
import service.FileService;
import service.SessionService;
import service.UserService;

public class CreateVisitor implements Visitor {
    private final UserService userService = UserService.getUserService();
    private final FileService fileService = FileService.getFileService();
    private final SessionService sessionService = SessionService.getSessionService();

    @Override
    public void visit(User user) {
        userService.createUser(user);
    }

    @Override
    public void visit(File file) {
        fileService.createFile(file);
    }

    @Override
    public void visit(Session session) {
        sessionService.createSession(session);
    }
}
