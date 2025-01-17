package commandHandlers;

import model.User;
import myFtpServer.ftpServerStates.UserLoggedInServerState;
import myFtpServer.protocol.FtpResponse;
import service.UserService;
import userMemento.UserMemento;

import java.io.IOException;

public class RestoreCommandHandler extends BaseCommandHandler{
    private final UserService userService = UserService.getUserService();

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        UserMemento restoredUserMemento = UserLoggedInServerState.getUserCaretaker(user.getUserId()).getLastSavedMemento();
        if(restoredUserMemento == null)
            return new FtpResponse(202, "No action taken, user state is current");

        user.restoreFromMemento(restoredUserMemento);
        userService.updateUser(user);
        return new FtpResponse(250, "User state restored to the latest one");
    }
}
