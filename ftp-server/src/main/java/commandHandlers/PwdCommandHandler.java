package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;

public class PwdCommandHandler extends BaseCommandHandler{
    private final String currentDirectory;

    public PwdCommandHandler(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        return new FtpResponse(257, currentDirectory + "\\ is the current directory");
    }
}
