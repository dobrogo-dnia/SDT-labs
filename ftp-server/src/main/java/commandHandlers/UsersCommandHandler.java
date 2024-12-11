package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;
import service.UserService;
import view.UI;

import java.io.IOException;
import java.util.List;

public class UsersCommandHandler extends BaseCommandHandler{
    private final UserService userService = UserService.getUserService();
    private final UI ui;

    public UsersCommandHandler(UI ui) {
        this.ui = ui;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null && user.getIsAdmin();
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        List<User> users = userService.getAllUsers();
        ui.displayUsersList(users);
        return new FtpResponse(212, "List of users successfully retrieved");
    }
}
