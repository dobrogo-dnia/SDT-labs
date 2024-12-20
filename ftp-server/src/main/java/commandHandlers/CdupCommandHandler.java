package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CdupCommandHandler extends BaseCommandHandler{
    private final StringBuilder currentDirectoryPath;

    public CdupCommandHandler(StringBuilder currentDirectoryPath){
        this.currentDirectoryPath = currentDirectoryPath;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        Path currentDirectory = Paths.get(currentDirectoryPath.toString()).toAbsolutePath();
        Path parentDirectory = currentDirectory.getParent();
        if(parentDirectory != null) {
            currentDirectoryPath.setLength(0);
            currentDirectoryPath.append(parentDirectory.toAbsolutePath());
            return new FtpResponse(200, "Changed to parent directory");
        } else
            return new FtpResponse(550, "Failed to change directory: No parent directory");
    }
}
