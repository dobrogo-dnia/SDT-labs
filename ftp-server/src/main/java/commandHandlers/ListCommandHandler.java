package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class ListCommandHandler extends BaseCommandHandler{
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
        PrintWriter dataOutput = new PrintWriter(dataSocket.getOutputStream(), true);

        File directory = new File(currentDirectory);
        File[] files = directory.listFiles();

        for(File file : files)
            dataOutput.println(getFileInfo(file));

        dataOutput.close();
        dataSocket.close();

        return new FtpResponse(226, "Transfer complete");
    }

    private String getFileInfo(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");

        String date = sdf.format(file.lastModified());
        String size = (file.isDirectory() ? "<DIR>" : String.valueOf(file.length()));
        String name = file.getName();

        return String.format("%s %10s %s", date, size, name);
    }
}
