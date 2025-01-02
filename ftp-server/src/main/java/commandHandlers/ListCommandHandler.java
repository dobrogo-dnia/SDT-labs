package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class ListCommandHandler extends BaseCommandHandler {
    private final Socket dataSocket;
    private final String currentDirectory;

    public ListCommandHandler(Socket dataSocket, String currentDirectory) {
        this.dataSocket = dataSocket;
        this.currentDirectory = currentDirectory;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        try (OutputStream outputStream = dataSocket.getOutputStream();
             PrintWriter dataOutput = new PrintWriter(outputStream, true)) {

            File directory = new File(currentDirectory);
            File[] files = directory.listFiles();

            if (files == null || files.length == 0) {
                dataOutput.println("Directory is empty");
            } else {
                for (File file : files) {
                    dataOutput.println(getFileInfo(file));
                }
            }

            dataOutput.flush();
            dataSocket.shutdownOutput();
            System.out.println("Directory listing sent successfully");

            return new FtpResponse(226, "Transfer complete");
        } catch (IOException e) {
            System.err.println("Error during LIST command: " + e.getMessage());
            return new FtpResponse(425, "Data connection failed");
        } finally {
            System.out.println("Data connection not forcibly closed");
        }
    }

    private String getFileInfo(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String date = sdf.format(file.lastModified());
        String size = file.isDirectory() ? "<DIR>" : String.valueOf(file.length());
        String name = file.getName();

        return String.format("%-20s %10s %s", date, size, name);
    }
}
