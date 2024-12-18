package commandHandlers;

import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpResponse;
import model.User;

public class SiteSpeedGlobalCommandHandler extends BaseCommandHandler {
    private final FtpServer ftpServer;

    public SiteSpeedGlobalCommandHandler(FtpServer ftpServer) {
        this.ftpServer = ftpServer;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null && user.getIsAdmin();
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) {
        if (arguments == null || arguments.isEmpty()) {
            return new FtpResponse(501, "Syntax error in parameters or arguments.");
        }

        try {
            int globalSpeedLimit = Integer.parseInt(arguments.trim());
            if (globalSpeedLimit <= 0) {
                return new FtpResponse(501, "Speed limit must be a positive integer.");
            }

            ftpServer.getController().setGlobalSpeedLimit(globalSpeedLimit);
            return new FtpResponse(200, "Global speed limit set to " + globalSpeedLimit + " KB/s.");
        } catch (NumberFormatException e) {
            return new FtpResponse(501, "Invalid speed limit format. Please provide an integer value.");
        }
    }
}
