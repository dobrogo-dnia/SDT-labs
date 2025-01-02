package commandHandlers;

import model.File;
import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpResponse;
import visitor.CreateVisitor;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorCommandHandler extends BaseCommandHandler {
    private final Socket dataSocket;
    private final String currentDir;
    private final FtpServer ftpServer;

    public StorCommandHandler(Socket dataSocket, String currentDir, FtpServer ftpServer) {
        this.dataSocket = dataSocket;
        this.currentDir = currentDir;
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

        String filePath = getFilePathFromArgs(arguments);

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
             InputStream inputStream = dataSocket.getInputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            long startTime = System.currentTimeMillis();
            int userSpeedLimit = ftpServer.getController().getUserSpeedLimit(user.getUsername());
            int bytesTransferred = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                bytesTransferred += bytesRead;
                enforceSpeedLimit(userSpeedLimit, bytesTransferred / 1024L, startTime);
            }

            bufferedOutputStream.flush();
            System.out.println("File received successfully: " + filePath);

            createFileRecordInDb(user, filePath, "644");

            return new FtpResponse(226, "File transfer complete");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during STOR: " + e.getMessage());
            return new FtpResponse(451, "Error during file upload");
        } finally {
            if (!dataSocket.isClosed()) {
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

    private String getFilePathFromArgs(String arguments) {
        Path argumentPath = Paths.get(arguments);
        if (argumentPath.isAbsolute()) {
            return argumentPath.normalize().toString();
        } else {
            return Paths.get(currentDir, arguments).normalize().toString();
        }
    }

    private void createFileRecordInDb(User user, String filePath, String permissions) {
        try {
            String fileName = Paths.get(filePath).getFileName().toString();
            String fileLocation = Paths.get(filePath).getParent().toString();

            File newFile = new File(fileName, fileLocation, user, permissions);

            newFile.accept(new CreateVisitor());

            System.out.println("File record created in database: " + newFile);
        } catch (Exception e) {
            System.err.println("Error while creating file record in database: " + e.getMessage());
        }
    }
}
