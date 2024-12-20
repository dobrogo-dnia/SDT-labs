package commandHandlers;

import myFtpServer.protocol.FtpResponse;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class SiteChmodCommandHandler extends BaseCommandHandler {

    private static final Map<String, String> filePermissions = new HashMap<>();

    @Override
    protected boolean authorize(User user) {
        return user != null && user.getIsAdmin();
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return new FtpResponse(501, "Syntax error: SITE CHMOD <permissions> <filename>");
        }

        String[] args = arguments.trim().split("\\s+");
        if (args.length != 2) {
            return new FtpResponse(501, "Syntax error: SITE CHMOD <permissions> <filename>");
        }

        String permissions = args[0];
        String fileName = args[1];

        if (!permissions.matches("[0-7]{3}")) {
            return new FtpResponse(501, "Invalid permissions format: must be 3 digits (e.g., 644).");
        }

        filePermissions.put(fileName, permissions);

        return new FtpResponse(200, "Permissions for " + fileName + " set to " + permissions);
    }

    public static String getFilePermissions(String fileName) {
        return filePermissions.getOrDefault(fileName, "644");
    }
}
