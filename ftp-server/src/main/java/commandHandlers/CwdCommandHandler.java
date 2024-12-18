package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.File;
import java.io.IOException;

public class CwdCommandHandler extends BaseCommandHandler{
    private final StringBuilder currentDirectoryPath;

    public CwdCommandHandler(StringBuilder currentDirectoryPath) {
        this.currentDirectoryPath = currentDirectoryPath;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments == null)
            return new FtpResponse(501, "Syntax error in parameters or arguments");

        File homeDir = new File(user.getHomeDirectory()).getCanonicalFile();
        File currentDir = new File(currentDirectoryPath.toString());
        File newDir;

        if (new File(arguments).isAbsolute()) {
            newDir = new File(arguments);
        } else {
            newDir = new File(currentDir, arguments);
        }

        if (!newDir.getAbsolutePath().startsWith(homeDir.getAbsolutePath())) {
            return new FtpResponse(550, "Access denied: Cannot go above home directory");
        }

        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectoryPath.setLength(0);
            currentDirectoryPath.append(newDir.getAbsolutePath());
            return new FtpResponse(250, "Directory successfully changed");
        } else
            return new FtpResponse(550, "Failed to change directory");
    }
}
