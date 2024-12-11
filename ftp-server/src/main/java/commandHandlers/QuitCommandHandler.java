package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;
import service.AuthenticationService;

import java.io.IOException;

public class QuitCommandHandler extends BaseCommandHandler{
    private final AuthenticationService authenticationService = AuthenticationService.getAuthenticationService();

    @Override
    protected boolean authorize(User user) {
        return true;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        authenticationService.signOut(user.getUsername());
        return new FtpResponse(221, "Service closing control connection");
    }
}
