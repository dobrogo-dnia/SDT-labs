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
        return user != null && user.getIsAdmin();
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments == null)
            return new FtpResponse(501, "Syntax error in parameters or arguments");

        File adminFolder = new File(user.getHomeDirectory());
        File basicDir = new File(adminFolder.getParent());

        File newDir;
        if(arguments.split("\\\\").length == 1)
            newDir = new File(basicDir, arguments);
        else
            newDir = new File(arguments);

        if(newDir.exists() && newDir.isDirectory()){
            currentDirectoryPath.setLength(0);
            currentDirectoryPath.append(newDir.getAbsolutePath());
            return new FtpResponse(250, "Directory successfully changed");
        } else
            return new FtpResponse(550, "Failed to change directory");
    }
}
