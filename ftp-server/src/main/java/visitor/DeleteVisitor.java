package visitor;

import model.File;
import model.Session;
import model.User;
import service.FileService;
import service.SessionService;
import service.UserService;

public class DeleteVisitor implements Visitor{
    private final UserService userService = UserService.getUserService();
    private final FileService fileService = FileService.getFileService();
    private final SessionService sessionService = SessionService.getSessionService();

    @Override
    public void visit(User user) {
        if(user != null)
            userService.deleteById(user.getUserId());
    }

    @Override
    public void visit(File file) {
        if(file != null)
            fileService.deleteFile(file.getFileId());
    }

    @Override
    public void visit(Session session) {
        if(session != null)
            sessionService.deleteSession(session.getSessionId());
    }
}
