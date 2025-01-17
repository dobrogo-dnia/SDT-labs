package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;
import service.FileService;
import visitor.DeleteVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class DeleCommandHandler extends BaseCommandHandler {
    private final String currentDirectoryPath;
    private final FileService fileService = FileService.getFileService();

    public DeleCommandHandler(String currentDirectoryPath) {
        this.currentDirectoryPath = currentDirectoryPath;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if (arguments == null || arguments.isEmpty()) {
            return new FtpResponse(501, "Syntax error in parameters or arguments");
        }

        String filePath = getFilePathFromArgs(arguments);
        File fileToDelete = new File(filePath);

        if (!fileToDelete.exists()) {
            return new FtpResponse(404, "File not found");
        }

        if (fileToDelete.isDirectory()) {
            return new FtpResponse(550, "Permission denied");
        }

        model.File fileModel = fileService.getFileByUserAndFileName(user, fileToDelete.getName());
        if (fileModel == null) {
            return new FtpResponse(404, "File not found in database");
        }

        if (!hasWritePermission(user, fileModel)) {
            return new FtpResponse(550, "Permission denied");
        }

        boolean deletedSuccessfully = fileToDelete.delete();
        if (deletedSuccessfully) {
            fileModel.accept(new DeleteVisitor());
            return new FtpResponse(204, "File deleted successfully");
        } else {
            return new FtpResponse(409, "File could not be deleted");
        }
    }

    private String getFilePathFromArgs(String arguments) {
        String[] argumentsSplit = arguments.split("\\\\");

        if (argumentsSplit.length == 1) {
            return Paths.get(currentDirectoryPath, arguments).toAbsolutePath().toString();
        } else {
            return arguments;
        }
    }

    private boolean hasWritePermission(User user, model.File file) {
        String permissions = file.getPermissions();
        if (permissions == null || permissions.length() != 3) {
            return false;
        }

        boolean isOwner = file.getOwner().equals(user);

        int ownerPermissions = Character.getNumericValue(permissions.charAt(0));
        int othersPermissions = Character.getNumericValue(permissions.charAt(2));

        boolean hasFileWritePermission = isOwner
                ? (ownerPermissions & 2) != 0
                : (othersPermissions & 2) != 0;

        File parentDir = new File(file.getLocation()).getParentFile();
        if (parentDir != null && !parentDir.canWrite()) {
            return false;
        }

        return hasFileWritePermission;
    }
}
