package commandHandlers;

import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpResponse;
import model.User;


public class SiteSpeedUserCommandHandler extends BaseCommandHandler {
    private final FtpServer ftpServer;

    public SiteSpeedUserCommandHandler(FtpServer ftpServer) {
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

        String[] args = arguments.split(" ", 2);
        if (args.length != 2) {
            return new FtpResponse(501, "Invalid SITE SPEED USER command format. Use: SITE SPEED USER <username> <limit>.");
        }

        String targetUsername = args[0].trim();
        try {
            int userSpeedLimit = Integer.parseInt(args[1].trim());
            if (userSpeedLimit <= 0) {
                return new FtpResponse(501, "Speed limit must be a positive integer.");
            }

            boolean success = ftpServer.getController().setUserSpeedLimit(targetUsername, userSpeedLimit);
            if (success) {
                return new FtpResponse(200, "Speed limit for user " + targetUsername + " set to " + userSpeedLimit + " KB/s.");
            } else {
                return new FtpResponse(550, "Failed to set speed limit. User not found or error occurred.");
            }
        } catch (NumberFormatException e) {
            return new FtpResponse(501, "Invalid speed limit format. Please provide an integer value.");
        }
    }
}
