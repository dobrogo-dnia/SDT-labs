package commandHandlers;

import model.File;
import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpResponse;
import service.FileService;

import java.io.*;
import java.net.Socket;

public class RetrieveCommandHandler extends BaseCommandHandler {
    private final Socket dataSocket;
    private final String currentDirectory;
    private final FtpServer ftpServer;
    private final FileService fileService = FileService.getFileService();

    public RetrieveCommandHandler(Socket dataSocket, String currentDirectory, FtpServer ftpServer) {
        this.dataSocket = dataSocket;
        this.currentDirectory = currentDirectory;
        this.ftpServer = ftpServer;
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

        File file = fileService.getFileByUserAndFileName(user, arguments);
        if (file == null) {
            return new FtpResponse(550, "File not found");
        }

        if (!canUserReadFile(user, file)) {
            return new FtpResponse(550, "Permission denied");
        }

        java.io.File systemFile = new java.io.File(file.getLocation(), file.getName());
        if (!systemFile.exists() || !systemFile.isFile()) {
            return new FtpResponse(550, "File not found or is not a valid file");
        }

        System.out.println("RETR command received. Sending file: " + systemFile.getPath());

        try (BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(systemFile));
             BufferedOutputStream dataOutput = new BufferedOutputStream(dataSocket.getOutputStream())) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long startTime = System.currentTimeMillis();
            int userSpeedLimit = ftpServer.getController().getUserSpeedLimit(user.getUsername());
            int globalSpeedLimit = ftpServer.getController().getGlobalSpeedLimit();
            int effectiveSpeedLimit = (globalSpeedLimit > 0) ? Math.min(userSpeedLimit, globalSpeedLimit) : userSpeedLimit;

            int bytesTransferred = 0;

            while ((bytesRead = fileInput.read(buffer)) != -1) {
                dataOutput.write(buffer, 0, bytesRead);
                bytesTransferred += bytesRead;

                enforceSpeedLimit(effectiveSpeedLimit, bytesTransferred / 1024L, startTime);
            }

            dataOutput.flush();
            dataSocket.shutdownOutput();
            System.out.println("File sent successfully: " + systemFile.getPath());
            return new FtpResponse(226, "File transfer complete");

        } catch (IOException | InterruptedException e) {
            System.err.println("Error during RETR: " + e.getMessage());
            return new FtpResponse(550, "Failed to send file");
        } finally {
            if (dataSocket != null && !dataSocket.isClosed()) {
                dataSocket.close();
                System.out.println("Data socket closed");
            }
        }
    }

    private void enforceSpeedLimit(int speedLimitKBps, long bytesTransferred, long startTime) throws InterruptedException {
        if (speedLimitKBps > 0) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long expectedTime = (bytesTransferred * 1000) / speedLimitKBps;
            if (elapsedTime < expectedTime) {
                Thread.sleep(expectedTime - elapsedTime);
            }
        }
    }

    private boolean canUserReadFile(User user, File file) {
        String permissions = file.getPermissions();
        boolean isOwner = file.getOwner().equals(user);

        return isOwner
                ? (Character.getNumericValue(permissions.charAt(0)) & 4) != 0
                : (Character.getNumericValue(permissions.charAt(2)) & 4) != 0;
    }
}
