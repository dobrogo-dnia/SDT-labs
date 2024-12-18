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
        if (user != null) {
            sessionService.getByUserId(user.getUserId())
                    .forEach(session -> sessionService.deleteSession(session.getSessionId()));

            fileService.getByUserId(user.getUserId())
                    .forEach(file -> fileService.deleteFile(file.getFileId()));

            userService.deleteById(user.getUserId());
        }
    }

    @Override
    public void visit(File file) {
        if (file != null && file.getFileId() > 0) {
            System.out.println("Deleting file from DB with ID: " + file.getFileId());
            fileService.deleteById(file.getFileId());
        } else {
            System.out.println("File is null or has invalid ID, skipping deletion.");
        }
    }

    @Override
    public void visit(Session session) {
        if(session != null)
            sessionService.deleteSession(session.getSessionId());
    }
}
