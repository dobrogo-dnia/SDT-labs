package commandHandlers;

import myFtpServer.protocol.FtpResponse;
import model.User;
import model.File;
import service.FileService;

public class SiteChmodCommandHandler extends BaseCommandHandler {

    private final FileService fileService = FileService.getFileService();

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
            return new FtpResponse(501, "Invalid permissions format: must be 3 digits (e.g., 644)");
        }

        File file = fileService.getFileByUserAndFileName(user, fileName);
        if (file == null) {
            return new FtpResponse(550, "File not found: " + fileName);
        }

        file.setPermissions(permissions);
        fileService.updateFile(file);

        return new FtpResponse(200, "Permissions for " + fileName + " set to " + permissions);
    }
}
