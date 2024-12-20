package commandHandlers;

import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpResponse;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RetrieveCommandHandler extends BaseCommandHandler {
    private final Socket dataSocket;
    private final String currentDirectory;
    private final FtpServer ftpServer;

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

        String filePath = getFilePath(arguments);
        java.io.File file = new java.io.File(filePath);

        if (!file.exists() || !file.isFile()) {
            return new FtpResponse(550, "File not found or is not a valid file");
        }

        System.out.println("RETR command received. Sending file: " + filePath);

        try (BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream dataOutput = new BufferedOutputStream(dataSocket.getOutputStream());
             Socket socket = dataSocket) {

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
            System.out.println("File sent successfully: " + filePath);
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

    private String getFilePath(String arguments) {
        Path argumentPath = Paths.get(arguments);
        if (argumentPath.isAbsolute()) {
            return argumentPath.normalize().toString();
        } else {
            return Paths.get(currentDirectory, arguments).normalize().toString();
        }
    }
}
